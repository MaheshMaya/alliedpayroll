// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'role_view.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RoleViewCWProxy {
  RoleView id(String id);

  RoleView code(String code);

  RoleView name(String name);

  RoleView description(String? description);

  RoleView system(bool system);

  RoleView permissions(Set<String> permissions);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RoleView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RoleView(...).copyWith(id: 12, name: "My name")
  /// ````
  RoleView call({
    String id,
    String code,
    String name,
    String? description,
    bool system,
    Set<String> permissions,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRoleView.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRoleView.copyWith.fieldName(...)`
class _$RoleViewCWProxyImpl implements _$RoleViewCWProxy {
  const _$RoleViewCWProxyImpl(this._value);

  final RoleView _value;

  @override
  RoleView id(String id) => this(id: id);

  @override
  RoleView code(String code) => this(code: code);

  @override
  RoleView name(String name) => this(name: name);

  @override
  RoleView description(String? description) => this(description: description);

  @override
  RoleView system(bool system) => this(system: system);

  @override
  RoleView permissions(Set<String> permissions) =>
      this(permissions: permissions);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RoleView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RoleView(...).copyWith(id: 12, name: "My name")
  /// ````
  RoleView call({
    Object? id = const $CopyWithPlaceholder(),
    Object? code = const $CopyWithPlaceholder(),
    Object? name = const $CopyWithPlaceholder(),
    Object? description = const $CopyWithPlaceholder(),
    Object? system = const $CopyWithPlaceholder(),
    Object? permissions = const $CopyWithPlaceholder(),
  }) {
    return RoleView(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String,
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
      system: system == const $CopyWithPlaceholder()
          ? _value.system
          // ignore: cast_nullable_to_non_nullable
          : system as bool,
      permissions: permissions == const $CopyWithPlaceholder()
          ? _value.permissions
          // ignore: cast_nullable_to_non_nullable
          : permissions as Set<String>,
    );
  }
}

extension $RoleViewCopyWith on RoleView {
  /// Returns a callable class that can be used as follows: `instanceOfRoleView.copyWith(...)` or like so:`instanceOfRoleView.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RoleViewCWProxy get copyWith => _$RoleViewCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RoleView _$RoleViewFromJson(Map<String, dynamic> json) =>
    $checkedCreate('RoleView', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const ['id', 'code', 'name', 'system', 'permissions'],
      );
      final val = RoleView(
        id: $checkedConvert('id', (v) => v as String),
        code: $checkedConvert('code', (v) => v as String),
        name: $checkedConvert('name', (v) => v as String),
        description: $checkedConvert('description', (v) => v as String?),
        system: $checkedConvert('system', (v) => v as bool),
        permissions: $checkedConvert(
          'permissions',
          (v) => (v as List<dynamic>).map((e) => e as String).toSet(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$RoleViewToJson(RoleView instance) => <String, dynamic>{
  'id': instance.id,
  'code': instance.code,
  'name': instance.name,
  'description': ?instance.description,
  'system': instance.system,
  'permissions': instance.permissions.toList(),
};
