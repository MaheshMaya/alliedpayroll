// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_user_summary.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageResponseUserSummaryCWProxy {
  PageResponseUserSummary items(List<UserSummary> items);

  PageResponseUserSummary nextCursor(String? nextCursor);

  PageResponseUserSummary totalCount(int totalCount);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageResponseUserSummary(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageResponseUserSummary(...).copyWith(id: 12, name: "My name")
  /// ````
  PageResponseUserSummary call({
    List<UserSummary> items,
    String? nextCursor,
    int totalCount,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageResponseUserSummary.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageResponseUserSummary.copyWith.fieldName(...)`
class _$PageResponseUserSummaryCWProxyImpl
    implements _$PageResponseUserSummaryCWProxy {
  const _$PageResponseUserSummaryCWProxyImpl(this._value);

  final PageResponseUserSummary _value;

  @override
  PageResponseUserSummary items(List<UserSummary> items) => this(items: items);

  @override
  PageResponseUserSummary nextCursor(String? nextCursor) =>
      this(nextCursor: nextCursor);

  @override
  PageResponseUserSummary totalCount(int totalCount) =>
      this(totalCount: totalCount);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageResponseUserSummary(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageResponseUserSummary(...).copyWith(id: 12, name: "My name")
  /// ````
  PageResponseUserSummary call({
    Object? items = const $CopyWithPlaceholder(),
    Object? nextCursor = const $CopyWithPlaceholder(),
    Object? totalCount = const $CopyWithPlaceholder(),
  }) {
    return PageResponseUserSummary(
      items: items == const $CopyWithPlaceholder()
          ? _value.items
          // ignore: cast_nullable_to_non_nullable
          : items as List<UserSummary>,
      nextCursor: nextCursor == const $CopyWithPlaceholder()
          ? _value.nextCursor
          // ignore: cast_nullable_to_non_nullable
          : nextCursor as String?,
      totalCount: totalCount == const $CopyWithPlaceholder()
          ? _value.totalCount
          // ignore: cast_nullable_to_non_nullable
          : totalCount as int,
    );
  }
}

extension $PageResponseUserSummaryCopyWith on PageResponseUserSummary {
  /// Returns a callable class that can be used as follows: `instanceOfPageResponseUserSummary.copyWith(...)` or like so:`instanceOfPageResponseUserSummary.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageResponseUserSummaryCWProxy get copyWith =>
      _$PageResponseUserSummaryCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageResponseUserSummary _$PageResponseUserSummaryFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageResponseUserSummary', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['items', 'totalCount']);
  final val = PageResponseUserSummary(
    items: $checkedConvert(
      'items',
      (v) => (v as List<dynamic>)
          .map((e) => UserSummary.fromJson(e as Map<String, dynamic>))
          .toList(),
    ),
    nextCursor: $checkedConvert('nextCursor', (v) => v as String?),
    totalCount: $checkedConvert('totalCount', (v) => (v as num).toInt()),
  );
  return val;
});

Map<String, dynamic> _$PageResponseUserSummaryToJson(
  PageResponseUserSummary instance,
) => <String, dynamic>{
  'items': instance.items.map((e) => e.toJson()).toList(),
  'nextCursor': ?instance.nextCursor,
  'totalCount': instance.totalCount,
};
