//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'tenant_summary.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class TenantSummary {
  /// Returns a new [TenantSummary] instance.
  TenantSummary({
    required this.id,

    required this.slug,

    required this.name,

    required this.industryProfileCode,

    required this.defaultLocale,

    required this.enabledLocales,

    required this.timezone,
  });

  @JsonKey(name: r'id', required: true, includeIfNull: false)
  final String id;

  @JsonKey(name: r'slug', required: true, includeIfNull: false)
  final String slug;

  @JsonKey(name: r'name', required: true, includeIfNull: false)
  final String name;

  @JsonKey(name: r'industryProfileCode', required: true, includeIfNull: false)
  final String industryProfileCode;

  @JsonKey(name: r'defaultLocale', required: true, includeIfNull: false)
  final String defaultLocale;

  @JsonKey(name: r'enabledLocales', required: true, includeIfNull: false)
  final List<String> enabledLocales;

  @JsonKey(name: r'timezone', required: true, includeIfNull: false)
  final String timezone;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is TenantSummary &&
          other.id == id &&
          other.slug == slug &&
          other.name == name &&
          other.industryProfileCode == industryProfileCode &&
          other.defaultLocale == defaultLocale &&
          other.enabledLocales == enabledLocales &&
          other.timezone == timezone;

  @override
  int get hashCode =>
      id.hashCode +
      slug.hashCode +
      name.hashCode +
      industryProfileCode.hashCode +
      defaultLocale.hashCode +
      enabledLocales.hashCode +
      timezone.hashCode;

  factory TenantSummary.fromJson(Map<String, dynamic> json) =>
      _$TenantSummaryFromJson(json);

  Map<String, dynamic> toJson() => _$TenantSummaryToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
