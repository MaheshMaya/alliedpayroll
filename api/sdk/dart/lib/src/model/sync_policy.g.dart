// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sync_policy.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$SyncPolicyCWProxy {
  SyncPolicy deltaInterval(String deltaInterval);

  SyncPolicy offlinePunchRetention(String offlinePunchRetention);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SyncPolicy(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SyncPolicy(...).copyWith(id: 12, name: "My name")
  /// ````
  SyncPolicy call({String deltaInterval, String offlinePunchRetention});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfSyncPolicy.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfSyncPolicy.copyWith.fieldName(...)`
class _$SyncPolicyCWProxyImpl implements _$SyncPolicyCWProxy {
  const _$SyncPolicyCWProxyImpl(this._value);

  final SyncPolicy _value;

  @override
  SyncPolicy deltaInterval(String deltaInterval) =>
      this(deltaInterval: deltaInterval);

  @override
  SyncPolicy offlinePunchRetention(String offlinePunchRetention) =>
      this(offlinePunchRetention: offlinePunchRetention);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SyncPolicy(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SyncPolicy(...).copyWith(id: 12, name: "My name")
  /// ````
  SyncPolicy call({
    Object? deltaInterval = const $CopyWithPlaceholder(),
    Object? offlinePunchRetention = const $CopyWithPlaceholder(),
  }) {
    return SyncPolicy(
      deltaInterval: deltaInterval == const $CopyWithPlaceholder()
          ? _value.deltaInterval
          // ignore: cast_nullable_to_non_nullable
          : deltaInterval as String,
      offlinePunchRetention:
          offlinePunchRetention == const $CopyWithPlaceholder()
          ? _value.offlinePunchRetention
          // ignore: cast_nullable_to_non_nullable
          : offlinePunchRetention as String,
    );
  }
}

extension $SyncPolicyCopyWith on SyncPolicy {
  /// Returns a callable class that can be used as follows: `instanceOfSyncPolicy.copyWith(...)` or like so:`instanceOfSyncPolicy.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$SyncPolicyCWProxy get copyWith => _$SyncPolicyCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SyncPolicy _$SyncPolicyFromJson(Map<String, dynamic> json) =>
    $checkedCreate('SyncPolicy', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const ['deltaInterval', 'offlinePunchRetention'],
      );
      final val = SyncPolicy(
        deltaInterval: $checkedConvert('deltaInterval', (v) => v as String),
        offlinePunchRetention: $checkedConvert(
          'offlinePunchRetention',
          (v) => v as String,
        ),
      );
      return val;
    });

Map<String, dynamic> _$SyncPolicyToJson(SyncPolicy instance) =>
    <String, dynamic>{
      'deltaInterval': instance.deltaInterval,
      'offlinePunchRetention': instance.offlinePunchRetention,
    };
