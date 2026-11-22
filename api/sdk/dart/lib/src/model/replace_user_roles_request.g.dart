// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'replace_user_roles_request.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ReplaceUserRolesRequestCWProxy {
  ReplaceUserRolesRequest roleIds(Set<String> roleIds);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReplaceUserRolesRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReplaceUserRolesRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  ReplaceUserRolesRequest call({Set<String> roleIds});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfReplaceUserRolesRequest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfReplaceUserRolesRequest.copyWith.fieldName(...)`
class _$ReplaceUserRolesRequestCWProxyImpl
    implements _$ReplaceUserRolesRequestCWProxy {
  const _$ReplaceUserRolesRequestCWProxyImpl(this._value);

  final ReplaceUserRolesRequest _value;

  @override
  ReplaceUserRolesRequest roleIds(Set<String> roleIds) =>
      this(roleIds: roleIds);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReplaceUserRolesRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReplaceUserRolesRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  ReplaceUserRolesRequest call({
    Object? roleIds = const $CopyWithPlaceholder(),
  }) {
    return ReplaceUserRolesRequest(
      roleIds: roleIds == const $CopyWithPlaceholder()
          ? _value.roleIds
          // ignore: cast_nullable_to_non_nullable
          : roleIds as Set<String>,
    );
  }
}

extension $ReplaceUserRolesRequestCopyWith on ReplaceUserRolesRequest {
  /// Returns a callable class that can be used as follows: `instanceOfReplaceUserRolesRequest.copyWith(...)` or like so:`instanceOfReplaceUserRolesRequest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ReplaceUserRolesRequestCWProxy get copyWith =>
      _$ReplaceUserRolesRequestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ReplaceUserRolesRequest _$ReplaceUserRolesRequestFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ReplaceUserRolesRequest', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['roleIds']);
  final val = ReplaceUserRolesRequest(
    roleIds: $checkedConvert(
      'roleIds',
      (v) => (v as List<dynamic>).map((e) => e as String).toSet(),
    ),
  );
  return val;
});

Map<String, dynamic> _$ReplaceUserRolesRequestToJson(
  ReplaceUserRolesRequest instance,
) => <String, dynamic>{'roleIds': instance.roleIds.toList()};
