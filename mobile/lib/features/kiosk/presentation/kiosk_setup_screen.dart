import 'package:allied_payroll/l10n/generated/app_localizations.dart';
import 'package:allied_tokens/allied_tokens.dart';
import 'package:flutter/material.dart';

/// Entry point for provisioning a tablet as a kiosk. The enrolment QR flow
/// arrives with the devices slice; until then the screen states what an
/// administrator has to do.
class KioskSetupScreen extends StatelessWidget {
  const KioskSetupScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(title: Text(l10n.kioskSetupTitle)),
      body: Padding(
        padding: const EdgeInsets.all(AlliedSpace.s6),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(l10n.kioskSetupBody, style: theme.textTheme.bodyLarge),
            const SizedBox(height: AlliedSpace.s6),
            FilledButton.icon(
              onPressed: null,
              icon: const Icon(Icons.qr_code_scanner),
              label: Text(l10n.kioskScanQr),
            ),
          ],
        ),
      ),
    );
  }
}
