// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_role_request.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CreateRoleRequestCWProxy {
  CreateRoleRequest code(String code);

  CreateRoleRequest name(String name);

  CreateRoleRequest description(String? description);

  CreateRoleRequest permissions(Set<String> permissions);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CreateRoleRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CreateRoleRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  CreateRoleRequest call({
    String code,
    String name,
    String? description,
    Set<String> permissions,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCreateRoleRequest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCreateRoleRequest.copyWith.fieldName(...)`
class _$CreateRoleRequestCWProxyImpl implements _$CreateRoleRequestCWProxy {
  const _$CreateRoleRequestCWProxyImpl(this._value);

  final CreateRoleRequest _value;

  @override
  CreateRoleRequest code(String code) => this(code: code);

  @override
  CreateRoleRequest name(String name) => this(name: name);

  @override
  CreateRoleRequest description(String? description) =>
      this(description: description);

  @override
  CreateRoleRequest permissions(Set<String> permissions) =>
      this(permissions: permissions);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CreateRoleRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CreateRoleRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  CreateRoleRequest call({
    Object? code = const $CopyWithPlaceholder(),
    Object? name = const $CopyWithPlaceholder(),
    Object? description = const $CopyWithPlaceholder(),
    Object? permissions = const $CopyWithPlaceholder(),
  }) {
    return CreateRoleRequest(
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String,
      name: name == const $CopyWithPlaceholder()
          ? _value.name
          // ignore: cast_nullable_to_non_nullable
          : name as String,
      description: description == const $CopyWithPlaceholder()
          ? _value.description
          // ignore: cast_nullable_to_non_nullable
          : description as String?,
      permissions: permissions == const $CopyWithPlaceholder()
          ? _value.permissions
          // ignore: cast_nullable_to_non_nullable
          : permissions as Set<String>,
    );
  }
}

extension $CreateRoleRequestCopyWith on CreateRoleRequest {
  /// Returns a callable class that can be used as follows: `instanceOfCreateRoleRequest.copyWith(...)` or like so:`instanceOfCreateRoleRequest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CreateRoleRequestCWProxy get copyWith =>
      _$CreateRoleRequestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CreateRoleRequest _$CreateRoleRequestFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CreateRoleRequest', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['code', 'name', 'permissions']);
      final val = CreateRoleRequest(
        code: $checkedConvert('code', (v) => v as String),
        name: $checkedConvert('name', (v) => v as String),
        description: $checkedConvert('description', (v) => v as String?),
        permissions: $checkedConvert(
          'permissions',
          (v) => (v as List<dynamic>).map((e) => e as String).toSet(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$CreateRoleRequestToJson(CreateRoleRequest instance) =>
    <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'description': ?instance.description,
      'permissions': instance.permissions.toList(),
    };
