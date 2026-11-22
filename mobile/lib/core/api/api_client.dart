import 'package:dio/dio.dart';
import 'package:uuid/uuid.dart';

abstract final class ApiHeaders {
  static const channel = 'X-Channel';
  static const appVersion = 'X-App-Version';
  static const idempotencyKey = 'Idempotency-Key';
  static const acceptLanguage = 'Accept-Language';
  static const authorization = 'Authorization';
}

const _retried = 'allied.retried';
const _mutations = {'POST', 'PUT', 'PATCH', 'DELETE'};

/// The Dio instance behind the generated client: the headers every call
/// carries, a bearer that is refreshed and retried once on 401, and an
/// Idempotency-Key on every mutation.
Dio buildApiDio({
  required Uri baseUrl,
  required Future<String?> Function() accessToken,
  required String Function() languageTag,
  required String appVersion,
  required void Function() onUnauthorized,
  String Function()? idempotencyKey,
}) {
  final newKey = idempotencyKey ?? const Uuid().v4;
  final dio = Dio(
    BaseOptions(
      baseUrl: baseUrl.toString(),
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 20),
    ),
  );
  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        // A retry already carries its headers and fresh bearer.
        if (options.extra[_retried] == true) return handler.next(options);
        options.headers[ApiHeaders.channel] = 'mobile';
        options.headers[ApiHeaders.appVersion] = appVersion;
        options.headers[ApiHeaders.acceptLanguage] = languageTag();
        if (_mutations.contains(options.method.toUpperCase())) {
          options.headers.putIfAbsent(ApiHeaders.idempotencyKey, newKey);
        }
        final token = await accessToken();
        if (token != null) {
          options.headers[ApiHeaders.authorization] = 'Bearer $token';
        }
        handler.next(options);
      },
      onError: (error, handler) async {
        final options = error.requestOptions;
        if (error.response?.statusCode != 401) return handler.next(error);
        if (options.extra[_retried] == true) {
          onUnauthorized();
          return handler.next(error);
        }
        final token = await accessToken();
        if (token == null) {
          onUnauthorized();
          return handler.next(error);
        }
        options
          ..extra[_retried] = true
          ..headers[ApiHeaders.authorization] = 'Bearer $token';
        try {
          handler.resolve(await dio.fetch<Object?>(options));
        } on DioException catch (retryError) {
          handler.next(retryError);
        }
      },
    ),
  );
  return dio;
}
