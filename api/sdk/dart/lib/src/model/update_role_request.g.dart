// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_role_request.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$UpdateRoleRequestCWProxy {
  UpdateRoleRequest name(String name);

  UpdateRoleRequest description(String? description);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UpdateRoleRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UpdateRoleRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  UpdateRoleRequest call({String name, String? description});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfUpdateRoleRequest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfUpdateRoleRequest.copyWith.fieldName(...)`
class _$UpdateRoleRequestCWProxyImpl implements _$UpdateRoleRequestCWProxy {
  const _$UpdateRoleRequestCWProxyImpl(this._value);

  final UpdateRoleRequest _value;

  @override
  UpdateRoleRequest name(String name) => this(name: name);

  @override
  UpdateRoleRequest description(String? description) =>
      this(description: description);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UpdateRoleRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UpdateRoleRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  UpdateRoleRequest call({
    Object? name = const $CopyWithPlaceholder(),
    Object? description = const $CopyWithPlaceholder(),
  }) {
    return UpdateRoleRequest(
      name: name == const $CopyWithPlaceholder()
          ? _value.name
          // ignore: cast_nullable_to_non_nullable
          : name as String,
      description: description == const $CopyWithPlaceholder()
          ? _value.description
          // ignore: cast_nullable_to_non_nullable
          : description as String?,
    );
  }
}

extension $UpdateRoleRequestCopyWith on UpdateRoleRequest {
  /// Returns a callable class that can be used as follows: `instanceOfUpdateRoleRequest.copyWith(...)` or like so:`instanceOfUpdateRoleRequest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$UpdateRoleRequestCWProxy get copyWith =>
      _$UpdateRoleRequestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UpdateRoleRequest _$UpdateRoleRequestFromJson(Map<String, dynamic> json) =>
    $checkedCreate('UpdateRoleRequest', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['name']);
      final val = UpdateRoleRequest(
        name: $checkedConvert('name', (v) => v as String),
        description: $checkedConvert('description', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$UpdateRoleRequestToJson(UpdateRoleRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'description': ?instance.description,
    };
