// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'terminology_view.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$TerminologyViewCWProxy {
  TerminologyView locale(String locale);

  TerminologyView terms(List<TermView> terms);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TerminologyView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TerminologyView(...).copyWith(id: 12, name: "My name")
  /// ````
  TerminologyView call({String locale, List<TermView> terms});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfTerminologyView.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfTerminologyView.copyWith.fieldName(...)`
class _$TerminologyViewCWProxyImpl implements _$TerminologyViewCWProxy {
  const _$TerminologyViewCWProxyImpl(this._value);

  final TerminologyView _value;

  @override
  TerminologyView locale(String locale) => this(locale: locale);

  @override
  TerminologyView terms(List<TermView> terms) => this(terms: terms);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TerminologyView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TerminologyView(...).copyWith(id: 12, name: "My name")
  /// ````
  TerminologyView call({
    Object? locale = const $CopyWithPlaceholder(),
    Object? terms = const $CopyWithPlaceholder(),
  }) {
    return TerminologyView(
      locale: locale == const $CopyWithPlaceholder()
          ? _value.locale
          // ignore: cast_nullable_to_non_nullable
          : locale as String,
      terms: terms == const $CopyWithPlaceholder()
          ? _value.terms
          // ignore: cast_nullable_to_non_nullable
          : terms as List<TermView>,
    );
  }
}

extension $TerminologyViewCopyWith on TerminologyView {
  /// Returns a callable class that can be used as follows: `instanceOfTerminologyView.copyWith(...)` or like so:`instanceOfTerminologyView.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$TerminologyViewCWProxy get copyWith => _$TerminologyViewCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TerminologyView _$TerminologyViewFromJson(Map<String, dynamic> json) =>
    $checkedCreate('TerminologyView', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['locale', 'terms']);
      final val = TerminologyView(
        locale: $checkedConvert('locale', (v) => v as String),
        terms: $checkedConvert(
          'terms',
          (v) => (v as List<dynamic>)
              .map((e) => TermView.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$TerminologyViewToJson(TerminologyView instance) =>
    <String, dynamic>{
      'locale': instance.locale,
      'terms': instance.terms.map((e) => e.toJson()).toList(),
    };
