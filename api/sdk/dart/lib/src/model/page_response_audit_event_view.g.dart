// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_audit_event_view.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageResponseAuditEventViewCWProxy {
  PageResponseAuditEventView items(List<AuditEventView> items);

  PageResponseAuditEventView nextCursor(String? nextCursor);

  PageResponseAuditEventView totalCount(int totalCount);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageResponseAuditEventView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageResponseAuditEventView(...).copyWith(id: 12, name: "My name")
  /// ````
  PageResponseAuditEventView call({
    List<AuditEventView> items,
    String? nextCursor,
    int totalCount,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageResponseAuditEventView.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageResponseAuditEventView.copyWith.fieldName(...)`
class _$PageResponseAuditEventViewCWProxyImpl
    implements _$PageResponseAuditEventViewCWProxy {
  const _$PageResponseAuditEventViewCWProxyImpl(this._value);

  final PageResponseAuditEventView _value;

  @override
  PageResponseAuditEventView items(List<AuditEventView> items) =>
      this(items: items);

  @override
  PageResponseAuditEventView nextCursor(String? nextCursor) =>
      this(nextCursor: nextCursor);

  @override
  PageResponseAuditEventView totalCount(int totalCount) =>
      this(totalCount: totalCount);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageResponseAuditEventView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageResponseAuditEventView(...).copyWith(id: 12, name: "My name")
  /// ````
  PageResponseAuditEventView call({
    Object? items = const $CopyWithPlaceholder(),
    Object? nextCursor = const $CopyWithPlaceholder(),
    Object? totalCount = const $CopyWithPlaceholder(),
  }) {
    return PageResponseAuditEventView(
      items: items == const $CopyWithPlaceholder()
          ? _value.items
          // ignore: cast_nullable_to_non_nullable
          : items as List<AuditEventView>,
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

extension $PageResponseAuditEventViewCopyWith on PageResponseAuditEventView {
  /// Returns a callable class that can be used as follows: `instanceOfPageResponseAuditEventView.copyWith(...)` or like so:`instanceOfPageResponseAuditEventView.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageResponseAuditEventViewCWProxy get copyWith =>
      _$PageResponseAuditEventViewCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageResponseAuditEventView _$PageResponseAuditEventViewFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageResponseAuditEventView', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['items', 'totalCount']);
  final val = PageResponseAuditEventView(
    items: $checkedConvert(
      'items',
      (v) => (v as List<dynamic>)
          .map((e) => AuditEventView.fromJson(e as Map<String, dynamic>))
          .toList(),
    ),
    nextCursor: $checkedConvert('nextCursor', (v) => v as String?),
    totalCount: $checkedConvert('totalCount', (v) => (v as num).toInt()),
  );
  return val;
});

Map<String, dynamic> _$PageResponseAuditEventViewToJson(
  PageResponseAuditEventView instance,
) => <String, dynamic>{
  'items': instance.items.map((e) => e.toJson()).toList(),
  'nextCursor': ?instance.nextCursor,
  'totalCount': instance.totalCount,
};
