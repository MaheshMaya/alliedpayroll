import 'package:allied_payroll/app/environment.dart';
import 'package:allied_payroll/core/auth/auth_session.dart';
import 'package:allied_payroll/core/auth/workspace_code.dart';
import 'package:flutter_appauth/flutter_appauth.dart';

/// The person closed the browser before finishing.
final class SignInCancelled implements Exception {
  const SignInCancelled();
}

/// The identity provider answered without one of the tokens the app needs.
final class IncompleteTokenResponse implements Exception {
  const IncompleteTokenResponse(this.missing);

  final String missing;

  @override
  String toString() => 'token response has no $missing';
}

/// OIDC Authorization Code with PKCE against Keycloak.
/// The vendor API stays behind this port.
abstract interface class AuthGateway {
  Future<AuthSession> signIn(WorkspaceCode workspace);

  Future<AuthSession> refresh(AuthSession session);
}

final class AppAuthGateway implements AuthGateway {
  const AppAuthGateway(this._appAuth, this._environment);

  final FlutterAppAuth _appAuth;
  final Environment _environment;

  static const _baseScopes = ['openid', 'profile', 'email'];

  @override
  Future<AuthSession> signIn(WorkspaceCode workspace) async {
    final AuthorizationTokenResponse response;
    try {
      response = await _appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          _environment.oidcClientId,
          _environment.oidcRedirectUri,
          issuer: _environment.oidcIssuer,
          scopes: [..._baseScopes, workspace.organizationScope],
        ),
      );
    } on FlutterAppAuthUserCancelledException {
      throw const SignInCancelled();
    }
    return _toSession(workspace, response);
  }

  @override
  Future<AuthSession> refresh(AuthSession session) async {
    final response = await _appAuth.token(
      TokenRequest(
        _environment.oidcClientId,
        _environment.oidcRedirectUri,
        issuer: _environment.oidcIssuer,
        refreshToken: session.refreshToken,
        scopes: [..._baseScopes, session.workspace.organizationScope],
      ),
    );
    return _toSession(session.workspace, response, previous: session);
  }

  static AuthSession _toSession(
    WorkspaceCode workspace,
    TokenResponse response, {
    AuthSession? previous,
  }) {
    final accessToken = response.accessToken;
    if (accessToken == null) {
      throw const IncompleteTokenResponse('access token');
    }
    final expiresAt = response.accessTokenExpirationDateTime;
    if (expiresAt == null) throw const IncompleteTokenResponse('expiry');
    final refreshToken = response.refreshToken ?? previous?.refreshToken;
    if (refreshToken == null) {
      throw const IncompleteTokenResponse('refresh token');
    }
    final idToken = response.idToken ?? previous?.idToken;
    if (idToken == null) throw const IncompleteTokenResponse('id token');
    return AuthSession(
      workspace: workspace,
      accessToken: accessToken,
      refreshToken: refreshToken,
      idToken: idToken,
      accessTokenExpiresAt: expiresAt,
    );
  }
}
