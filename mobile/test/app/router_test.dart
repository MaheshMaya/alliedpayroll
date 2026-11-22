import 'package:allied_payroll/app/router.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:flutter_test/flutter_test.dart';

import '../support/fakes.dart';

void main() {
  test('waits on the splash until storage is read', () {
    expect(redirectFor(const Unknown(), AppRoutes.home), AppRoutes.splash);
    expect(redirectFor(const Unknown(), AppRoutes.splash), isNull);
  });

  test('signed out people see sign-in or kiosk setup only', () {
    expect(redirectFor(const SignedOut(), AppRoutes.home), AppRoutes.signIn);
    expect(redirectFor(const SignedOut(), AppRoutes.signIn), isNull);
    expect(redirectFor(const SignedOut(), AppRoutes.kioskSetup), isNull);
  });

  test('signed in people leave sign-in and splash for home', () {
    final signedIn = SignedIn(sentinelSession);
    expect(redirectFor(signedIn, AppRoutes.signIn), AppRoutes.home);
    expect(redirectFor(signedIn, AppRoutes.splash), AppRoutes.home);
    expect(redirectFor(signedIn, AppRoutes.home), isNull);
  });
}
