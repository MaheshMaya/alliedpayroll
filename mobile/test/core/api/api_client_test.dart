import 'dart:convert';
import 'dart:typed_data';

import 'package:allied_payroll/core/api/api_client.dart';
import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';

/// Answers every request from a queue of status codes and keeps what was sent.
class _Server implements HttpClientAdapter {
  _Server(this.statuses);

  final List<int> statuses;
  final requests = <RequestOptions>[];

  @override
  Future<ResponseBody> fetch(
    RequestOptions options,
    Stream<Uint8List>? requestStream,
    Future<void>? cancelFuture,
  ) async {
    requests.add(options);
    final status = statuses.removeAt(0);
    return ResponseBody.fromString(
      jsonEncode({'ok': status == 200}),
      status,
      headers: {
        Headers.contentTypeHeader: [Headers.jsonContentType],
      },
    );
  }

  @override
  void close({bool force = false}) {}
}

void main() {
  late List<String?> tokens;
  late int unauthorized;

  Dio dioWith(_Server server) {
    unauthorized = 0;
    return buildApiDio(
      baseUrl: Uri.parse('http://sentinel.localhost:8088'),
      accessToken: () async => tokens.removeAt(0),
      languageTag: () => 'ta-IN',
      appVersion: '0.1.0-test',
      onUnauthorized: () => unauthorized++,
      idempotencyKey: () => 'key-1',
    )..httpClientAdapter = server;
  }

  test('every call carries channel, version, language and bearer', () async {
    tokens = ['t1'];
    final server = _Server([200]);
    await dioWith(server).get<Object?>('/api/v1/mobile/bootstrap');

    final sent = server.requests.single.headers;
    expect(sent[ApiHeaders.channel], 'mobile');
    expect(sent[ApiHeaders.appVersion], '0.1.0-test');
    expect(sent[ApiHeaders.acceptLanguage], 'ta-IN');
    expect(sent[ApiHeaders.authorization], 'Bearer t1');
    expect(sent.containsKey(ApiHeaders.idempotencyKey), isFalse);
  });

  test('mutations get an Idempotency-Key, reads do not', () async {
    tokens = ['t1'];
    final server = _Server([200]);
    await dioWith(
      server,
    ).post<Object?>('/api/v1/mobile/sync/punches', data: {});
    expect(server.requests.single.headers[ApiHeaders.idempotencyKey], 'key-1');
  });

  test('a 401 is retried once with a fresh token', () async {
    tokens = ['stale', 'fresh'];
    final server = _Server([401, 200]);
    final response = await dioWith(server).get<Object?>('/api/v1/me');

    expect(response.statusCode, 200);
    expect(server.requests, hasLength(2));
    expect(
      server.requests.last.headers[ApiHeaders.authorization],
      'Bearer fresh',
    );
    expect(unauthorized, 0);
  });

  test('a second 401 gives up and reports it', () async {
    tokens = ['stale', 'fresh'];
    final server = _Server([401, 401]);
    await expectLater(
      dioWith(server).get<Object?>('/api/v1/me'),
      throwsA(isA<DioException>()),
    );
    expect(unauthorized, 1);
  });
}
