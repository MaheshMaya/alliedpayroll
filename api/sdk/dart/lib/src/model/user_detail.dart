//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:allied_api/src/model/role_ref.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'user_detail.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class UserDetail {
  /// Returns a new [UserDetail] instance.
  UserDetail({
    required this.id,

    required this.username,

    required this.displayName,

    this.emailMasked,

    required this.userType,

    required this.status,

    required this.scopeMode,

    required this.linked,

    required this.roles,

    required this.permissions,

    this.lastLoginAt,
  });

  @JsonKey(name: r'id', required: true, includeIfNull: false)
  final String id;

  @JsonKey(name: r'username', required: true, includeIfNull: false)
  final String username;

  @JsonKey(name: r'displayName', required: true, includeIfNull: false)
  final String displayName;

  @JsonKey(name: r'emailMasked', required: false, includeIfNull: false)
  final String? emailMasked;

  @JsonKey(
    name: r'userType',
    required: true,
    includeIfNull: false,
    unknownEnumValue: UserDetailUserTypeEnum.unknownDefaultOpenApi,
  )
  final UserDetailUserTypeEnum userType;

  @JsonKey(
    name: r'status',
    required: true,
    includeIfNull: false,
    unknownEnumValue: UserDetailStatusEnum.unknownDefaultOpenApi,
  )
  final UserDetailStatusEnum status;

  @JsonKey(
    name: r'scopeMode',
    required: true,
    includeIfNull: false,
    unknownEnumValue: UserDetailScopeModeEnum.unknownDefaultOpenApi,
  )
  final UserDetailScopeModeEnum scopeMode;

  @JsonKey(name: r'linked', required: true, includeIfNull: false)
  final bool linked;

  @JsonKey(name: r'roles', required: true, includeIfNull: false)
  final List<RoleRef> roles;

  @JsonKey(name: r'permissions', required: true, includeIfNull: false)
  final Set<String> permissions;

  @JsonKey(name: r'lastLoginAt', required: false, includeIfNull: false)
  final DateTime? lastLoginAt;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is UserDetail &&
          other.id == id &&
          other.username == username &&
          other.displayName == displayName &&
          other.emailMasked == emailMasked &&
          other.userType == userType &&
          other.status == status &&
          other.scopeMode == scopeMode &&
          other.linked == linked &&
          other.roles == roles &&
          other.permissions == permissions &&
          other.lastLoginAt == lastLoginAt;

  @override
  int get hashCode =>
      id.hashCode +
      username.hashCode +
      displayName.hashCode +
      emailMasked.hashCode +
      userType.hashCode +
      status.hashCode +
      scopeMode.hashCode +
      linked.hashCode +
      roles.hashCode +
      permissions.hashCode +
      lastLoginAt.hashCode;

  factory UserDetail.fromJson(Map<String, dynamic> json) =>
      _$UserDetailFromJson(json);

  Map<String, dynamic> toJson() => _$UserDetailToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}

enum UserDetailUserTypeEnum {
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

  const UserDetailUserTypeEnum(this.value);

  final String value;

  @override
  String toString() => value;
}

enum UserDetailStatusEnum {
  @JsonValue(r'INVITED')
  INVITED(r'INVITED'),
  @JsonValue(r'ACTIVE')
  ACTIVE(r'ACTIVE'),
  @JsonValue(r'DISABLED')
  DISABLED(r'DISABLED'),
  @JsonValue(r'LOCKED')
  LOCKED(r'LOCKED'),
  @JsonValue(r'unknown_default_open_api')
  unknownDefaultOpenApi(r'unknown_default_open_api');

  const UserDetailStatusEnum(this.value);

  final String value;

  @override
  String toString() => value;
}

enum UserDetailScopeModeEnum {
  @JsonValue(r'ALL')
  ALL(r'ALL'),
  @JsonValue(r'RESTRICTED')
  RESTRICTED(r'RESTRICTED'),
  @JsonValue(r'unknown_default_open_api')
  unknownDefaultOpenApi(r'unknown_default_open_api');

  const UserDetailScopeModeEnum(this.value);

  final String value;

  @override
  String toString() => value;
}
