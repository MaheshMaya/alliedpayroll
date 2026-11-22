//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'replace_user_roles_request.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ReplaceUserRolesRequest {
  /// Returns a new [ReplaceUserRolesRequest] instance.
  ReplaceUserRolesRequest({required this.roleIds});

  @JsonKey(name: r'roleIds', required: true, includeIfNull: false)
  final Set<String> roleIds;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is ReplaceUserRolesRequest && other.roleIds == roleIds;

  @override
  int get hashCode => roleIds.hashCode;

  factory ReplaceUserRolesRequest.fromJson(Map<String, dynamic> json) =>
      _$ReplaceUserRolesRequestFromJson(json);

  Map<String, dynamic> toJson() => _$ReplaceUserRolesRequestToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
