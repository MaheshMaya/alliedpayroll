//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

import 'dart:async';

// ignore: unused_import
import 'dart:convert';
import 'package:allied_api/src/deserialize.dart';
import 'package:dio/dio.dart';

import 'package:allied_api/src/model/page_response_audit_event_view.dart';

class AuditApi {
  final Dio _dio;

  const AuditApi(this._dio);

  /// Trail of the tenant, newest first; security rows need audit:security_read
  ///
  ///
  /// Parameters:
  /// * [filterLeftSquareBracketActionRightSquareBracket]
  /// * [filterLeftSquareBracketActorIdRightSquareBracket]
  /// * [filterLeftSquareBracketEntityTypeRightSquareBracket]
  /// * [filterLeftSquareBracketEntityIdRightSquareBracket]
  /// * [from]
  /// * [to]
  /// * [cursor]
  /// * [size]
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [PageResponseAuditEventView] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<PageResponseAuditEventView>> listAuditEvents({
    String? filterLeftSquareBracketActionRightSquareBracket,
    String? filterLeftSquareBracketActorIdRightSquareBracket,
    String? filterLeftSquareBracketEntityTypeRightSquareBracket,
    String? filterLeftSquareBracketEntityIdRightSquareBracket,
    DateTime? from,
    DateTime? to,
    String? cursor,
    int? size = 50,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/audit-events';
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{...?headers},
      extra: <String, dynamic>{'secure': <Map<String, String>>[], ...?extra},
      validateStatus: validateStatus,
    );

    final _queryParameters = <String, dynamic>{
      if (filterLeftSquareBracketActionRightSquareBracket != null)
        r'filter[action]': filterLeftSquareBracketActionRightSquareBracket,
      if (filterLeftSquareBracketActorIdRightSquareBracket != null)
        r'filter[actorId]': filterLeftSquareBracketActorIdRightSquareBracket,
      if (filterLeftSquareBracketEntityTypeRightSquareBracket != null)
        r'filter[entityType]':
            filterLeftSquareBracketEntityTypeRightSquareBracket,
      if (filterLeftSquareBracketEntityIdRightSquareBracket != null)
        r'filter[entityId]': filterLeftSquareBracketEntityIdRightSquareBracket,
      if (from != null) r'from': from,
      if (to != null) r'to': to,
      if (cursor != null) r'cursor': cursor,
      if (size != null) r'size': size,
    };

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      queryParameters: _queryParameters,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    PageResponseAuditEventView? _responseData;

    try {
      final rawData = _response.data;
      _responseData = rawData == null
          ? null
          : deserialize<PageResponseAuditEventView, PageResponseAuditEventView>(
              rawData,
              'PageResponseAuditEventView',
              growable: true,
            );
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<PageResponseAuditEventView>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }
}
