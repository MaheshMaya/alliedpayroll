//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:allied_api/src/model/user_summary.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'page_response_user_summary.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PageResponseUserSummary {
  /// Returns a new [PageResponseUserSummary] instance.
  PageResponseUserSummary({
    required this.items,

    this.nextCursor,

    required this.totalCount,
  });

  @JsonKey(name: r'items', required: true, includeIfNull: false)
  final List<UserSummary> items;

  @JsonKey(name: r'nextCursor', required: false, includeIfNull: false)
  final String? nextCursor;

  @JsonKey(name: r'totalCount', required: true, includeIfNull: false)
  final int totalCount;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is PageResponseUserSummary &&
          other.items == items &&
          other.nextCursor == nextCursor &&
          other.totalCount == totalCount;

  @override
  int get hashCode =>
      items.hashCode + nextCursor.hashCode + totalCount.hashCode;

  factory PageResponseUserSummary.fromJson(Map<String, dynamic> json) =>
      _$PageResponseUserSummaryFromJson(json);

  Map<String, dynamic> toJson() => _$PageResponseUserSummaryToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
