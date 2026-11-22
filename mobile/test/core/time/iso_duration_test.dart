import 'package:allied_payroll/core/time/iso_duration.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('parses what java.time.Duration writes', () {
    expect(parseIsoDuration('PT15M'), const Duration(minutes: 15));
    expect(parseIsoDuration('PT168H'), const Duration(days: 7));
    expect(
      parseIsoDuration('PT1H30M5.5S'),
      const Duration(hours: 1, minutes: 30, seconds: 5, milliseconds: 500),
    );
    expect(parseIsoDuration('-PT2M'), const Duration(minutes: -2));
  });

  test('rejects anything else', () {
    expect(() => parseIsoDuration('P1D'), throwsFormatException);
    expect(() => parseIsoDuration('15 minutes'), throwsFormatException);
  });
}
