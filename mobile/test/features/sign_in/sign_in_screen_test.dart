import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:allied_payroll/features/sign_in/presentation/sign_in_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../support/fakes.dart';
import '../../support/pump_app.dart';

void main() {
  testWidgets('an invalid workspace code is refused before any sign-in', (
    tester,
  ) async {
    final controller = FakeAuthController(const SignedOut());
    await tester.pumpApp(
      const SignInScreen(),
      overrides: [authControllerProvider.overrideWith(() => controller)],
    );

    await tester.enterText(find.byType(TextField), 'Sentinel Security');
    await tester.tap(find.widgetWithText(FilledButton, 'Sign in'));
    await tester.pump();

    expect(
      find.text('Use lower-case letters, digits and hyphens only.'),
      findsOneWidget,
    );
    expect(controller.signIns, isEmpty);
  });

  testWidgets('a valid code is normalised and handed to the controller', (
    tester,
  ) async {
    final controller = FakeAuthController(const SignedOut());
    await tester.pumpApp(
      const SignInScreen(),
      overrides: [authControllerProvider.overrideWith(() => controller)],
    );

    await tester.enterText(find.byType(TextField), ' Sentinel ');
    await tester.tap(find.widgetWithText(FilledButton, 'Sign in'));
    await tester.pump();

    expect(controller.signIns.map((w) => w.value), ['sentinel']);
  });

  testWidgets('a failed attempt is explained', (tester) async {
    await tester.pumpApp(
      const SignInScreen(),
      overrides: [
        authControllerProvider.overrideWith(
          () => FakeAuthController(const SignedOut(lastAttemptFailed: true)),
        ),
      ],
    );
    expect(find.textContaining('Sign-in did not complete'), findsOneWidget);
  });

  testWidgets('renders in Tamil', (tester) async {
    await tester.pumpApp(
      const SignInScreen(),
      overrides: [
        authControllerProvider.overrideWith(
          () => FakeAuthController(const SignedOut()),
        ),
      ],
      locale: const Locale('ta'),
    );
    expect(find.text('உள்நுழைக'), findsOneWidget);
  });
}
