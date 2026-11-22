import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/auth_gateway.dart';
import 'package:allied_payroll/core/auth/auth_session.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:allied_payroll/core/auth/workspace_code.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../support/fakes.dart';

class _Gateway implements AuthGateway {
  _Gateway({this.onSignIn, this.onRefresh});

  final Future<AuthSession> Function(WorkspaceCode)? onSignIn;
  final Future<AuthSession> Function(AuthSession)? onRefresh;

  @override
  Future<AuthSession> signIn(WorkspaceCode workspace) => onSignIn!(workspace);

  @override
  Future<AuthSession> refresh(AuthSession session) => onRefresh!(session);
}

ProviderContainer containerWith(AuthGateway gateway, MemorySessionStore store) {
  final container = ProviderContainer(
    overrides: [
      authGatewayProvider.overrideWithValue(gateway),
      sessionStoreProvider.overrideWithValue(store),
    ],
  );
  addTearDown(container.dispose);
  return container;
}

void main() {
  test('restores a stored session on start', () async {
    final container = containerWith(
      _Gateway(),
      MemorySessionStore(sentinelSession),
    );
    expect(container.read(authControllerProvider), isA<Unknown>());
    await Future<void>.delayed(Duration.zero);
    expect(container.read(authControllerProvider), isA<SignedIn>());
  });

  test('a successful sign-in is stored', () async {
    final store = MemorySessionStore();
    final container = containerWith(
      _Gateway(onSignIn: (_) async => sentinelSession),
      store,
    );
    await container.read(authControllerProvider.notifier).signIn(sentinel);
    expect(container.read(authControllerProvider), isA<SignedIn>());
    expect(store.session?.workspace, sentinel);
  });

  test('a cancelled sign-in is not an error', () async {
    final container = containerWith(
      _Gateway(onSignIn: (_) => throw const SignInCancelled()),
      MemorySessionStore(),
    );
    await container.read(authControllerProvider.notifier).signIn(sentinel);
    final state = container.read(authControllerProvider);
    expect(
      state,
      isA<SignedOut>().having((s) => s.lastAttemptFailed, 'failed', false),
    );
  });

  test('a failed sign-in says so', () async {
    final container = containerWith(
      _Gateway(
        onSignIn: (_) => throw const IncompleteTokenResponse('access token'),
      ),
      MemorySessionStore(),
    );
    await container.read(authControllerProvider.notifier).signIn(sentinel);
    final state = container.read(authControllerProvider);
    expect(
      state,
      isA<SignedOut>().having((s) => s.lastAttemptFailed, 'failed', true),
    );
  });

  AuthSession expiredSession() => AuthSession(
    workspace: sentinel,
    accessToken: 'old',
    refreshToken: 'r',
    idToken: 'i',
    accessTokenExpiresAt: DateTime.now().subtract(const Duration(minutes: 1)),
  );

  test('an expired token is refreshed before use', () async {
    final store = MemorySessionStore(expiredSession());
    final container = containerWith(
      _Gateway(onRefresh: (_) async => sentinelSession),
      store,
    );
    final controller = container.read(authControllerProvider.notifier);
    await Future<void>.delayed(Duration.zero);

    expect(await controller.accessToken(), 'access');
    expect(store.session?.accessToken, 'access');
  });

  test('a failed refresh signs the person out', () async {
    final store = MemorySessionStore(expiredSession());
    final container = containerWith(
      _Gateway(
        onRefresh: (_) => throw const IncompleteTokenResponse('access token'),
      ),
      store,
    );
    final controller = container.read(authControllerProvider.notifier);
    await Future<void>.delayed(Duration.zero);

    expect(await controller.accessToken(), isNull);
    expect(container.read(authControllerProvider), isA<SignedOut>());
    expect(store.session, isNull);
  });
}
