//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'invite_user_request.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class InviteUserRequest {
  /// Returns a new [InviteUserRequest] instance.
  InviteUserRequest({
    required this.displayName,

    required this.email,

    required this.userType,

    required this.roleIds,
  });

  @JsonKey(name: r'displayName', required: true, includeIfNull: false)
  final String displayName;

  @JsonKey(name: r'email', required: true, includeIfNull: false)
  final String email;

  @JsonKey(
    name: r'userType',
    required: true,
    includeIfNull: false,
    unknownEnumValue: InviteUserRequestUserTypeEnum.unknownDefaultOpenApi,
  )
  final InviteUserRequestUserTypeEnum userType;

  @JsonKey(name: r'roleIds', required: true, includeIfNull: false)
  final Set<String> roleIds;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is InviteUserRequest &&
          other.displayName == displayName &&
          other.email == email &&
          other.userType == userType &&
          other.roleIds == roleIds;

  @override
  int get hashCode =>
      displayName.hashCode +
      email.hashCode +
      userType.hashCode +
      roleIds.hashCode;

  factory InviteUserRequest.fromJson(Map<String, dynamic> json) =>
      _$InviteUserRequestFromJson(json);

  Map<String, dynamic> toJson() => _$InviteUserRequestToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}

enum InviteUserRequestUserTypeEnum {
  @JsonValue(r'ADMIN')
  ADMIN(r'ADMIN'),
  @JsonValue(r'MANAGER')
  MANAGER(r'MANAGER'),
  @JsonValue(r'CLIENT_CONTACT')
  CLIENT_CONTACT(r'CLIENT_CONTACT'),
  @JsonValue(r'EMPLOYEE')
  EMPLOYEE(r'EMPLOYEE'),
  @JsonValue(r'SERVICE_ACCOUNT')
  SERVICE_ACCOUNT(r'SERVICE_ACCOUNT'),
  @JsonValue(r'unknown_default_open_api')
  unknownDefaultOpenApi(r'unknown_default_open_api');

  const InviteUserRequestUserTypeEnum(this.value);

  final String value;

  @override
  String toString() => value;
}
