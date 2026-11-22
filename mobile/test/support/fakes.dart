import 'package:allied_api/allied_api.dart';
import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/auth_session.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:allied_payroll/core/auth/session_store.dart';
import 'package:allied_payroll/core/auth/workspace_code.dart';

final WorkspaceCode sentinel = WorkspaceCode.tryParse('sentinel')!;

final sentinelSession = AuthSession(
  workspace: sentinel,
  accessToken: 'access',
  refreshToken: 'refresh',
  idToken: 'id',
  accessTokenExpiresAt: DateTime.utc(2030),
);

/// Starts in the given state and records the workspaces it was asked to sign
/// in to.
class FakeAuthController extends AuthController {
  FakeAuthController(this._initial);

  final AuthState _initial;
  final List<WorkspaceCode> signIns = [];
  int signOuts = 0;

  @override
  AuthState build() => _initial;

  @override
  Future<void> signIn(WorkspaceCode workspace) async {
    signIns.add(workspace);
  }

  @override
  Future<void> signOut() async {
    signOuts++;
    state = const SignedOut();
  }
}

class MemorySessionStore implements SessionStore {
  MemorySessionStore([this.session]);

  AuthSession? session;

  @override
  Future<AuthSession?> read() async => session;

  @override
  Future<void> write(AuthSession session) async => this.session = session;

  @override
  Future<void> clear() async => session = null;
}

MobileBootstrap bootstrapFixture({
  Set<String> permissions = const {},
  String displayName = 'Asha Rao',
}) {
  return MobileBootstrap(
    bootstrap: Bootstrap(
      tenant: TenantSummary(
        id: '0192f7a0-0000-7000-8000-000000000001',
        slug: 'sentinel',
        name: 'Sentinel Security Services',
        industryProfileCode: 'STAFFING',
        defaultLocale: 'en-IN',
        enabledLocales: const ['en-IN', 'ta-IN'],
        timezone: 'Asia/Kolkata',
      ),
      user: User(subject: 'u-1', displayName: displayName),
      permissions: permissions,
      features: const {'attendance': true},
    ),
    sync_: SyncPolicy(deltaInterval: 'PT15M', offlinePunchRetention: 'PT168H'),
  );
}
