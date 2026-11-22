// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'relabel_request.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RelabelRequestCWProxy {
  RelabelRequest labels(Map<String, String> labels);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RelabelRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RelabelRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  RelabelRequest call({Map<String, String> labels});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRelabelRequest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRelabelRequest.copyWith.fieldName(...)`
class _$RelabelRequestCWProxyImpl implements _$RelabelRequestCWProxy {
  const _$RelabelRequestCWProxyImpl(this._value);

  final RelabelRequest _value;

  @override
  RelabelRequest labels(Map<String, String> labels) => this(labels: labels);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RelabelRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RelabelRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  RelabelRequest call({Object? labels = const $CopyWithPlaceholder()}) {
    return RelabelRequest(
      labels: labels == const $CopyWithPlaceholder()
          ? _value.labels
          // ignore: cast_nullable_to_non_nullable
          : labels as Map<String, String>,
    );
  }
}

extension $RelabelRequestCopyWith on RelabelRequest {
  /// Returns a callable class that can be used as follows: `instanceOfRelabelRequest.copyWith(...)` or like so:`instanceOfRelabelRequest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RelabelRequestCWProxy get copyWith => _$RelabelRequestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RelabelRequest _$RelabelRequestFromJson(Map<String, dynamic> json) =>
    $checkedCreate('RelabelRequest', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['labels']);
      final val = RelabelRequest(
        labels: $checkedConvert(
          'labels',
          (v) => Map<String, String>.from(v as Map),
        ),
      );
      return val;
    });

Map<String, dynamic> _$RelabelRequestToJson(RelabelRequest instance) =>
    <String, dynamic>{'labels': instance.labels};
