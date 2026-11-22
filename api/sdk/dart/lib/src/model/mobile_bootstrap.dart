//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:allied_api/src/model/bootstrap.dart';
import 'package:allied_api/src/model/sync_policy.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'mobile_bootstrap.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class MobileBootstrap {
  /// Returns a new [MobileBootstrap] instance.
  MobileBootstrap({required this.bootstrap, required this.sync_});

  @JsonKey(name: r'bootstrap', required: true, includeIfNull: false)
  final Bootstrap bootstrap;

  @JsonKey(name: r'sync', required: true, includeIfNull: false)
  final SyncPolicy sync_;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is MobileBootstrap &&
          other.bootstrap == bootstrap &&
          other.sync_ == sync_;

  @override
  int get hashCode => bootstrap.hashCode + sync_.hashCode;

  factory MobileBootstrap.fromJson(Map<String, dynamic> json) =>
      _$MobileBootstrapFromJson(json);

  Map<String, dynamic> toJson() => _$MobileBootstrapToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
