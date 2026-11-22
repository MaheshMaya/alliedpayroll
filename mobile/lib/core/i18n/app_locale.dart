import 'dart:ui';

import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'app_locale.g.dart';

/// The locale the app renders and sends as Accept-Language. It follows the
/// device until the language switcher arrives.
@Riverpod(keepAlive: true)
class AppLocale extends _$AppLocale {
  @override
  Locale build() => PlatformDispatcher.instance.locale;
}
