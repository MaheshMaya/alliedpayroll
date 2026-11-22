// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'set_module_request.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$SetModuleRequestCWProxy {
  SetModuleRequest enabled(bool enabled);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SetModuleRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SetModuleRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  SetModuleRequest call({bool enabled});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfSetModuleRequest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfSetModuleRequest.copyWith.fieldName(...)`
class _$SetModuleRequestCWProxyImpl implements _$SetModuleRequestCWProxy {
  const _$SetModuleRequestCWProxyImpl(this._value);

  final SetModuleRequest _value;

  @override
  SetModuleRequest enabled(bool enabled) => this(enabled: enabled);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SetModuleRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SetModuleRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  SetModuleRequest call({Object? enabled = const $CopyWithPlaceholder()}) {
    return SetModuleRequest(
      enabled: enabled == const $CopyWithPlaceholder()
          ? _value.enabled
          // ignore: cast_nullable_to_non_nullable
          : enabled as bool,
    );
  }
}

extension $SetModuleRequestCopyWith on SetModuleRequest {
  /// Returns a callable class that can be used as follows: `instanceOfSetModuleRequest.copyWith(...)` or like so:`instanceOfSetModuleRequest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$SetModuleRequestCWProxy get copyWith => _$SetModuleRequestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SetModuleRequest _$SetModuleRequestFromJson(Map<String, dynamic> json) =>
    $checkedCreate('SetModuleRequest', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['enabled']);
      final val = SetModuleRequest(
        enabled: $checkedConvert('enabled', (v) => v as bool),
      );
      return val;
    });

Map<String, dynamic> _$SetModuleRequestToJson(SetModuleRequest instance) =>
    <String, dynamic>{'enabled': instance.enabled};
