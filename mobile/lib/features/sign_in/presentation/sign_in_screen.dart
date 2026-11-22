import 'package:allied_payroll/app/router.dart';
import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/auth/auth_state.dart';
import 'package:allied_payroll/core/auth/workspace_code.dart';
import 'package:allied_payroll/l10n/generated/app_localizations.dart';
import 'package:allied_tokens/allied_tokens.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

class SignInScreen extends ConsumerStatefulWidget {
  const SignInScreen({super.key});

  @override
  ConsumerState<SignInScreen> createState() => _SignInScreenState();
}

class _SignInScreenState extends ConsumerState<SignInScreen> {
  final _workspace = TextEditingController();
  bool _invalid = false;
  bool _busy = false;

  @override
  void dispose() {
    _workspace.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    final workspace = WorkspaceCode.tryParse(_workspace.text);
    if (workspace == null) {
      setState(() => _invalid = true);
      return;
    }
    setState(() {
      _invalid = false;
      _busy = true;
    });
    await ref.read(authControllerProvider.notifier).signIn(workspace);
    if (mounted) setState(() => _busy = false);
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final colors = theme.extension<AlliedColors>()!;
    final auth = ref.watch(authControllerProvider);
    final failed = auth is SignedOut && auth.lastAttemptFailed;

    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(AlliedSpace.s6),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 420),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Text(l10n.appTitle, style: theme.textTheme.titleLarge),
                  const SizedBox(height: AlliedSpace.s2),
                  Text(
                    l10n.signInIntro,
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: colors.textMuted,
                    ),
                  ),
                  const SizedBox(height: AlliedSpace.s8),
                  TextField(
                    controller: _workspace,
                    autocorrect: false,
                    enableSuggestions: false,
                    textInputAction: TextInputAction.go,
                    keyboardType: TextInputType.url,
                    onSubmitted: (_) => _submit(),
                    decoration: InputDecoration(
                      labelText: l10n.workspaceLabel,
                      hintText: l10n.workspaceHint,
                      errorText: _invalid ? l10n.workspaceInvalid : null,
                    ),
                  ),
                  const SizedBox(height: AlliedSpace.s4),
                  FilledButton(
                    onPressed: _busy ? null : _submit,
                    child: Text(l10n.signIn),
                  ),
                  if (failed) ...[
                    const SizedBox(height: AlliedSpace.s4),
                    Text(
                      l10n.signInFailed,
                      style: theme.textTheme.bodyMedium?.copyWith(
                        color: colors.statusDangerFg,
                      ),
                    ),
                  ],
                  const SizedBox(height: AlliedSpace.s10),
                  TextButton(
                    onPressed: () => context.push(AppRoutes.kioskSetup),
                    child: Text(l10n.kioskSetupLink),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
