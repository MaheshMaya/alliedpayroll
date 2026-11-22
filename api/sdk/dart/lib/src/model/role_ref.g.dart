// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'role_ref.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RoleRefCWProxy {
  RoleRef id(String id);

  RoleRef code(String code);

  RoleRef name(String name);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RoleRef(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RoleRef(...).copyWith(id: 12, name: "My name")
  /// ````
  RoleRef call({String id, String code, String name});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRoleRef.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRoleRef.copyWith.fieldName(...)`
class _$RoleRefCWProxyImpl implements _$RoleRefCWProxy {
  const _$RoleRefCWProxyImpl(this._value);

  final RoleRef _value;

  @override
  RoleRef id(String id) => this(id: id);

  @override
  RoleRef code(String code) => this(code: code);

  @override
  RoleRef name(String name) => this(name: name);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RoleRef(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RoleRef(...).copyWith(id: 12, name: "My name")
  /// ````
  RoleRef call({
    Object? id = const $CopyWithPlaceholder(),
    Object? code = const $CopyWithPlaceholder(),
    Object? name = const $CopyWithPlaceholder(),
  }) {
    return RoleRef(
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
    );
  }
}

extension $RoleRefCopyWith on RoleRef {
  /// Returns a callable class that can be used as follows: `instanceOfRoleRef.copyWith(...)` or like so:`instanceOfRoleRef.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RoleRefCWProxy get copyWith => _$RoleRefCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RoleRef _$RoleRefFromJson(Map<String, dynamic> json) =>
    $checkedCreate('RoleRef', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['id', 'code', 'name']);
      final val = RoleRef(
        id: $checkedConvert('id', (v) => v as String),
        code: $checkedConvert('code', (v) => v as String),
        name: $checkedConvert('name', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$RoleRefToJson(RoleRef instance) => <String, dynamic>{
  'id': instance.id,
  'code': instance.code,
  'name': instance.name,
};
