//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'update_role_request.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class UpdateRoleRequest {
  /// Returns a new [UpdateRoleRequest] instance.
  UpdateRoleRequest({required this.name, this.description});

  @JsonKey(name: r'name', required: true, includeIfNull: false)
  final String name;

  @JsonKey(name: r'description', required: false, includeIfNull: false)
  final String? description;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is UpdateRoleRequest &&
          other.name == name &&
          other.description == description;

  @override
  int get hashCode => name.hashCode + description.hashCode;

  factory UpdateRoleRequest.fromJson(Map<String, dynamic> json) =>
      _$UpdateRoleRequestFromJson(json);

  Map<String, dynamic> toJson() => _$UpdateRoleRequestToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
