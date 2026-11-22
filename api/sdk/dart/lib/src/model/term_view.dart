//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'term_view.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class TermView {
  /// Returns a new [TermView] instance.
  TermView({
    required this.key,

    required this.label,

    required this.defaultLabel,

    required this.overridden,
  });

  @JsonKey(name: r'key', required: true, includeIfNull: false)
  final String key;

  @JsonKey(name: r'label', required: true, includeIfNull: false)
  final String label;

  @JsonKey(name: r'defaultLabel', required: true, includeIfNull: false)
  final String defaultLabel;

  @JsonKey(name: r'overridden', required: true, includeIfNull: false)
  final bool overridden;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is TermView &&
          other.key == key &&
          other.label == label &&
          other.defaultLabel == defaultLabel &&
          other.overridden == overridden;

  @override
  int get hashCode =>
      key.hashCode +
      label.hashCode +
      defaultLabel.hashCode +
      overridden.hashCode;

  factory TermView.fromJson(Map<String, dynamic> json) =>
      _$TermViewFromJson(json);

  Map<String, dynamic> toJson() => _$TermViewToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
