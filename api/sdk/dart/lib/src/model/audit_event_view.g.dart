// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'audit_event_view.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$AuditEventViewCWProxy {
  AuditEventView id(String id);

  AuditEventView occurredAt(DateTime occurredAt);

  AuditEventView action(AuditEventViewActionEnum action);

  AuditEventView entityType(String? entityType);

  AuditEventView entityId(String? entityId);

  AuditEventView entityLabel(String? entityLabel);

  AuditEventView actorType(AuditEventViewActorTypeEnum actorType);

  AuditEventView actorId(String actorId);

  AuditEventView actorDisplayName(String? actorDisplayName);

  AuditEventView channel(String channel);

  AuditEventView clientIp(String? clientIp);

  AuditEventView changedFields(List<String> changedFields);

  AuditEventView oldValues(Map<String, Object?>? oldValues);

  AuditEventView newValues(Map<String, Object?>? newValues);

  AuditEventView reasonKey(String? reasonKey);

  AuditEventView reasonParams(Map<String, Object?> reasonParams);

  AuditEventView note(String? note);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AuditEventView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AuditEventView(...).copyWith(id: 12, name: "My name")
  /// ````
  AuditEventView call({
    String id,
    DateTime occurredAt,
    AuditEventViewActionEnum action,
    String? entityType,
    String? entityId,
    String? entityLabel,
    AuditEventViewActorTypeEnum actorType,
    String actorId,
    String? actorDisplayName,
    String channel,
    String? clientIp,
    List<String> changedFields,
    Map<String, Object?>? oldValues,
    Map<String, Object?>? newValues,
    String? reasonKey,
    Map<String, Object?> reasonParams,
    String? note,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfAuditEventView.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfAuditEventView.copyWith.fieldName(...)`
class _$AuditEventViewCWProxyImpl implements _$AuditEventViewCWProxy {
  const _$AuditEventViewCWProxyImpl(this._value);

  final AuditEventView _value;

  @override
  AuditEventView id(String id) => this(id: id);

  @override
  AuditEventView occurredAt(DateTime occurredAt) =>
      this(occurredAt: occurredAt);

  @override
  AuditEventView action(AuditEventViewActionEnum action) =>
      this(action: action);

  @override
  AuditEventView entityType(String? entityType) => this(entityType: entityType);

  @override
  AuditEventView entityId(String? entityId) => this(entityId: entityId);

  @override
  AuditEventView entityLabel(String? entityLabel) =>
      this(entityLabel: entityLabel);

  @override
  AuditEventView actorType(AuditEventViewActorTypeEnum actorType) =>
      this(actorType: actorType);

  @override
  AuditEventView actorId(String actorId) => this(actorId: actorId);

  @override
  AuditEventView actorDisplayName(String? actorDisplayName) =>
      this(actorDisplayName: actorDisplayName);

  @override
  AuditEventView channel(String channel) => this(channel: channel);

  @override
  AuditEventView clientIp(String? clientIp) => this(clientIp: clientIp);

  @override
  AuditEventView changedFields(List<String> changedFields) =>
      this(changedFields: changedFields);

  @override
  AuditEventView oldValues(Map<String, Object?>? oldValues) =>
      this(oldValues: oldValues);

  @override
  AuditEventView newValues(Map<String, Object?>? newValues) =>
      this(newValues: newValues);

  @override
  AuditEventView reasonKey(String? reasonKey) => this(reasonKey: reasonKey);

  @override
  AuditEventView reasonParams(Map<String, Object?> reasonParams) =>
      this(reasonParams: reasonParams);

  @override
  AuditEventView note(String? note) => this(note: note);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AuditEventView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AuditEventView(...).copyWith(id: 12, name: "My name")
  /// ````
  AuditEventView call({
    Object? id = const $CopyWithPlaceholder(),
    Object? occurredAt = const $CopyWithPlaceholder(),
    Object? action = const $CopyWithPlaceholder(),
    Object? entityType = const $CopyWithPlaceholder(),
    Object? entityId = const $CopyWithPlaceholder(),
    Object? entityLabel = const $CopyWithPlaceholder(),
    Object? actorType = const $CopyWithPlaceholder(),
    Object? actorId = const $CopyWithPlaceholder(),
    Object? actorDisplayName = const $CopyWithPlaceholder(),
    Object? channel = const $CopyWithPlaceholder(),
    Object? clientIp = const $CopyWithPlaceholder(),
    Object? changedFields = const $CopyWithPlaceholder(),
    Object? oldValues = const $CopyWithPlaceholder(),
    Object? newValues = const $CopyWithPlaceholder(),
    Object? reasonKey = const $CopyWithPlaceholder(),
    Object? reasonParams = const $CopyWithPlaceholder(),
    Object? note = const $CopyWithPlaceholder(),
  }) {
    return AuditEventView(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String,
      occurredAt: occurredAt == const $CopyWithPlaceholder()
          ? _value.occurredAt
          // ignore: cast_nullable_to_non_nullable
          : occurredAt as DateTime,
      action: action == const $CopyWithPlaceholder()
          ? _value.action
          // ignore: cast_nullable_to_non_nullable
          : action as AuditEventViewActionEnum,
      entityType: entityType == const $CopyWithPlaceholder()
          ? _value.entityType
          // ignore: cast_nullable_to_non_nullable
          : entityType as String?,
      entityId: entityId == const $CopyWithPlaceholder()
          ? _value.entityId
          // ignore: cast_nullable_to_non_nullable
          : entityId as String?,
      entityLabel: entityLabel == const $CopyWithPlaceholder()
          ? _value.entityLabel
          // ignore: cast_nullable_to_non_nullable
          : entityLabel as String?,
      actorType: actorType == const $CopyWithPlaceholder()
          ? _value.actorType
          // ignore: cast_nullable_to_non_nullable
          : actorType as AuditEventViewActorTypeEnum,
      actorId: actorId == const $CopyWithPlaceholder()
          ? _value.actorId
          // ignore: cast_nullable_to_non_nullable
          : actorId as String,
      actorDisplayName: actorDisplayName == const $CopyWithPlaceholder()
          ? _value.actorDisplayName
          // ignore: cast_nullable_to_non_nullable
          : actorDisplayName as String?,
      channel: channel == const $CopyWithPlaceholder()
          ? _value.channel
          // ignore: cast_nullable_to_non_nullable
          : channel as String,
      clientIp: clientIp == const $CopyWithPlaceholder()
          ? _value.clientIp
          // ignore: cast_nullable_to_non_nullable
          : clientIp as String?,
      changedFields: changedFields == const $CopyWithPlaceholder()
          ? _value.changedFields
          // ignore: cast_nullable_to_non_nullable
          : changedFields as List<String>,
      oldValues: oldValues == const $CopyWithPlaceholder()
          ? _value.oldValues
          // ignore: cast_nullable_to_non_nullable
          : oldValues as Map<String, Object?>?,
      newValues: newValues == const $CopyWithPlaceholder()
          ? _value.newValues
          // ignore: cast_nullable_to_non_nullable
          : newValues as Map<String, Object?>?,
      reasonKey: reasonKey == const $CopyWithPlaceholder()
          ? _value.reasonKey
          // ignore: cast_nullable_to_non_nullable
          : reasonKey as String?,
      reasonParams: reasonParams == const $CopyWithPlaceholder()
          ? _value.reasonParams
          // ignore: cast_nullable_to_non_nullable
          : reasonParams as Map<String, Object?>,
      note: note == const $CopyWithPlaceholder()
          ? _value.note
          // ignore: cast_nullable_to_non_nullable
          : note as String?,
    );
  }
}

extension $AuditEventViewCopyWith on AuditEventView {
  /// Returns a callable class that can be used as follows: `instanceOfAuditEventView.copyWith(...)` or like so:`instanceOfAuditEventView.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$AuditEventViewCWProxy get copyWith => _$AuditEventViewCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AuditEventView _$AuditEventViewFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('AuditEventView', json, ($checkedConvert) {
  $checkKeys(
    json,
    requiredKeys: const [
      'id',
      'occurredAt',
      'action',
      'actorType',
      'actorId',
      'channel',
      'changedFields',
      'reasonParams',
    ],
  );
  final val = AuditEventView(
    id: $checkedConvert('id', (v) => v as String),
    occurredAt: $checkedConvert(
      'occurredAt',
      (v) => DateTime.parse(v as String),
    ),
    action: $checkedConvert(
      'action',
      (v) => $enumDecode(
        _$AuditEventViewActionEnumEnumMap,
        v,
        unknownValue: AuditEventViewActionEnum.unknownDefaultOpenApi,
      ),
    ),
    entityType: $checkedConvert('entityType', (v) => v as String?),
    entityId: $checkedConvert('entityId', (v) => v as String?),
    entityLabel: $checkedConvert('entityLabel', (v) => v as String?),
    actorType: $checkedConvert(
      'actorType',
      (v) => $enumDecode(
        _$AuditEventViewActorTypeEnumEnumMap,
        v,
        unknownValue: AuditEventViewActorTypeEnum.unknownDefaultOpenApi,
      ),
    ),
    actorId: $checkedConvert('actorId', (v) => v as String),
    actorDisplayName: $checkedConvert('actorDisplayName', (v) => v as String?),
    channel: $checkedConvert('channel', (v) => v as String),
    clientIp: $checkedConvert('clientIp', (v) => v as String?),
    changedFields: $checkedConvert(
      'changedFields',
      (v) => (v as List<dynamic>).map((e) => e as String).toList(),
    ),
    oldValues: $checkedConvert('oldValues', (v) => v as Map<String, dynamic>?),
    newValues: $checkedConvert('newValues', (v) => v as Map<String, dynamic>?),
    reasonKey: $checkedConvert('reasonKey', (v) => v as String?),
    reasonParams: $checkedConvert(
      'reasonParams',
      (v) => v as Map<String, dynamic>,
    ),
    note: $checkedConvert('note', (v) => v as String?),
  );
  return val;
});

Map<String, dynamic> _$AuditEventViewToJson(AuditEventView instance) =>
    <String, dynamic>{
      'id': instance.id,
      'occurredAt': instance.occurredAt.toIso8601String(),
      'action': _$AuditEventViewActionEnumEnumMap[instance.action]!,
      'entityType': ?instance.entityType,
      'entityId': ?instance.entityId,
      'entityLabel': ?instance.entityLabel,
      'actorType': _$AuditEventViewActorTypeEnumEnumMap[instance.actorType]!,
      'actorId': instance.actorId,
      'actorDisplayName': ?instance.actorDisplayName,
      'channel': instance.channel,
      'clientIp': ?instance.clientIp,
      'changedFields': instance.changedFields,
      'oldValues': ?instance.oldValues,
      'newValues': ?instance.newValues,
      'reasonKey': ?instance.reasonKey,
      'reasonParams': instance.reasonParams,
      'note': ?instance.note,
    };

const _$AuditEventViewActionEnumEnumMap = {
  AuditEventViewActionEnum.CREATE: 'CREATE',
  AuditEventViewActionEnum.UPDATE: 'UPDATE',
  AuditEventViewActionEnum.DELETE: 'DELETE',
  AuditEventViewActionEnum.STATE_CHANGE: 'STATE_CHANGE',
  AuditEventViewActionEnum.LOGIN: 'LOGIN',
  AuditEventViewActionEnum.LOGIN_FAILED: 'LOGIN_FAILED',
  AuditEventViewActionEnum.LOGOUT: 'LOGOUT',
  AuditEventViewActionEnum.OTP_SENT: 'OTP_SENT',
  AuditEventViewActionEnum.OTP_FAILED: 'OTP_FAILED',
  AuditEventViewActionEnum.AUTHZ_DENIED: 'AUTHZ_DENIED',
  AuditEventViewActionEnum.PII_REVEAL: 'PII_REVEAL',
  AuditEventViewActionEnum.EXPORT: 'EXPORT',
  AuditEventViewActionEnum.IMPORT_COMMIT: 'IMPORT_COMMIT',
  AuditEventViewActionEnum.APPROVE: 'APPROVE',
  AuditEventViewActionEnum.REJECT: 'REJECT',
  AuditEventViewActionEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};

const _$AuditEventViewActorTypeEnumEnumMap = {
  AuditEventViewActorTypeEnum.USER: 'USER',
  AuditEventViewActorTypeEnum.SYSTEM: 'SYSTEM',
  AuditEventViewActorTypeEnum.DEVICE: 'DEVICE',
  AuditEventViewActorTypeEnum.PARTNER: 'PARTNER',
  AuditEventViewActorTypeEnum.unknownDefaultOpenApi: 'unknown_default_open_api',
};
