// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'invite_user_request.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$InviteUserRequestCWProxy {
  InviteUserRequest displayName(String displayName);

  InviteUserRequest email(String email);

  InviteUserRequest userType(InviteUserRequestUserTypeEnum userType);

  InviteUserRequest roleIds(Set<String> roleIds);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `InviteUserRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// InviteUserRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  InviteUserRequest call({
    String displayName,
    String email,
    InviteUserRequestUserTypeEnum userType,
    Set<String> roleIds,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfInviteUserRequest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfInviteUserRequest.copyWith.fieldName(...)`
class _$InviteUserRequestCWProxyImpl implements _$InviteUserRequestCWProxy {
  const _$InviteUserRequestCWProxyImpl(this._value);

  final InviteUserRequest _value;

  @override
  InviteUserRequest displayName(String displayName) =>
      this(displayName: displayName);

  @override
  InviteUserRequest email(String email) => this(email: email);

  @override
  InviteUserRequest userType(InviteUserRequestUserTypeEnum userType) =>
      this(userType: userType);

  @override
  InviteUserRequest roleIds(Set<String> roleIds) => this(roleIds: roleIds);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `InviteUserRequest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// InviteUserRequest(...).copyWith(id: 12, name: "My name")
  /// ````
  InviteUserRequest call({
    Object? displayName = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? userType = const $CopyWithPlaceholder(),
    Object? roleIds = const $CopyWithPlaceholder(),
  }) {
    return InviteUserRequest(
      displayName: displayName == const $CopyWithPlaceholder()
          ? _value.displayName
          // ignore: cast_nullable_to_non_nullable
          : displayName as String,
      email: email == const $CopyWithPlaceholder()
          ? _value.email
          // ignore: cast_nullable_to_non_nullable
          : email as String,
      userType: userType == const $CopyWithPlaceholder()
          ? _value.userType
          // ignore: cast_nullable_to_non_nullable
          : userType as InviteUserRequestUserTypeEnum,
      roleIds: roleIds == const $CopyWithPlaceholder()
          ? _value.roleIds
          // ignore: cast_nullable_to_non_nullable
          : roleIds as Set<String>,
    );
  }
}

extension $InviteUserRequestCopyWith on InviteUserRequest {
  /// Returns a callable class that can be used as follows: `instanceOfInviteUserRequest.copyWith(...)` or like so:`instanceOfInviteUserRequest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$InviteUserRequestCWProxy get copyWith =>
      _$InviteUserRequestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

InviteUserRequest _$InviteUserRequestFromJson(Map<String, dynamic> json) =>
    $checkedCreate('InviteUserRequest', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const ['displayName', 'email', 'userType', 'roleIds'],
      );
      final val = InviteUserRequest(
        displayName: $checkedConvert('displayName', (v) => v as String),
        email: $checkedConvert('email', (v) => v as String),
        userType: $checkedConvert(
          'userType',
          (v) => $enumDecode(
            _$InviteUserRequestUserTypeEnumEnumMap,
            v,
            unknownValue: InviteUserRequestUserTypeEnum.unknownDefaultOpenApi,
          ),
        ),
        roleIds: $checkedConvert(
          'roleIds',
          (v) => (v as List<dynamic>).map((e) => e as String).toSet(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$InviteUserRequestToJson(InviteUserRequest instance) =>
    <String, dynamic>{
      'displayName': instance.displayName,
      'email': instance.email,
      'userType': _$InviteUserRequestUserTypeEnumEnumMap[instance.userType]!,
      'roleIds': instance.roleIds.toList(),
    };

const _$InviteUserRequestUserTypeEnumEnumMap = {
  InviteUserRequestUserTypeEnum.ADMIN: 'ADMIN',
  InviteUserRequestUserTypeEnum.MANAGER: 'MANAGER',
  InviteUserRequestUserTypeEnum.CLIENT_CONTACT: 'CLIENT_CONTACT',
  InviteUserRequestUserTypeEnum.EMPLOYEE: 'EMPLOYEE',
  InviteUserRequestUserTypeEnum.SERVICE_ACCOUNT: 'SERVICE_ACCOUNT',
  InviteUserRequestUserTypeEnum.unknownDefaultOpenApi:
      'unknown_default_open_api',
};
