import 'package:flutter/foundation.dart';

/// The tenant's slug as typed on the sign-in screen. Same shape the backend's
/// host resolver accepts.
@immutable
final class WorkspaceCode {
  const WorkspaceCode._(this.value);

  static final RegExp _shape = RegExp(
    r'^[a-z0-9](?:[a-z0-9-]{1,38}[a-z0-9])?$',
  );

  /// Null when the text is not a workspace code; whitespace and case are
  /// forgiven.
  static WorkspaceCode? tryParse(String input) {
    final normalised = input.trim().toLowerCase();
    return _shape.hasMatch(normalised) ? WorkspaceCode._(normalised) : null;
  }

  final String value;

  /// The Keycloak scope that puts this organisation into the token.
  String get organizationScope => 'organization:$value';

  @override
  bool operator ==(Object other) =>
      other is WorkspaceCode && other.value == value;

  @override
  int get hashCode => value.hashCode;

  @override
  String toString() => value;
}
