// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_summary.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$UserSummaryCWProxy {
  UserSummary id(String id);

  UserSummary username(String username);

  UserSummary displayName(String displayName);

  UserSummary emailMasked(String? emailMasked);

  UserSummary userType(UserSummaryUserTypeEnum userType);

  UserSummary status(UserSummaryStatusEnum status);

  UserSummary scopeMode(UserSummaryScopeModeEnum scopeMode);

  UserSummary roles(List<RoleRef> roles);

  UserSummary lastLoginAt(DateTime? lastLoginAt);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UserSummary(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UserSummary(...).copyWith(id: 12, name: "My name")
  /// ````
  UserSummary call({
    String id,
    String username,
    String displayName,
    String? emailMasked,
    UserSummaryUserTypeEnum userType,
    UserSummaryStatusEnum status,
    UserSummaryScopeModeEnum scopeMode,
    List<RoleRef> roles,
    DateTime? lastLoginAt,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfUserSummary.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfUserSummary.copyWith.fieldName(...)`
class _$UserSummaryCWProxyImpl implements _$UserSummaryCWProxy {
  const _$UserSummaryCWProxyImpl(this._value);

  final UserSummary _value;

  @override
  UserSummary id(String id) => this(id: id);

  @override
  UserSummary username(String username) => this(username: username);

  @override
  UserSummary displayName(String displayName) => this(displayName: displayName);

  @override
  UserSummary emailMasked(String? emailMasked) =>
      this(emailMasked: emailMasked);

  @override
  UserSummary userType(UserSummaryUserTypeEnum userType) =>
      this(userType: userType);

  @override
  UserSummary status(UserSummaryStatusEnum status) => this(status: status);

  @override
  UserSummary scopeMode(UserSummaryScopeModeEnum scopeMode) =>
      this(scopeMode: scopeMode);

  @override
  UserSummary roles(List<RoleRef> roles) => this(roles: roles);

  @override
  UserSummary lastLoginAt(DateTime? lastLoginAt) =>
      this(lastLoginAt: lastLoginAt);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UserSummary(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UserSummary(...).copyWith(id: 12, name: "My name")
  /// ````
  UserSummary call({
    Object? id = const $CopyWithPlaceholder(),
    Object? username = const $CopyWithPlaceholder(),
    Object? displayName = const $CopyWithPlaceholder(),
    Object? emailMasked = const $CopyWithPlaceholder(),
    Object? userType = const $CopyWithPlaceholder(),
    Object? status = const $CopyWithPlaceholder(),
    Object? scopeMode = const $CopyWithPlaceholder(),
    Object? roles = const $CopyWithPlaceholder(),
    Object? lastLoginAt = const $CopyWithPlaceholder(),
  }) {
    return UserSummary(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String,
      username: username == const $CopyWithPlaceholder()
          ? _value.username
          // ignore: cast_nullable_to_non_nullable
          : username as String,
      displayName: displayName == const $CopyWithPlaceholder()
          ? _value.displayName
          // ignore: cast_nullable_to_non_nullable
          : displayName as String,
      emailMasked: emailMasked == const $CopyWithPlaceholder()
          ? _value.emailMasked
          // ignore: cast_nullable_to_non_nullable
          : emailMasked as String?,
      userType: userType == const $CopyWithPlaceholder()
          ? _value.userType
          // ignore: cast_nullable_to_non_nullable
          : userType as UserSummaryUserTypeEnum,
      status: status == const $CopyWithPlaceholder()
          ? _value.status
          // ignore: cast_nullable_to_non_nullable
          : status as UserSummaryStatusEnum,
      scopeMode: scopeMode == const $CopyWithPlaceholder()
          ? _value.scopeMode
          // ignore: cast_nullable_to_non_nullable
          : scopeMode as UserSummaryScopeModeEnum,
      roles: roles == const $CopyWithPlaceholder()
          ? _value.roles
          // ignore: cast_nullable_to_non_nullable
          : roles as List<RoleRef>,
      lastLoginAt: lastLoginAt == const $CopyWithPlaceholder()
          ? _value.lastLoginAt
          // ignore: cast_nullable_to_non_nullable
          : lastLoginAt as DateTime?,
    );
  }
}

extension $UserSummaryCopyWith on UserSummary {
  /// Returns a callable class that can be used as follows: `instanceOfUserSummary.copyWith(...)` or like so:`instanceOfUserSummary.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$UserSummaryCWProxy get copyWith => _$UserSummaryCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UserSummary _$UserSummaryFromJson(Map<String, dynamic> json) =>
    $checkedCreate('UserSummary', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const [
          'id',
          'username',
          'displayName',
          'userType',
          'status',
          'scopeMode',
          'roles',
        ],
      );
      final val = UserSummary(
        id: $checkedConvert('id', (v) => v as String),
        username: $checkedConvert('username', (v) => v as String),
        displayName: $checkedConvert('displayName', (v) => v as String),
        emailMasked: $checkedConvert('emailMasked', (v) => v as String?),
        userType: $checkedConvert(
          'userType',
          (v) => $enumDecode(
            _$UserSummaryUserTypeEnumEnumMap,
            v,
            unknownValue: UserSummaryUserTypeEnum.unknownDefaultOpenApi,
          ),
        ),
        status: $checkedConvert(
          'status',
          (v) => $enumDecode(
            _$UserSummaryStatusEnumEnumMap,
            v,
            unknownValue: UserSummaryStatusEnum.unknownDefaultOpenApi,
          ),
        ),
        scopeMode: $checkedConvert(
          'scopeMode',
          (v) => $enumDecode(
            _$UserSummaryScopeModeEnumEnumMap,
            v,
            unknownValue: UserSummaryScopeModeEnum.unknownDefaultOpenApi,
          ),
        ),
        roles: $checkedConvert(
          'roles',
          (v) => (v as List<dynamic>)
              .map((e) => RoleRef.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
        lastLoginAt: $checkedConvert(
          'lastLoginAt',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$UserSummaryToJson(UserSummary instance) =>
    <String, dynamic>{
      'id': instance.id,
      'username': instance.username,
      'displayName': instance.displayName,
      'emailMasked': ?instance.emailMasked,
      'userType': _$UserSummaryUserTypeEnumEnumMap[instance.userType]!,
      'status': _$UserSummaryStatusEnumEnumMap[instance.status]!,
      'scopeMode': _$UserSummaryScopeModeEnumEnumMap[instance.scopeMode]!,
      'roles': instance.roles.map((e) => e.toJson()).toList(),
      'lastLoginAt': ?instance.lastLoginAt?.toIso8601String(),
    };

const _$UserSummaryUserTypeEnumEnumMap = {
  UserSummaryUserTypeEnum.ADMIN: 'ADMIN',
  UserSummaryUserTypeEnum.MANAGER: 'MANAGER',
  UserSummaryUserTypeEnum.CLIENT_CONTACT: 'CLIENT_CONTACT',
  UserSummaryUserTypeEnum.EMPLOYEE: 'EMPLOYEE',
  UserSummaryUserTypeEnum.SERVICE_ACCOUNT: 'SERVICE_ACCOUNT',
  UserSummaryUserTypeEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};

const _$UserSummaryStatusEnumEnumMap = {
  UserSummaryStatusEnum.INVITED: 'INVITED',
  UserSummaryStatusEnum.ACTIVE: 'ACTIVE',
  UserSummaryStatusEnum.DISABLED: 'DISABLED',
  UserSummaryStatusEnum.LOCKED: 'LOCKED',
  UserSummaryStatusEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};

const _$UserSummaryScopeModeEnumEnumMap = {
  UserSummaryScopeModeEnum.ALL: 'ALL',
  UserSummaryScopeModeEnum.RESTRICTED: 'RESTRICTED',
  UserSummaryScopeModeEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};
