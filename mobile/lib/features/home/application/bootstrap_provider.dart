import 'package:allied_api/allied_api.dart';
import 'package:allied_payroll/core/api/api_providers.dart';
import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'bootstrap_provider.g.dart';

/// Nothing was returned where the contract promises a body.
final class EmptyResponse implements Exception {
  const EmptyResponse(this.operation);

  final String operation;

  @override
  String toString() => '$operation returned no body';
}

/// The tenant, the person, their permissions and the sync policy, for the
/// signed-in workspace.
@riverpod
Future<MobileBootstrap> mobileBootstrap(Ref ref) async {
  final auth = ref.watch(authControllerProvider);
  if (auth is! SignedIn) throw StateError('bootstrap needs a signed-in person');
  final api = ref.watch(alliedApiProvider(auth.session.workspace));
  final response = await api.getMobileApi().getMobileBootstrap();
  return response.data ?? (throw const EmptyResponse('getMobileBootstrap'));
}
