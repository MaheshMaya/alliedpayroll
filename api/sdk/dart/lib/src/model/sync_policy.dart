//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'sync_policy.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class SyncPolicy {
  /// Returns a new [SyncPolicy] instance.
  SyncPolicy({
    required this.deltaInterval,

    required this.offlinePunchRetention,
  });

  @JsonKey(name: r'deltaInterval', required: true, includeIfNull: false)
  final String deltaInterval;

  @JsonKey(name: r'offlinePunchRetention', required: true, includeIfNull: false)
  final String offlinePunchRetention;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is SyncPolicy &&
          other.deltaInterval == deltaInterval &&
          other.offlinePunchRetention == offlinePunchRetention;

  @override
  int get hashCode => deltaInterval.hashCode + offlinePunchRetention.hashCode;

  factory SyncPolicy.fromJson(Map<String, dynamic> json) =>
      _$SyncPolicyFromJson(json);

  Map<String, dynamic> toJson() => _$SyncPolicyToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
