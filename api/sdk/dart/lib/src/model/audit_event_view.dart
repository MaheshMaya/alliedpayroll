//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'audit_event_view.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class AuditEventView {
  /// Returns a new [AuditEventView] instance.
  AuditEventView({
    required this.id,

    required this.occurredAt,

    required this.action,

    this.entityType,

    this.entityId,

    this.entityLabel,

    required this.actorType,

    required this.actorId,

    this.actorDisplayName,

    required this.channel,

    this.clientIp,

    required this.changedFields,

    this.oldValues,

    this.newValues,

    this.reasonKey,

    required this.reasonParams,

    this.note,
  });

  @JsonKey(name: r'id', required: true, includeIfNull: false)
  final String id;

  @JsonKey(name: r'occurredAt', required: true, includeIfNull: false)
  final DateTime occurredAt;

  @JsonKey(
    name: r'action',
    required: true,
    includeIfNull: false,
    unknownEnumValue: AuditEventViewActionEnum.unknownDefaultOpenApi,
  )
  final AuditEventViewActionEnum action;

  @JsonKey(name: r'entityType', required: false, includeIfNull: false)
  final String? entityType;

  @JsonKey(name: r'entityId', required: false, includeIfNull: false)
  final String? entityId;

  @JsonKey(name: r'entityLabel', required: false, includeIfNull: false)
  final String? entityLabel;

  @JsonKey(
    name: r'actorType',
    required: true,
    includeIfNull: false,
    unknownEnumValue: AuditEventViewActorTypeEnum.unknownDefaultOpenApi,
  )
  final AuditEventViewActorTypeEnum actorType;

  @JsonKey(name: r'actorId', required: true, includeIfNull: false)
  final String actorId;

  @JsonKey(name: r'actorDisplayName', required: false, includeIfNull: false)
  final String? actorDisplayName;

  @JsonKey(name: r'channel', required: true, includeIfNull: false)
  final String channel;

  @JsonKey(name: r'clientIp', required: false, includeIfNull: false)
  final String? clientIp;

  @JsonKey(name: r'changedFields', required: true, includeIfNull: false)
  final List<String> changedFields;

  @JsonKey(name: r'oldValues', required: false, includeIfNull: false)
  final Map<String, Object?>? oldValues;

  @JsonKey(name: r'newValues', required: false, includeIfNull: false)
  final Map<String, Object?>? newValues;

  @JsonKey(name: r'reasonKey', required: false, includeIfNull: false)
  final String? reasonKey;

  @JsonKey(name: r'reasonParams', required: true, includeIfNull: false)
  final Map<String, Object?> reasonParams;

  @JsonKey(name: r'note', required: false, includeIfNull: false)
  final String? note;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is AuditEventView &&
          other.id == id &&
          other.occurredAt == occurredAt &&
          other.action == action &&
          other.entityType == entityType &&
          other.entityId == entityId &&
          other.entityLabel == entityLabel &&
          other.actorType == actorType &&
          other.actorId == actorId &&
          other.actorDisplayName == actorDisplayName &&
          other.channel == channel &&
          other.clientIp == clientIp &&
          other.changedFields == changedFields &&
          other.oldValues == oldValues &&
          other.newValues == newValues &&
          other.reasonKey == reasonKey &&
          other.reasonParams == reasonParams &&
          other.note == note;

  @override
  int get hashCode =>
      id.hashCode +
      occurredAt.hashCode +
      action.hashCode +
      entityType.hashCode +
      entityId.hashCode +
      entityLabel.hashCode +
      actorType.hashCode +
      actorId.hashCode +
      actorDisplayName.hashCode +
      channel.hashCode +
      clientIp.hashCode +
      changedFields.hashCode +
      oldValues.hashCode +
      newValues.hashCode +
      reasonKey.hashCode +
      reasonParams.hashCode +
      note.hashCode;

  factory AuditEventView.fromJson(Map<String, dynamic> json) =>
      _$AuditEventViewFromJson(json);

  Map<String, dynamic> toJson() => _$AuditEventViewToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}

enum AuditEventViewActionEnum {
  @JsonValue(r'CREATE')
  CREATE(r'CREATE'),
  @JsonValue(r'UPDATE')
  UPDATE(r'UPDATE'),
  @JsonValue(r'DELETE')
  DELETE(r'DELETE'),
  @JsonValue(r'STATE_CHANGE')
  STATE_CHANGE(r'STATE_CHANGE'),
  @JsonValue(r'LOGIN')
  LOGIN(r'LOGIN'),
  @JsonValue(r'LOGIN_FAILED')
  LOGIN_FAILED(r'LOGIN_FAILED'),
  @JsonValue(r'LOGOUT')
  LOGOUT(r'LOGOUT'),
  @JsonValue(r'OTP_SENT')
  OTP_SENT(r'OTP_SENT'),
  @JsonValue(r'OTP_FAILED')
  OTP_FAILED(r'OTP_FAILED'),
  @JsonValue(r'AUTHZ_DENIED')
  AUTHZ_DENIED(r'AUTHZ_DENIED'),
  @JsonValue(r'PII_REVEAL')
  PII_REVEAL(r'PII_REVEAL'),
  @JsonValue(r'EXPORT')
  EXPORT(r'EXPORT'),
  @JsonValue(r'IMPORT_COMMIT')
  IMPORT_COMMIT(r'IMPORT_COMMIT'),
  @JsonValue(r'APPROVE')
  APPROVE(r'APPROVE'),
  @JsonValue(r'REJECT')
  REJECT(r'REJECT'),
  @JsonValue(r'unknown_default_open_api')
  unknownDefaultOpenApi(r'unknown_default_open_api');

  const AuditEventViewActionEnum(this.value);

  final String value;

  @override
  String toString() => value;
}

enum AuditEventViewActorTypeEnum {
  @JsonValue(r'USER')
  USER(r'USER'),
  @JsonValue(r'SYSTEM')
  SYSTEM(r'SYSTEM'),
  @JsonValue(r'DEVICE')
  DEVICE(r'DEVICE'),
  @JsonValue(r'PARTNER')
  PARTNER(r'PARTNER'),
  @JsonValue(r'unknown_default_open_api')
  unknownDefaultOpenApi(r'unknown_default_open_api');

  const AuditEventViewActorTypeEnum(this.value);

  final String value;

  @override
  String toString() => value;
}
