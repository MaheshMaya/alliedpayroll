import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:allied_payroll/features/home/presentation/home_screen.dart';
import 'package:allied_payroll/features/kiosk/presentation/kiosk_setup_screen.dart';
import 'package:allied_payroll/features/sign_in/presentation/sign_in_screen.dart';
import 'package:allied_payroll/features/sign_in/presentation/splash_screen.dart';
import 'package:flutter/foundation.dart';
import 'package:go_router/go_router.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'router.g.dart';

abstract final class AppRoutes {
  static const home = '/';
  static const signIn = '/sign-in';
  static const splash = '/splash';
  static const kioskSetup = '/kiosk/setup';

  /// Reachable without a signed-in person.
  static const Set<String> public = {signIn, kioskSetup};
}

@riverpod
GoRouter router(Ref ref) {
  final auth = ValueNotifier<AuthState>(ref.read(authControllerProvider));
  ref
    ..listen(authControllerProvider, (_, next) => auth.value = next)
    ..onDispose(auth.dispose);

  return GoRouter(
    initialLocation: AppRoutes.splash,
    refreshListenable: auth,
    redirect: (context, state) =>
        redirectFor(auth.value, state.matchedLocation),
    routes: [
      GoRoute(
        path: AppRoutes.splash,
        builder: (context, state) => const SplashScreen(),
      ),
      GoRoute(
        path: AppRoutes.signIn,
        builder: (context, state) => const SignInScreen(),
      ),
      GoRoute(
        path: AppRoutes.kioskSetup,
        builder: (context, state) => const KioskSetupScreen(),
      ),
      GoRoute(
        path: AppRoutes.home,
        builder: (context, state) => const HomeScreen(),
      ),
    ],
  );
}

/// Where a navigation ends up given the sign-in state; null keeps the
/// requested location.
@visibleForTesting
String? redirectFor(AuthState auth, String location) {
  return switch (auth) {
    Unknown() => location == AppRoutes.splash ? null : AppRoutes.splash,
    SignedOut() =>
      AppRoutes.public.contains(location) ? null : AppRoutes.signIn,
    SignedIn() =>
      location == AppRoutes.signIn || location == AppRoutes.splash
          ? AppRoutes.home
          : null,
  };
}
