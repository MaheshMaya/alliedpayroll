import 'package:allied_payroll/core/mode/app_mode.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('no permissions means employee mode', () {
    expect(resolveMode(const []), AppMode.employee);
    expect(resolveMode(const ['payslip:read']), AppMode.employee);
  });

  test('a team permission opens manager mode', () {
    expect(
      resolveMode(const ['payslip:read', 'attendance:approve']),
      AppMode.manager,
    );
  });
}
