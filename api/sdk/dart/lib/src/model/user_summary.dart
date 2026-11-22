//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:allied_api/src/model/role_ref.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'user_summary.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class UserSummary {
  /// Returns a new [UserSummary] instance.
  UserSummary({
    required this.id,

    required this.username,

    required this.displayName,

    this.emailMasked,

    required this.userType,

    required this.status,

    required this.scopeMode,

    required this.roles,

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
    unknownEnumValue: UserSummaryUserTypeEnum.unknownDefaultOpenApi,
  )
  final UserSummaryUserTypeEnum userType;

  @JsonKey(
    name: r'status',
    required: true,
    includeIfNull: false,
    unknownEnumValue: UserSummaryStatusEnum.unknownDefaultOpenApi,
  )
  final UserSummaryStatusEnum status;

  @JsonKey(
    name: r'scopeMode',
    required: true,
    includeIfNull: false,
    unknownEnumValue: UserSummaryScopeModeEnum.unknownDefaultOpenApi,
  )
  final UserSummaryScopeModeEnum scopeMode;

  @JsonKey(name: r'roles', required: true, includeIfNull: false)
  final List<RoleRef> roles;

  @JsonKey(name: r'lastLoginAt', required: false, includeIfNull: false)
  final DateTime? lastLoginAt;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is UserSummary &&
          other.id == id &&
          other.username == username &&
          other.displayName == displayName &&
          other.emailMasked == emailMasked &&
          other.userType == userType &&
          other.status == status &&
          other.scopeMode == scopeMode &&
          other.roles == roles &&
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
      roles.hashCode +
      lastLoginAt.hashCode;

  factory UserSummary.fromJson(Map<String, dynamic> json) =>
      _$UserSummaryFromJson(json);

  Map<String, dynamic> toJson() => _$UserSummaryToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}

enum UserSummaryUserTypeEnum {
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

  const UserSummaryUserTypeEnum(this.value);

  final String value;

  @override
  String toString() => value;
}

enum UserSummaryStatusEnum {
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

  const UserSummaryStatusEnum(this.value);

  final String value;

  @override
  String toString() => value;
}

enum UserSummaryScopeModeEnum {
  @JsonValue(r'ALL')
  ALL(r'ALL'),
  @JsonValue(r'RESTRICTED')
  RESTRICTED(r'RESTRICTED'),
  @JsonValue(r'unknown_default_open_api')
  unknownDefaultOpenApi(r'unknown_default_open_api');

  const UserSummaryScopeModeEnum(this.value);

  final String value;

  @override
  String toString() => value;
}
