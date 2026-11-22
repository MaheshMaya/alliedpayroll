// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'permission_definition.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PermissionDefinitionCWProxy {
  PermissionDefinition code(String code);

  PermissionDefinition moduleCode(String moduleCode);

  PermissionDefinition descriptionKey(String descriptionKey);

  PermissionDefinition moneyMoving(bool moneyMoving);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PermissionDefinition(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PermissionDefinition(...).copyWith(id: 12, name: "My name")
  /// ````
  PermissionDefinition call({
    String code,
    String moduleCode,
    String descriptionKey,
    bool moneyMoving,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPermissionDefinition.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPermissionDefinition.copyWith.fieldName(...)`
class _$PermissionDefinitionCWProxyImpl
    implements _$PermissionDefinitionCWProxy {
  const _$PermissionDefinitionCWProxyImpl(this._value);

  final PermissionDefinition _value;

  @override
  PermissionDefinition code(String code) => this(code: code);

  @override
  PermissionDefinition moduleCode(String moduleCode) =>
      this(moduleCode: moduleCode);

  @override
  PermissionDefinition descriptionKey(String descriptionKey) =>
      this(descriptionKey: descriptionKey);

  @override
  PermissionDefinition moneyMoving(bool moneyMoving) =>
      this(moneyMoving: moneyMoving);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PermissionDefinition(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PermissionDefinition(...).copyWith(id: 12, name: "My name")
  /// ````
  PermissionDefinition call({
    Object? code = const $CopyWithPlaceholder(),
    Object? moduleCode = const $CopyWithPlaceholder(),
    Object? descriptionKey = const $CopyWithPlaceholder(),
    Object? moneyMoving = const $CopyWithPlaceholder(),
  }) {
    return PermissionDefinition(
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String,
      moduleCode: moduleCode == const $CopyWithPlaceholder()
          ? _value.moduleCode
          // ignore: cast_nullable_to_non_nullable
          : moduleCode as String,
      descriptionKey: descriptionKey == const $CopyWithPlaceholder()
          ? _value.descriptionKey
          // ignore: cast_nullable_to_non_nullable
          : descriptionKey as String,
      moneyMoving: moneyMoving == const $CopyWithPlaceholder()
          ? _value.moneyMoving
          // ignore: cast_nullable_to_non_nullable
          : moneyMoving as bool,
    );
  }
}

extension $PermissionDefinitionCopyWith on PermissionDefinition {
  /// Returns a callable class that can be used as follows: `instanceOfPermissionDefinition.copyWith(...)` or like so:`instanceOfPermissionDefinition.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PermissionDefinitionCWProxy get copyWith =>
      _$PermissionDefinitionCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PermissionDefinition _$PermissionDefinitionFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PermissionDefinition', json, ($checkedConvert) {
  $checkKeys(
    json,
    requiredKeys: const ['code', 'moduleCode', 'descriptionKey', 'moneyMoving'],
  );
  final val = PermissionDefinition(
    code: $checkedConvert('code', (v) => v as String),
    moduleCode: $checkedConvert('moduleCode', (v) => v as String),
    descriptionKey: $checkedConvert('descriptionKey', (v) => v as String),
    moneyMoving: $checkedConvert('moneyMoving', (v) => v as bool),
  );
  return val;
});

Map<String, dynamic> _$PermissionDefinitionToJson(
  PermissionDefinition instance,
) => <String, dynamic>{
  'code': instance.code,
  'moduleCode': instance.moduleCode,
  'descriptionKey': instance.descriptionKey,
  'moneyMoving': instance.moneyMoving,
};
