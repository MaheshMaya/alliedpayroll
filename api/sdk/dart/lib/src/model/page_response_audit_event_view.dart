//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:allied_api/src/model/audit_event_view.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'page_response_audit_event_view.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PageResponseAuditEventView {
  /// Returns a new [PageResponseAuditEventView] instance.
  PageResponseAuditEventView({
    required this.items,

    this.nextCursor,

    required this.totalCount,
  });

  @JsonKey(name: r'items', required: true, includeIfNull: false)
  final List<AuditEventView> items;

  @JsonKey(name: r'nextCursor', required: false, includeIfNull: false)
  final String? nextCursor;

  @JsonKey(name: r'totalCount', required: true, includeIfNull: false)
  final int totalCount;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is PageResponseAuditEventView &&
          other.items == items &&
          other.nextCursor == nextCursor &&
          other.totalCount == totalCount;

  @override
  int get hashCode =>
      items.hashCode + nextCursor.hashCode + totalCount.hashCode;

  factory PageResponseAuditEventView.fromJson(Map<String, dynamic> json) =>
      _$PageResponseAuditEventViewFromJson(json);

  Map<String, dynamic> toJson() => _$PageResponseAuditEventViewToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
