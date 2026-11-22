//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:allied_api/src/model/tenant_summary.dart';
import 'package:allied_api/src/model/user.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bootstrap.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class Bootstrap {
  /// Returns a new [Bootstrap] instance.
  Bootstrap({
    required this.tenant,

    required this.user,

    required this.permissions,

    required this.features,

    required this.terminology,
  });

  @JsonKey(name: r'tenant', required: true, includeIfNull: false)
  final TenantSummary tenant;

  @JsonKey(name: r'user', required: true, includeIfNull: false)
  final User user;

  @JsonKey(name: r'permissions', required: true, includeIfNull: false)
  final Set<String> permissions;

  @JsonKey(name: r'features', required: true, includeIfNull: false)
  final Map<String, bool> features;

  @JsonKey(name: r'terminology', required: true, includeIfNull: false)
  final Map<String, String> terminology;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Bootstrap &&
          other.tenant == tenant &&
          other.user == user &&
          other.permissions == permissions &&
          other.features == features &&
          other.terminology == terminology;

  @override
  int get hashCode =>
      tenant.hashCode +
      user.hashCode +
      permissions.hashCode +
      features.hashCode +
      terminology.hashCode;

  factory Bootstrap.fromJson(Map<String, dynamic> json) =>
      _$BootstrapFromJson(json);

  Map<String, dynamic> toJson() => _$BootstrapToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
