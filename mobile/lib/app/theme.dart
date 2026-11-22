import 'package:allied_tokens/allied_tokens.dart';
import 'package:flutter/material.dart';

/// Material 3 theme built from the Allied DS tokens.
/// [brand] is the tenant accent from bootstrap.
ThemeData alliedTheme(
  Brightness brightness, {
  AlliedBrand brand = AlliedBrand.defaultBrand,
}) {
  final colors = brightness == Brightness.dark
      ? AlliedColors.dark
      : AlliedColors.light;
  final scheme = ColorScheme(
    brightness: brightness,
    primary: brand.accentDefault,
    onPrimary: brand.accentOn,
    primaryContainer: brand.accentSubtle,
    onPrimaryContainer: colors.textDefault,
    secondary: colors.statusNeutralSolid,
    onSecondary: colors.textInverse,
    error: colors.statusDangerSolid,
    onError: colors.textInverse,
    errorContainer: colors.statusDangerBg,
    onErrorContainer: colors.statusDangerFg,
    surface: colors.bgSurface,
    onSurface: colors.textDefault,
    onSurfaceVariant: colors.textMuted,
    surfaceContainerLow: colors.bgSubtle,
    outline: colors.borderInput,
    outlineVariant: colors.borderDefault,
  );
  final textTheme = const TextTheme(
    displayMedium: AlliedTextStyles.mobileHeroNumber,
    titleLarge: AlliedTextStyles.mobileTitle,
    bodyLarge: AlliedTextStyles.mobileBody,
    bodyMedium: AlliedTextStyles.mobileBody,
    bodySmall: AlliedTextStyles.mobileCaption,
    labelLarge: AlliedTextStyles.mobileLabel,
    labelSmall: AlliedTextStyles.mobileOverline,
  ).apply(bodyColor: colors.textDefault, displayColor: colors.textDefault);
  const controls = AlliedDensity.comfortable;

  return ThemeData(
    useMaterial3: true,
    colorScheme: scheme,
    scaffoldBackgroundColor: colors.bgCanvas,
    textTheme: textTheme,
    dividerColor: colors.borderDivider,
    extensions: [colors, brand],
    appBarTheme: AppBarTheme(
      backgroundColor: colors.bgSurface,
      foregroundColor: colors.textDefault,
      elevation: 0,
      scrolledUnderElevation: 0,
      titleTextStyle: AlliedTextStyles.mobileTitle.copyWith(
        color: colors.textDefault,
      ),
    ),
    cardTheme: CardThemeData(
      color: colors.bgSurface,
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AlliedRadius.lg),
        side: BorderSide(color: colors.borderDefault),
      ),
      margin: EdgeInsets.zero,
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: colors.bgSurface,
      contentPadding: EdgeInsets.symmetric(
        horizontal: controls.controlPaddingX,
        vertical: AlliedSpace.s3,
      ),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(AlliedRadius.md),
        borderSide: BorderSide(color: colors.borderInput),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(AlliedRadius.md),
        borderSide: BorderSide(color: colors.borderInput),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(AlliedRadius.md),
        borderSide: BorderSide(color: colors.borderFocus, width: 2),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(AlliedRadius.md),
        borderSide: BorderSide(color: colors.statusDangerSolid),
      ),
    ),
    filledButtonTheme: FilledButtonThemeData(
      style: FilledButton.styleFrom(
        minimumSize: Size.fromHeight(controls.controlHeightLg),
        textStyle: AlliedTextStyles.mobileLabel,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AlliedRadius.md),
        ),
      ),
    ),
    textButtonTheme: TextButtonThemeData(
      style: TextButton.styleFrom(
        minimumSize: Size(0, controls.controlHeightMd),
        textStyle: AlliedTextStyles.mobileLabel,
      ),
    ),
  );
}
