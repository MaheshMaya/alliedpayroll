// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bootstrap.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$BootstrapCWProxy {
  Bootstrap tenant(TenantSummary tenant);

  Bootstrap user(User user);

  Bootstrap permissions(Set<String> permissions);

  Bootstrap features(Map<String, bool> features);

  Bootstrap terminology(Map<String, String> terminology);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Bootstrap(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Bootstrap(...).copyWith(id: 12, name: "My name")
  /// ````
  Bootstrap call({
    TenantSummary tenant,
    User user,
    Set<String> permissions,
    Map<String, bool> features,
    Map<String, String> terminology,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfBootstrap.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfBootstrap.copyWith.fieldName(...)`
class _$BootstrapCWProxyImpl implements _$BootstrapCWProxy {
  const _$BootstrapCWProxyImpl(this._value);

  final Bootstrap _value;

  @override
  Bootstrap tenant(TenantSummary tenant) => this(tenant: tenant);

  @override
  Bootstrap user(User user) => this(user: user);

  @override
  Bootstrap permissions(Set<String> permissions) =>
      this(permissions: permissions);

  @override
  Bootstrap features(Map<String, bool> features) => this(features: features);

  @override
  Bootstrap terminology(Map<String, String> terminology) =>
      this(terminology: terminology);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Bootstrap(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Bootstrap(...).copyWith(id: 12, name: "My name")
  /// ````
  Bootstrap call({
    Object? tenant = const $CopyWithPlaceholder(),
    Object? user = const $CopyWithPlaceholder(),
    Object? permissions = const $CopyWithPlaceholder(),
    Object? features = const $CopyWithPlaceholder(),
    Object? terminology = const $CopyWithPlaceholder(),
  }) {
    return Bootstrap(
      tenant: tenant == const $CopyWithPlaceholder()
          ? _value.tenant
          // ignore: cast_nullable_to_non_nullable
          : tenant as TenantSummary,
      user: user == const $CopyWithPlaceholder()
          ? _value.user
          // ignore: cast_nullable_to_non_nullable
          : user as User,
      permissions: permissions == const $CopyWithPlaceholder()
          ? _value.permissions
          // ignore: cast_nullable_to_non_nullable
          : permissions as Set<String>,
      features: features == const $CopyWithPlaceholder()
          ? _value.features
          // ignore: cast_nullable_to_non_nullable
          : features as Map<String, bool>,
      terminology: terminology == const $CopyWithPlaceholder()
          ? _value.terminology
          // ignore: cast_nullable_to_non_nullable
          : terminology as Map<String, String>,
    );
  }
}

extension $BootstrapCopyWith on Bootstrap {
  /// Returns a callable class that can be used as follows: `instanceOfBootstrap.copyWith(...)` or like so:`instanceOfBootstrap.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$BootstrapCWProxy get copyWith => _$BootstrapCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Bootstrap _$BootstrapFromJson(Map<String, dynamic> json) =>
    $checkedCreate('Bootstrap', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const [
          'tenant',
          'user',
          'permissions',
          'features',
          'terminology',
        ],
      );
      final val = Bootstrap(
        tenant: $checkedConvert(
          'tenant',
          (v) => TenantSummary.fromJson(v as Map<String, dynamic>),
        ),
        user: $checkedConvert(
          'user',
          (v) => User.fromJson(v as Map<String, dynamic>),
        ),
        permissions: $checkedConvert(
          'permissions',
          (v) => (v as List<dynamic>).map((e) => e as String).toSet(),
        ),
        features: $checkedConvert(
          'features',
          (v) => Map<String, bool>.from(v as Map),
        ),
        terminology: $checkedConvert(
          'terminology',
          (v) => Map<String, String>.from(v as Map),
        ),
      );
      return val;
    });

Map<String, dynamic> _$BootstrapToJson(Bootstrap instance) => <String, dynamic>{
  'tenant': instance.tenant.toJson(),
  'user': instance.user.toJson(),
  'permissions': instance.permissions.toList(),
  'features': instance.features,
  'terminology': instance.terminology,
};
