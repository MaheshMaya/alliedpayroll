//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'module_view.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ModuleView {
  /// Returns a new [ModuleView] instance.
  ModuleView({
    required this.code,

    required this.flagKey,

    required this.enabled,
  });

  @JsonKey(name: r'code', required: true, includeIfNull: false)
  final String code;

  @JsonKey(name: r'flagKey', required: true, includeIfNull: false)
  final String flagKey;

  @JsonKey(name: r'enabled', required: true, includeIfNull: false)
  final bool enabled;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is ModuleView &&
          other.code == code &&
          other.flagKey == flagKey &&
          other.enabled == enabled;

  @override
  int get hashCode => code.hashCode + flagKey.hashCode + enabled.hashCode;

  factory ModuleView.fromJson(Map<String, dynamic> json) =>
      _$ModuleViewFromJson(json);

  Map<String, dynamic> toJson() => _$ModuleViewToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
