// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'module_view.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ModuleViewCWProxy {
  ModuleView code(String code);

  ModuleView flagKey(String flagKey);

  ModuleView enabled(bool enabled);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModuleView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModuleView(...).copyWith(id: 12, name: "My name")
  /// ````
  ModuleView call({String code, String flagKey, bool enabled});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfModuleView.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfModuleView.copyWith.fieldName(...)`
class _$ModuleViewCWProxyImpl implements _$ModuleViewCWProxy {
  const _$ModuleViewCWProxyImpl(this._value);

  final ModuleView _value;

  @override
  ModuleView code(String code) => this(code: code);

  @override
  ModuleView flagKey(String flagKey) => this(flagKey: flagKey);

  @override
  ModuleView enabled(bool enabled) => this(enabled: enabled);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModuleView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModuleView(...).copyWith(id: 12, name: "My name")
  /// ````
  ModuleView call({
    Object? code = const $CopyWithPlaceholder(),
    Object? flagKey = const $CopyWithPlaceholder(),
    Object? enabled = const $CopyWithPlaceholder(),
  }) {
    return ModuleView(
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String,
      flagKey: flagKey == const $CopyWithPlaceholder()
          ? _value.flagKey
          // ignore: cast_nullable_to_non_nullable
          : flagKey as String,
      enabled: enabled == const $CopyWithPlaceholder()
          ? _value.enabled
          // ignore: cast_nullable_to_non_nullable
          : enabled as bool,
    );
  }
}

extension $ModuleViewCopyWith on ModuleView {
  /// Returns a callable class that can be used as follows: `instanceOfModuleView.copyWith(...)` or like so:`instanceOfModuleView.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ModuleViewCWProxy get copyWith => _$ModuleViewCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ModuleView _$ModuleViewFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ModuleView', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['code', 'flagKey', 'enabled']);
      final val = ModuleView(
        code: $checkedConvert('code', (v) => v as String),
        flagKey: $checkedConvert('flagKey', (v) => v as String),
        enabled: $checkedConvert('enabled', (v) => v as bool),
      );
      return val;
    });

Map<String, dynamic> _$ModuleViewToJson(ModuleView instance) =>
    <String, dynamic>{
      'code': instance.code,
      'flagKey': instance.flagKey,
      'enabled': instance.enabled,
    };
