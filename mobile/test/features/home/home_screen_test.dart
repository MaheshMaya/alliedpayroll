import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:allied_payroll/features/home/application/bootstrap_provider.dart';
import 'package:allied_payroll/features/home/presentation/home_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../support/fakes.dart';
import '../../support/pump_app.dart';

void main() {
  testWidgets('shows the tenant, the person, their mode and the sync policy', (
    tester,
  ) async {
    await tester.pumpApp(
      const HomeScreen(),
      overrides: [
        authControllerProvider.overrideWith(
          () => FakeAuthController(SignedIn(sentinelSession)),
        ),
        mobileBootstrapProvider.overrideWith(
          (ref) async => bootstrapFixture(permissions: {'attendance:approve'}),
        ),
      ],
    );
    await tester.pumpAndSettle();

    expect(find.text('Sentinel Security Services'), findsOneWidget);
    expect(find.text('Hello, Asha Rao'), findsOneWidget);
    expect(find.text('Supervisor'), findsOneWidget);
    expect(find.text('Refreshes every 15 minutes'), findsOneWidget);
    expect(find.text('Offline punches are kept for 7 days'), findsOneWidget);
  });

  testWidgets('sign out goes through the controller', (tester) async {
    final controller = FakeAuthController(SignedIn(sentinelSession));
    await tester.pumpApp(
      const HomeScreen(),
      overrides: [
        authControllerProvider.overrideWith(() => controller),
        mobileBootstrapProvider.overrideWith((ref) async => bootstrapFixture()),
      ],
    );
    await tester.pumpAndSettle();

    await tester.tap(find.text('Sign out'));
    expect(controller.signOuts, 1);
  });

  testWidgets('a failed load offers a retry', (tester) async {
    await tester.pumpApp(
      const HomeScreen(),
      overrides: [
        authControllerProvider.overrideWith(
          () => FakeAuthController(SignedIn(sentinelSession)),
        ),
        mobileBootstrapProvider.overrideWith(
          (ref) async => throw const EmptyResponse('getMobileBootstrap'),
        ),
      ],
    );
    await tester.pumpAndSettle();

    expect(find.text('Could not load your workspace.'), findsOneWidget);
    expect(find.widgetWithText(FilledButton, 'Try again'), findsOneWidget);
  });

  testWidgets('matches the golden', (tester) async {
    await tester.binding.setSurfaceSize(const Size(390, 844));
    addTearDown(() => tester.binding.setSurfaceSize(null));
    await tester.pumpApp(
      const HomeScreen(),
      overrides: [
        authControllerProvider.overrideWith(
          () => FakeAuthController(SignedIn(sentinelSession)),
        ),
        mobileBootstrapProvider.overrideWith((ref) async => bootstrapFixture()),
      ],
    );
    await tester.pumpAndSettle();

    await expectLater(
      find.byType(HomeScreen),
      matchesGoldenFile('goldens/home_screen.png'),
    );
  });
}
