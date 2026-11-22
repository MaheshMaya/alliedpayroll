// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_detail.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$UserDetailCWProxy {
  UserDetail id(String id);

  UserDetail username(String username);

  UserDetail displayName(String displayName);

  UserDetail emailMasked(String? emailMasked);

  UserDetail userType(UserDetailUserTypeEnum userType);

  UserDetail status(UserDetailStatusEnum status);

  UserDetail scopeMode(UserDetailScopeModeEnum scopeMode);

  UserDetail linked(bool linked);

  UserDetail roles(List<RoleRef> roles);

  UserDetail permissions(Set<String> permissions);

  UserDetail lastLoginAt(DateTime? lastLoginAt);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UserDetail(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UserDetail(...).copyWith(id: 12, name: "My name")
  /// ````
  UserDetail call({
    String id,
    String username,
    String displayName,
    String? emailMasked,
    UserDetailUserTypeEnum userType,
    UserDetailStatusEnum status,
    UserDetailScopeModeEnum scopeMode,
    bool linked,
    List<RoleRef> roles,
    Set<String> permissions,
    DateTime? lastLoginAt,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfUserDetail.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfUserDetail.copyWith.fieldName(...)`
class _$UserDetailCWProxyImpl implements _$UserDetailCWProxy {
  const _$UserDetailCWProxyImpl(this._value);

  final UserDetail _value;

  @override
  UserDetail id(String id) => this(id: id);

  @override
  UserDetail username(String username) => this(username: username);

  @override
  UserDetail displayName(String displayName) => this(displayName: displayName);

  @override
  UserDetail emailMasked(String? emailMasked) => this(emailMasked: emailMasked);

  @override
  UserDetail userType(UserDetailUserTypeEnum userType) =>
      this(userType: userType);

  @override
  UserDetail status(UserDetailStatusEnum status) => this(status: status);

  @override
  UserDetail scopeMode(UserDetailScopeModeEnum scopeMode) =>
      this(scopeMode: scopeMode);

  @override
  UserDetail linked(bool linked) => this(linked: linked);

  @override
  UserDetail roles(List<RoleRef> roles) => this(roles: roles);

  @override
  UserDetail permissions(Set<String> permissions) =>
      this(permissions: permissions);

  @override
  UserDetail lastLoginAt(DateTime? lastLoginAt) =>
      this(lastLoginAt: lastLoginAt);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UserDetail(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UserDetail(...).copyWith(id: 12, name: "My name")
  /// ````
  UserDetail call({
    Object? id = const $CopyWithPlaceholder(),
    Object? username = const $CopyWithPlaceholder(),
    Object? displayName = const $CopyWithPlaceholder(),
    Object? emailMasked = const $CopyWithPlaceholder(),
    Object? userType = const $CopyWithPlaceholder(),
    Object? status = const $CopyWithPlaceholder(),
    Object? scopeMode = const $CopyWithPlaceholder(),
    Object? linked = const $CopyWithPlaceholder(),
    Object? roles = const $CopyWithPlaceholder(),
    Object? permissions = const $CopyWithPlaceholder(),
    Object? lastLoginAt = const $CopyWithPlaceholder(),
  }) {
    return UserDetail(
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
          : userType as UserDetailUserTypeEnum,
      status: status == const $CopyWithPlaceholder()
          ? _value.status
          // ignore: cast_nullable_to_non_nullable
          : status as UserDetailStatusEnum,
      scopeMode: scopeMode == const $CopyWithPlaceholder()
          ? _value.scopeMode
          // ignore: cast_nullable_to_non_nullable
          : scopeMode as UserDetailScopeModeEnum,
      linked: linked == const $CopyWithPlaceholder()
          ? _value.linked
          // ignore: cast_nullable_to_non_nullable
          : linked as bool,
      roles: roles == const $CopyWithPlaceholder()
          ? _value.roles
          // ignore: cast_nullable_to_non_nullable
          : roles as List<RoleRef>,
      permissions: permissions == const $CopyWithPlaceholder()
          ? _value.permissions
          // ignore: cast_nullable_to_non_nullable
          : permissions as Set<String>,
      lastLoginAt: lastLoginAt == const $CopyWithPlaceholder()
          ? _value.lastLoginAt
          // ignore: cast_nullable_to_non_nullable
          : lastLoginAt as DateTime?,
    );
  }
}

extension $UserDetailCopyWith on UserDetail {
  /// Returns a callable class that can be used as follows: `instanceOfUserDetail.copyWith(...)` or like so:`instanceOfUserDetail.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$UserDetailCWProxy get copyWith => _$UserDetailCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UserDetail _$UserDetailFromJson(Map<String, dynamic> json) =>
    $checkedCreate('UserDetail', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const [
          'id',
          'username',
          'displayName',
          'userType',
          'status',
          'scopeMode',
          'linked',
          'roles',
          'permissions',
        ],
      );
      final val = UserDetail(
        id: $checkedConvert('id', (v) => v as String),
        username: $checkedConvert('username', (v) => v as String),
        displayName: $checkedConvert('displayName', (v) => v as String),
        emailMasked: $checkedConvert('emailMasked', (v) => v as String?),
        userType: $checkedConvert(
          'userType',
          (v) => $enumDecode(
            _$UserDetailUserTypeEnumEnumMap,
            v,
            unknownValue: UserDetailUserTypeEnum.unknownDefaultOpenApi,
          ),
        ),
        status: $checkedConvert(
          'status',
          (v) => $enumDecode(
            _$UserDetailStatusEnumEnumMap,
            v,
            unknownValue: UserDetailStatusEnum.unknownDefaultOpenApi,
          ),
        ),
        scopeMode: $checkedConvert(
          'scopeMode',
          (v) => $enumDecode(
            _$UserDetailScopeModeEnumEnumMap,
            v,
            unknownValue: UserDetailScopeModeEnum.unknownDefaultOpenApi,
          ),
        ),
        linked: $checkedConvert('linked', (v) => v as bool),
        roles: $checkedConvert(
          'roles',
          (v) => (v as List<dynamic>)
              .map((e) => RoleRef.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
        permissions: $checkedConvert(
          'permissions',
          (v) => (v as List<dynamic>).map((e) => e as String).toSet(),
        ),
        lastLoginAt: $checkedConvert(
          'lastLoginAt',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$UserDetailToJson(UserDetail instance) =>
    <String, dynamic>{
      'id': instance.id,
      'username': instance.username,
      'displayName': instance.displayName,
      'emailMasked': ?instance.emailMasked,
      'userType': _$UserDetailUserTypeEnumEnumMap[instance.userType]!,
      'status': _$UserDetailStatusEnumEnumMap[instance.status]!,
      'scopeMode': _$UserDetailScopeModeEnumEnumMap[instance.scopeMode]!,
      'linked': instance.linked,
      'roles': instance.roles.map((e) => e.toJson()).toList(),
      'permissions': instance.permissions.toList(),
      'lastLoginAt': ?instance.lastLoginAt?.toIso8601String(),
    };

const _$UserDetailUserTypeEnumEnumMap = {
  UserDetailUserTypeEnum.ADMIN: 'ADMIN',
  UserDetailUserTypeEnum.MANAGER: 'MANAGER',
  UserDetailUserTypeEnum.CLIENT_CONTACT: 'CLIENT_CONTACT',
  UserDetailUserTypeEnum.EMPLOYEE: 'EMPLOYEE',
  UserDetailUserTypeEnum.SERVICE_ACCOUNT: 'SERVICE_ACCOUNT',
  UserDetailUserTypeEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};

const _$UserDetailStatusEnumEnumMap = {
  UserDetailStatusEnum.INVITED: 'INVITED',
  UserDetailStatusEnum.ACTIVE: 'ACTIVE',
  UserDetailStatusEnum.DISABLED: 'DISABLED',
  UserDetailStatusEnum.LOCKED: 'LOCKED',
  UserDetailStatusEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};

const _$UserDetailScopeModeEnumEnumMap = {
  UserDetailScopeModeEnum.ALL: 'ALL',
  UserDetailScopeModeEnum.RESTRICTED: 'RESTRICTED',
  UserDetailScopeModeEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};
