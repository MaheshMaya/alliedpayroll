// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'tenant_summary.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$TenantSummaryCWProxy {
  TenantSummary id(String id);

  TenantSummary slug(String slug);

  TenantSummary name(String name);

  TenantSummary industryProfileCode(String industryProfileCode);

  TenantSummary defaultLocale(String defaultLocale);

  TenantSummary enabledLocales(List<String> enabledLocales);

  TenantSummary timezone(String timezone);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TenantSummary(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TenantSummary(...).copyWith(id: 12, name: "My name")
  /// ````
  TenantSummary call({
    String id,
    String slug,
    String name,
    String industryProfileCode,
    String defaultLocale,
    List<String> enabledLocales,
    String timezone,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfTenantSummary.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfTenantSummary.copyWith.fieldName(...)`
class _$TenantSummaryCWProxyImpl implements _$TenantSummaryCWProxy {
  const _$TenantSummaryCWProxyImpl(this._value);

  final TenantSummary _value;

  @override
  TenantSummary id(String id) => this(id: id);

  @override
  TenantSummary slug(String slug) => this(slug: slug);

  @override
  TenantSummary name(String name) => this(name: name);

  @override
  TenantSummary industryProfileCode(String industryProfileCode) =>
      this(industryProfileCode: industryProfileCode);

  @override
  TenantSummary defaultLocale(String defaultLocale) =>
      this(defaultLocale: defaultLocale);

  @override
  TenantSummary enabledLocales(List<String> enabledLocales) =>
      this(enabledLocales: enabledLocales);

  @override
  TenantSummary timezone(String timezone) => this(timezone: timezone);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TenantSummary(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TenantSummary(...).copyWith(id: 12, name: "My name")
  /// ````
  TenantSummary call({
    Object? id = const $CopyWithPlaceholder(),
    Object? slug = const $CopyWithPlaceholder(),
    Object? name = const $CopyWithPlaceholder(),
    Object? industryProfileCode = const $CopyWithPlaceholder(),
    Object? defaultLocale = const $CopyWithPlaceholder(),
    Object? enabledLocales = const $CopyWithPlaceholder(),
    Object? timezone = const $CopyWithPlaceholder(),
  }) {
    return TenantSummary(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String,
      slug: slug == const $CopyWithPlaceholder()
          ? _value.slug
          // ignore: cast_nullable_to_non_nullable
          : slug as String,
      name: name == const $CopyWithPlaceholder()
          ? _value.name
          // ignore: cast_nullable_to_non_nullable
          : name as String,
      industryProfileCode: industryProfileCode == const $CopyWithPlaceholder()
          ? _value.industryProfileCode
          // ignore: cast_nullable_to_non_nullable
          : industryProfileCode as String,
      defaultLocale: defaultLocale == const $CopyWithPlaceholder()
          ? _value.defaultLocale
          // ignore: cast_nullable_to_non_nullable
          : defaultLocale as String,
      enabledLocales: enabledLocales == const $CopyWithPlaceholder()
          ? _value.enabledLocales
          // ignore: cast_nullable_to_non_nullable
          : enabledLocales as List<String>,
      timezone: timezone == const $CopyWithPlaceholder()
          ? _value.timezone
          // ignore: cast_nullable_to_non_nullable
          : timezone as String,
    );
  }
}

extension $TenantSummaryCopyWith on TenantSummary {
  /// Returns a callable class that can be used as follows: `instanceOfTenantSummary.copyWith(...)` or like so:`instanceOfTenantSummary.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$TenantSummaryCWProxy get copyWith => _$TenantSummaryCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TenantSummary _$TenantSummaryFromJson(Map<String, dynamic> json) =>
    $checkedCreate('TenantSummary', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const [
          'id',
          'slug',
          'name',
          'industryProfileCode',
          'defaultLocale',
          'enabledLocales',
          'timezone',
        ],
      );
      final val = TenantSummary(
        id: $checkedConvert('id', (v) => v as String),
        slug: $checkedConvert('slug', (v) => v as String),
        name: $checkedConvert('name', (v) => v as String),
        industryProfileCode: $checkedConvert(
          'industryProfileCode',
          (v) => v as String,
        ),
        defaultLocale: $checkedConvert('defaultLocale', (v) => v as String),
        enabledLocales: $checkedConvert(
          'enabledLocales',
          (v) => (v as List<dynamic>).map((e) => e as String).toList(),
        ),
        timezone: $checkedConvert('timezone', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$TenantSummaryToJson(TenantSummary instance) =>
    <String, dynamic>{
      'id': instance.id,
      'slug': instance.slug,
      'name': instance.name,
      'industryProfileCode': instance.industryProfileCode,
      'defaultLocale': instance.defaultLocale,
      'enabledLocales': instance.enabledLocales,
      'timezone': instance.timezone,
    };
