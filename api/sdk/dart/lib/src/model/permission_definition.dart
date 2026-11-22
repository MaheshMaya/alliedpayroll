//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'permission_definition.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PermissionDefinition {
  /// Returns a new [PermissionDefinition] instance.
  PermissionDefinition({
    required this.code,

    required this.moduleCode,

    required this.descriptionKey,

    required this.moneyMoving,
  });

  @JsonKey(name: r'code', required: true, includeIfNull: false)
  final String code;

  @JsonKey(name: r'moduleCode', required: true, includeIfNull: false)
  final String moduleCode;

  @JsonKey(name: r'descriptionKey', required: true, includeIfNull: false)
  final String descriptionKey;

  @JsonKey(name: r'moneyMoving', required: true, includeIfNull: false)
  final bool moneyMoving;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is PermissionDefinition &&
          other.code == code &&
          other.moduleCode == moduleCode &&
          other.descriptionKey == descriptionKey &&
          other.moneyMoving == moneyMoving;

  @override
  int get hashCode =>
      code.hashCode +
      moduleCode.hashCode +
      descriptionKey.hashCode +
      moneyMoving.hashCode;

  factory PermissionDefinition.fromJson(Map<String, dynamic> json) =>
      _$PermissionDefinitionFromJson(json);

  Map<String, dynamic> toJson() => _$PermissionDefinitionToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
