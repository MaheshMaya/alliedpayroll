import 'package:allied_payroll/core/auth/workspace_code.dart';

/// The tokens of one signed-in workspace, as kept in secure storage.
final class AuthSession {
  const AuthSession({
    required this.workspace,
    required this.accessToken,
    required this.refreshToken,
    required this.idToken,
    required this.accessTokenExpiresAt,
  });

  factory AuthSession.fromJson(Map<String, Object?> json) {
    final workspace = WorkspaceCode.tryParse(json['workspace']! as String);
    if (workspace == null) {
      throw const FormatException('stored session has no workspace');
    }
    return AuthSession(
      workspace: workspace,
      accessToken: json['accessToken']! as String,
      refreshToken: json['refreshToken']! as String,
      idToken: json['idToken']! as String,
      accessTokenExpiresAt: DateTime.parse(
        json['accessTokenExpiresAt']! as String,
      ),
    );
  }

  final WorkspaceCode workspace;
  final String accessToken;
  final String refreshToken;
  final String idToken;
  final DateTime accessTokenExpiresAt;

  /// Treats a token as expired half a minute early so a request never lands
  /// with a dead one.
  bool needsRefreshAt(DateTime now) =>
      !now.isBefore(accessTokenExpiresAt.subtract(const Duration(seconds: 30)));

  Map<String, Object?> toJson() => {
    'workspace': workspace.value,
    'accessToken': accessToken,
    'refreshToken': refreshToken,
    'idToken': idToken,
    'accessTokenExpiresAt': accessTokenExpiresAt.toUtc().toIso8601String(),
  };
}
