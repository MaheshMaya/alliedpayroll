// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'term_view.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$TermViewCWProxy {
  TermView key(String key);

  TermView label(String label);

  TermView defaultLabel(String defaultLabel);

  TermView overridden(bool overridden);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TermView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TermView(...).copyWith(id: 12, name: "My name")
  /// ````
  TermView call({
    String key,
    String label,
    String defaultLabel,
    bool overridden,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfTermView.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfTermView.copyWith.fieldName(...)`
class _$TermViewCWProxyImpl implements _$TermViewCWProxy {
  const _$TermViewCWProxyImpl(this._value);

  final TermView _value;

  @override
  TermView key(String key) => this(key: key);

  @override
  TermView label(String label) => this(label: label);

  @override
  TermView defaultLabel(String defaultLabel) =>
      this(defaultLabel: defaultLabel);

  @override
  TermView overridden(bool overridden) => this(overridden: overridden);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TermView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TermView(...).copyWith(id: 12, name: "My name")
  /// ````
  TermView call({
    Object? key = const $CopyWithPlaceholder(),
    Object? label = const $CopyWithPlaceholder(),
    Object? defaultLabel = const $CopyWithPlaceholder(),
    Object? overridden = const $CopyWithPlaceholder(),
  }) {
    return TermView(
      key: key == const $CopyWithPlaceholder()
          ? _value.key
          // ignore: cast_nullable_to_non_nullable
          : key as String,
      label: label == const $CopyWithPlaceholder()
          ? _value.label
          // ignore: cast_nullable_to_non_nullable
          : label as String,
      defaultLabel: defaultLabel == const $CopyWithPlaceholder()
          ? _value.defaultLabel
          // ignore: cast_nullable_to_non_nullable
          : defaultLabel as String,
      overridden: overridden == const $CopyWithPlaceholder()
          ? _value.overridden
          // ignore: cast_nullable_to_non_nullable
          : overridden as bool,
    );
  }
}

extension $TermViewCopyWith on TermView {
  /// Returns a callable class that can be used as follows: `instanceOfTermView.copyWith(...)` or like so:`instanceOfTermView.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$TermViewCWProxy get copyWith => _$TermViewCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TermView _$TermViewFromJson(Map<String, dynamic> json) =>
    $checkedCreate('TermView', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const ['key', 'label', 'defaultLabel', 'overridden'],
      );
      final val = TermView(
        key: $checkedConvert('key', (v) => v as String),
        label: $checkedConvert('label', (v) => v as String),
        defaultLabel: $checkedConvert('defaultLabel', (v) => v as String),
        overridden: $checkedConvert('overridden', (v) => v as bool),
      );
      return val;
    });

Map<String, dynamic> _$TermViewToJson(TermView instance) => <String, dynamic>{
  'key': instance.key,
  'label': instance.label,
  'defaultLabel': instance.defaultLabel,
  'overridden': instance.overridden,
};
