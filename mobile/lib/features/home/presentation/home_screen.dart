import 'package:allied_api/allied_api.dart';
import 'package:allied_payroll/core/auth/auth_controller.dart';
import 'package:allied_payroll/core/mode/app_mode.dart';
import 'package:allied_payroll/core/time/iso_duration.dart';
import 'package:allied_payroll/features/home/application/bootstrap_provider.dart';
import 'package:allied_payroll/l10n/generated/app_localizations.dart';
import 'package:allied_tokens/allied_tokens.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final bootstrap = ref.watch(mobileBootstrapProvider);
    return Scaffold(
      appBar: AppBar(
        title: Text(bootstrap.value?.bootstrap.tenant.name ?? l10n.appTitle),
        actions: [
          TextButton(
            onPressed: () =>
                ref.read(authControllerProvider.notifier).signOut(),
            child: Text(l10n.signOut),
          ),
        ],
      ),
      body: switch (bootstrap) {
        AsyncData(:final value) => _HomeBody(value),
        AsyncError() => _LoadFailed(
          onRetry: () => ref.invalidate(mobileBootstrapProvider),
        ),
        _ => const Center(child: CircularProgressIndicator()),
      },
    );
  }
}

class _HomeBody extends StatelessWidget {
  const _HomeBody(this.data);

  final MobileBootstrap data;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final colors = theme.extension<AlliedColors>()!;
    final user = data.bootstrap.user;
    final mode = resolveMode(data.bootstrap.permissions);
    final sync = parseIsoDuration(data.sync_.deltaInterval);
    final retention = parseIsoDuration(data.sync_.offlinePunchRetention);

    return ListView(
      padding: const EdgeInsets.all(AlliedSpace.s4),
      children: [
        Text(
          l10n.homeGreeting(user.displayName ?? user.subject),
          style: theme.textTheme.titleLarge,
        ),
        const SizedBox(height: AlliedSpace.s2),
        Align(
          alignment: AlignmentDirectional.centerStart,
          child: Chip(
            label: Text(switch (mode) {
              AppMode.manager => l10n.modeManager,
              AppMode.employee || AppMode.kiosk => l10n.modeEmployee,
            }),
            backgroundColor: colors.bgSelected,
            side: BorderSide.none,
          ),
        ),
        const SizedBox(height: AlliedSpace.s6),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(AlliedSpace.s4),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(l10n.syncTitle, style: theme.textTheme.labelLarge),
                const SizedBox(height: AlliedSpace.s2),
                Text(l10n.syncEvery(sync.inMinutes)),
                const SizedBox(height: AlliedSpace.s1),
                Text(l10n.offlineRetention(retention.inDays)),
              ],
            ),
          ),
        ),
        const SizedBox(height: AlliedSpace.s4),
        Text(
          l10n.homeEmptyHint,
          style: theme.textTheme.bodySmall?.copyWith(color: colors.textMuted),
        ),
      ],
    );
  }
}

class _LoadFailed extends StatelessWidget {
  const _LoadFailed({required this.onRetry});

  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AlliedSpace.s6),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(l10n.loadFailed, textAlign: TextAlign.center),
            const SizedBox(height: AlliedSpace.s4),
            FilledButton(onPressed: onRetry, child: Text(l10n.retry)),
          ],
        ),
      ),
    );
  }
}
