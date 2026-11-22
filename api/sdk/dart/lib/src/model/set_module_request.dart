//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'set_module_request.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class SetModuleRequest {
  /// Returns a new [SetModuleRequest] instance.
  SetModuleRequest({required this.enabled});

  @JsonKey(name: r'enabled', required: true, includeIfNull: false)
  final bool enabled;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is SetModuleRequest && other.enabled == enabled;

  @override
  int get hashCode => enabled.hashCode;

  factory SetModuleRequest.fromJson(Map<String, dynamic> json) =>
      _$SetModuleRequestFromJson(json);

  Map<String, dynamic> toJson() => _$SetModuleRequestToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
