//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'replace_role_permissions_request.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ReplaceRolePermissionsRequest {
  /// Returns a new [ReplaceRolePermissionsRequest] instance.
  ReplaceRolePermissionsRequest({required this.permissions});

  @JsonKey(name: r'permissions', required: true, includeIfNull: false)
  final Set<String> permissions;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is ReplaceRolePermissionsRequest &&
          other.permissions == permissions;

  @override
  int get hashCode => permissions.hashCode;

  factory ReplaceRolePermissionsRequest.fromJson(Map<String, dynamic> json) =>
      _$ReplaceRolePermissionsRequestFromJson(json);

  Map<String, dynamic> toJson() => _$ReplaceRolePermissionsRequestToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
