import 'package:allied_payroll/core/auth/auth_session.dart';

sealed class AuthState {
  const AuthState();
}

/// Secure storage has not been read yet.
final class Unknown extends AuthState {
  const Unknown();
}

final class SignedOut extends AuthState {
  const SignedOut({this.lastAttemptFailed = false});

  /// True after a sign-in that ended in an error rather than a cancel.
  final bool lastAttemptFailed;
}

final class SignedIn extends AuthState {
  const SignedIn(this.session);

  final AuthSession session;
}
