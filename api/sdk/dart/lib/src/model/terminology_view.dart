//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:allied_api/src/model/term_view.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'terminology_view.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class TerminologyView {
  /// Returns a new [TerminologyView] instance.
  TerminologyView({required this.locale, required this.terms});

  @JsonKey(name: r'locale', required: true, includeIfNull: false)
  final String locale;

  @JsonKey(name: r'terms', required: true, includeIfNull: false)
  final List<TermView> terms;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is TerminologyView &&
          other.locale == locale &&
          other.terms == terms;

  @override
  int get hashCode => locale.hashCode + terms.hashCode;

  factory TerminologyView.fromJson(Map<String, dynamic> json) =>
      _$TerminologyViewFromJson(json);

  Map<String, dynamic> toJson() => _$TerminologyViewToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
