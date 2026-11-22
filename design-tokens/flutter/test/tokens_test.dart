import 'package:allied_tokens/allied_tokens.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('light and dark colours lerp without losing fields', () {
    final mid = AlliedColors.light.lerp(AlliedColors.dark, 0.5);
    expect(
      mid.bgCanvas,
      Color.lerp(AlliedColors.light.bgCanvas, AlliedColors.dark.bgCanvas, 0.5),
    );
    expect(mid.textDefault, isNot(AlliedColors.light.textDefault));
  });

  test('text styles carry line height as a multiplier of the font size', () {
    expect(AlliedTextStyles.mobileBody.fontSize, 16);
    expect(AlliedTextStyles.mobileBody.height, closeTo(1.5, 0.001));
  });

  test('brand accents keep the on-accent colour readable', () {
    for (final brand in [
      AlliedBrand.defaultBrand,
      AlliedBrand.teal,
      AlliedBrand.amber,
      AlliedBrand.plum,
    ]) {
      expect(brand.accentOn, const Color(0xFFFFFFFF));
    }
  });
}
