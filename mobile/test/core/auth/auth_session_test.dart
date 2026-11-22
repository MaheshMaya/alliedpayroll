import 'package:allied_payroll/core/auth/auth_session.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../support/fakes.dart';

void main() {
  test('round-trips through JSON', () {
    final restored = AuthSession.fromJson(sentinelSession.toJson());
    expect(restored.workspace, sentinelSession.workspace);
    expect(restored.accessToken, 'access');
    expect(restored.accessTokenExpiresAt, DateTime.utc(2030));
  });

  test('wants a refresh half a minute before expiry', () {
    final expiry = DateTime.utc(2030, 1, 1, 12);
    final session = AuthSession(
      workspace: sentinel,
      accessToken: 'a',
      refreshToken: 'r',
      idToken: 'i',
      accessTokenExpiresAt: expiry,
    );
    expect(
      session.needsRefreshAt(expiry.subtract(const Duration(minutes: 1))),
      isFalse,
    );
    expect(
      session.needsRefreshAt(expiry.subtract(const Duration(seconds: 29))),
      isTrue,
    );
  });

  test('a stored session without a workspace is rejected', () {
    expect(
      () =>
          AuthSession.fromJson({...sentinelSession.toJson(), 'workspace': '!'}),
      throwsFormatException,
    );
  });
}
