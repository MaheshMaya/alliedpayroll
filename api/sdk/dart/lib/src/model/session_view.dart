//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'session_view.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class SessionView {
  /// Returns a new [SessionView] instance.
  SessionView({
    required this.subject,

    this.displayName,

    this.email,

    this.tenantSlug,
  });

  @JsonKey(name: r'subject', required: true, includeIfNull: false)
  final String subject;

  @JsonKey(name: r'displayName', required: false, includeIfNull: false)
  final String? displayName;

  @JsonKey(name: r'email', required: false, includeIfNull: false)
  final String? email;

  @JsonKey(name: r'tenantSlug', required: false, includeIfNull: false)
  final String? tenantSlug;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is SessionView &&
          other.subject == subject &&
          other.displayName == displayName &&
          other.email == email &&
          other.tenantSlug == tenantSlug;

  @override
  int get hashCode =>
      subject.hashCode +
      displayName.hashCode +
      email.hashCode +
      tenantSlug.hashCode;

  factory SessionView.fromJson(Map<String, dynamic> json) =>
      _$SessionViewFromJson(json);

  Map<String, dynamic> toJson() => _$SessionViewToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
