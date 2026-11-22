//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'role_view.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class RoleView {
  /// Returns a new [RoleView] instance.
  RoleView({
    required this.id,

    required this.code,

    required this.name,

    this.description,

    required this.system,

    required this.permissions,
  });

  @JsonKey(name: r'id', required: true, includeIfNull: false)
  final String id;

  @JsonKey(name: r'code', required: true, includeIfNull: false)
  final String code;

  @JsonKey(name: r'name', required: true, includeIfNull: false)
  final String name;

  @JsonKey(name: r'description', required: false, includeIfNull: false)
  final String? description;

  @JsonKey(name: r'system', required: true, includeIfNull: false)
  final bool system;

  @JsonKey(name: r'permissions', required: true, includeIfNull: false)
  final Set<String> permissions;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is RoleView &&
          other.id == id &&
          other.code == code &&
          other.name == name &&
          other.description == description &&
          other.system == system &&
          other.permissions == permissions;

  @override
  int get hashCode =>
      id.hashCode +
      code.hashCode +
      name.hashCode +
      description.hashCode +
      system.hashCode +
      permissions.hashCode;

  factory RoleView.fromJson(Map<String, dynamic> json) =>
      _$RoleViewFromJson(json);

  Map<String, dynamic> toJson() => _$RoleViewToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
