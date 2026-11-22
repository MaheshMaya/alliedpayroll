import 'package:allied_payroll/core/auth/workspace_code.dart';

/// Build-time configuration from `--dart-define-from-file=env/<flavor>.json`.
/// The defaults are the local compose stack.
final class Environment {
  const Environment({
    required this.flavor,
    required this.apiScheme,
    required this.apiDomain,
    required this.oidcIssuer,
    required this.oidcClientId,
    required this.oidcRedirectUri,
    required this.appVersion,
  });

  static const Environment current = Environment(
    flavor: String.fromEnvironment('FLAVOR', defaultValue: 'dev'),
    apiScheme: String.fromEnvironment('API_SCHEME', defaultValue: 'http'),
    apiDomain: String.fromEnvironment(
      'API_DOMAIN',
      defaultValue: 'localhost:8088',
    ),
    oidcIssuer: String.fromEnvironment(
      'OIDC_ISSUER',
      defaultValue: 'http://auth.localhost:8081/realms/alliedpayroll',
    ),
    oidcClientId: String.fromEnvironment(
      'OIDC_CLIENT_ID',
      defaultValue: 'alliedpayroll-mobile',
    ),
    oidcRedirectUri: String.fromEnvironment(
      'OIDC_REDIRECT_URI',
      defaultValue: 'com.alliedpayroll.app://callback',
    ),
    appVersion: String.fromEnvironment('APP_VERSION', defaultValue: '0.1.0'),
  );

  final String flavor;
  final String apiScheme;
  final String apiDomain;
  final String oidcIssuer;
  final String oidcClientId;
  final String oidcRedirectUri;
  final String appVersion;

  /// The backend resolves the tenant from the first host label.
  Uri apiBaseUrl(WorkspaceCode workspace) =>
      Uri.parse('$apiScheme://${workspace.value}.$apiDomain');
}
