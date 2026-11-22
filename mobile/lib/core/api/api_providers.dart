import 'package:allied_api/allied_api.dart';
import 'package:allied_payroll/app/environment.dart';
import 'package:allied_payroll/core/api/api_client.dart';
import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/workspace_code.dart';
import 'package:allied_payroll/core/i18n/app_locale.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'api_providers.g.dart';

/// The generated client bound to one workspace's host.
/// Every feature reads the API through it.
@riverpod
AlliedApi alliedApi(Ref ref, WorkspaceCode workspace) {
  final auth = ref.read(authControllerProvider.notifier);
  final dio = buildApiDio(
    baseUrl: Environment.current.apiBaseUrl(workspace),
    accessToken: auth.accessToken,
    languageTag: () => ref.read(appLocaleProvider).toLanguageTag(),
    appVersion: Environment.current.appVersion,
    onUnauthorized: auth.signOut,
  );
  ref.onDispose(dio.close);
  return AlliedApi(dio: dio, interceptors: const []);
}
