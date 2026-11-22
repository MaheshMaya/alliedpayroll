import 'package:allied_payroll/core/auth/workspace_code.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('normalises case and whitespace', () {
    final code = WorkspaceCode.tryParse('  Sentinel ');
    expect(code?.value, 'sentinel');
    expect(code?.organizationScope, 'organization:sentinel');
  });

  test('rejects what the backend host resolver would reject', () {
    for (final input in [
      '',
      '-sentinel',
      'sentinel-',
      'sen tinel',
      'x' * 41,
    ]) {
      expect(WorkspaceCode.tryParse(input), isNull, reason: input);
    }
    expect(WorkspaceCode.tryParse('nimbus-it'), isNotNull);
    expect(WorkspaceCode.tryParse('a'), isNotNull);
  });
}
