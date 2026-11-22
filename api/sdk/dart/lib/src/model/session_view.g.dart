// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'session_view.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$SessionViewCWProxy {
  SessionView subject(String subject);

  SessionView displayName(String? displayName);

  SessionView email(String? email);

  SessionView tenantSlug(String? tenantSlug);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SessionView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SessionView(...).copyWith(id: 12, name: "My name")
  /// ````
  SessionView call({
    String subject,
    String? displayName,
    String? email,
    String? tenantSlug,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfSessionView.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfSessionView.copyWith.fieldName(...)`
class _$SessionViewCWProxyImpl implements _$SessionViewCWProxy {
  const _$SessionViewCWProxyImpl(this._value);

  final SessionView _value;

  @override
  SessionView subject(String subject) => this(subject: subject);

  @override
  SessionView displayName(String? displayName) =>
      this(displayName: displayName);

  @override
  SessionView email(String? email) => this(email: email);

  @override
  SessionView tenantSlug(String? tenantSlug) => this(tenantSlug: tenantSlug);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SessionView(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SessionView(...).copyWith(id: 12, name: "My name")
  /// ````
  SessionView call({
    Object? subject = const $CopyWithPlaceholder(),
    Object? displayName = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? tenantSlug = const $CopyWithPlaceholder(),
  }) {
    return SessionView(
      subject: subject == const $CopyWithPlaceholder()
          ? _value.subject
          // ignore: cast_nullable_to_non_nullable
          : subject as String,
      displayName: displayName == const $CopyWithPlaceholder()
          ? _value.displayName
          // ignore: cast_nullable_to_non_nullable
          : displayName as String?,
      email: email == const $CopyWithPlaceholder()
          ? _value.email
          // ignore: cast_nullable_to_non_nullable
          : email as String?,
      tenantSlug: tenantSlug == const $CopyWithPlaceholder()
          ? _value.tenantSlug
          // ignore: cast_nullable_to_non_nullable
          : tenantSlug as String?,
    );
  }
}

extension $SessionViewCopyWith on SessionView {
  /// Returns a callable class that can be used as follows: `instanceOfSessionView.copyWith(...)` or like so:`instanceOfSessionView.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$SessionViewCWProxy get copyWith => _$SessionViewCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SessionView _$SessionViewFromJson(Map<String, dynamic> json) =>
    $checkedCreate('SessionView', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['subject']);
      final val = SessionView(
        subject: $checkedConvert('subject', (v) => v as String),
        displayName: $checkedConvert('displayName', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        tenantSlug: $checkedConvert('tenantSlug', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$SessionViewToJson(SessionView instance) =>
    <String, dynamic>{
      'subject': instance.subject,
      'displayName': ?instance.displayName,
      'email': ?instance.email,
      'tenantSlug': ?instance.tenantSlug,
    };
