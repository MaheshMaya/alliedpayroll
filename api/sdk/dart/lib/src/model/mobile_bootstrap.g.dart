// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'mobile_bootstrap.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$MobileBootstrapCWProxy {
  MobileBootstrap bootstrap(Bootstrap bootstrap);

  MobileBootstrap sync_(SyncPolicy sync_);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MobileBootstrap(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MobileBootstrap(...).copyWith(id: 12, name: "My name")
  /// ````
  MobileBootstrap call({Bootstrap bootstrap, SyncPolicy sync_});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfMobileBootstrap.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfMobileBootstrap.copyWith.fieldName(...)`
class _$MobileBootstrapCWProxyImpl implements _$MobileBootstrapCWProxy {
  const _$MobileBootstrapCWProxyImpl(this._value);

  final MobileBootstrap _value;

  @override
  MobileBootstrap bootstrap(Bootstrap bootstrap) => this(bootstrap: bootstrap);

  @override
  MobileBootstrap sync_(SyncPolicy sync_) => this(sync_: sync_);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MobileBootstrap(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MobileBootstrap(...).copyWith(id: 12, name: "My name")
  /// ````
  MobileBootstrap call({
    Object? bootstrap = const $CopyWithPlaceholder(),
    Object? sync_ = const $CopyWithPlaceholder(),
  }) {
    return MobileBootstrap(
      bootstrap: bootstrap == const $CopyWithPlaceholder()
          ? _value.bootstrap
          // ignore: cast_nullable_to_non_nullable
          : bootstrap as Bootstrap,
      sync_: sync_ == const $CopyWithPlaceholder()
          ? _value.sync_
          // ignore: cast_nullable_to_non_nullable
          : sync_ as SyncPolicy,
    );
  }
}

extension $MobileBootstrapCopyWith on MobileBootstrap {
  /// Returns a callable class that can be used as follows: `instanceOfMobileBootstrap.copyWith(...)` or like so:`instanceOfMobileBootstrap.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$MobileBootstrapCWProxy get copyWith => _$MobileBootstrapCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MobileBootstrap _$MobileBootstrapFromJson(Map<String, dynamic> json) =>
    $checkedCreate('MobileBootstrap', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['bootstrap', 'sync']);
      final val = MobileBootstrap(
        bootstrap: $checkedConvert(
          'bootstrap',
          (v) => Bootstrap.fromJson(v as Map<String, dynamic>),
        ),
        sync_: $checkedConvert(
          'sync',
          (v) => SyncPolicy.fromJson(v as Map<String, dynamic>),
        ),
      );
      return val;
    }, fieldKeyMap: const {'sync_': 'sync'});

Map<String, dynamic> _$MobileBootstrapToJson(MobileBootstrap instance) =>
    <String, dynamic>{
      'bootstrap': instance.bootstrap.toJson(),
      'sync': instance.sync_.toJson(),
    };
