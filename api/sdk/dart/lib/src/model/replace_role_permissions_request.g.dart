// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'replace_role_permissions_request.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ReplaceRolePermissionsRequestCWProxy {
  ReplaceRolePermissionsRequest permissions(Set<String> permissions);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReplaceRolePermissionsRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReplaceRolePermissionsRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  ReplaceRolePermissionsRequest call({Set<String> permissions});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfReplaceRolePermissionsRequest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfReplaceRolePermissionsRequest.copyWith.fieldName(...)`
class _$ReplaceRolePermissionsRequestCWProxyImpl
    implements _$ReplaceRolePermissionsRequestCWProxy {
  const _$ReplaceRolePermissionsRequestCWProxyImpl(this._value);

  final ReplaceRolePermissionsRequest _value;

  @override
  ReplaceRolePermissionsRequest permissions(Set<String> permissions) =>
      this(permissions: permissions);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReplaceRolePermissionsRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReplaceRolePermissionsRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  ReplaceRolePermissionsRequest call({
    Object? permissions = const $CopyWithPlaceholder(),
  }) {
    return ReplaceRolePermissionsRequest(
      permissions: permissions == const $CopyWithPlaceholder()
          ? _value.permissions
          // ignore: cast_nullable_to_non_nullable
          : permissions as Set<String>,
    );
  }
}

extension $ReplaceRolePermissionsRequestCopyWith
    on ReplaceRolePermissionsRequest {
  /// Returns a callable class that can be used as follows: `instanceOfReplaceRolePermissionsRequest.copyWith(...)` or like so:`instanceOfReplaceRolePermissionsRequest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ReplaceRolePermissionsRequestCWProxy get copyWith =>
      _$ReplaceRolePermissionsRequestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ReplaceRolePermissionsRequest _$ReplaceRolePermissionsRequestFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ReplaceRolePermissionsRequest', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['permissions']);
  final val = ReplaceRolePermissionsRequest(
    permissions: $checkedConvert(
      'permissions',
      (v) => (v as List<dynamic>).map((e) => e as String).toSet(),
    ),
  );
  return val;
});

Map<String, dynamic> _$ReplaceRolePermissionsRequestToJson(
  ReplaceRolePermissionsRequest instance,
) => <String, dynamic>{'permissions': instance.permissions.toList()};
