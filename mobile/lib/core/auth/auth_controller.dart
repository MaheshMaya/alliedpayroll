import 'dart:async';

import 'package:allied_payroll/app/environment.dart';
import 'package:allied_payroll/core/auth/auth_gateway.dart';
import 'package:allied_payroll/core/auth/auth_session.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:allied_payroll/core/auth/session_store.dart';
import 'package:allied_payroll/core/auth/workspace_code.dart';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'auth_controller.g.dart';

@Riverpod(keepAlive: true)
AuthGateway authGateway(Ref ref) =>
    const AppAuthGateway(FlutterAppAuth(), Environment.current);

@Riverpod(keepAlive: true)
SessionStore sessionStore(Ref ref) =>
    const SecureSessionStore(FlutterSecureStorage());

@Riverpod(keepAlive: true)
class AuthController extends _$AuthController {
  @override
  AuthState build() {
    unawaited(Future<void>.microtask(_restore));
    return const Unknown();
  }

  Future<void> _restore() async {
    final session = await ref.read(sessionStoreProvider).read();
    state = session == null ? const SignedOut() : SignedIn(session);
  }

  Future<void> signIn(WorkspaceCode workspace) async {
    try {
      final session = await ref.read(authGatewayProvider).signIn(workspace);
      await ref.read(sessionStoreProvider).write(session);
      state = SignedIn(session);
    } on SignInCancelled {
      state = const SignedOut();
    } on Exception {
      state = const SignedOut(lastAttemptFailed: true);
    }
  }

  Future<void> signOut() async {
    await ref.read(sessionStoreProvider).clear();
    state = const SignedOut();
  }

  /// A bearer token good for at least the next half minute, refreshed when
  /// needed. Null when the refresh fails, which also signs the person out.
  Future<String?> accessToken() async {
    final current = state;
    if (current is! SignedIn) return null;
    final session = current.session;
    if (!session.needsRefreshAt(DateTime.now())) return session.accessToken;
    try {
      final refreshed = await ref.read(authGatewayProvider).refresh(session);
      await ref.read(sessionStoreProvider).write(refreshed);
      state = SignedIn(refreshed);
      return refreshed.accessToken;
    } on Exception {
      await signOut();
      return null;
    }
  }

  AuthSession? get session => switch (state) {
    SignedIn(:final session) => session,
    _ => null,
  };
}
