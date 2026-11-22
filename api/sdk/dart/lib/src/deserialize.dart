import 'package:allied_api/src/model/audit_event_view.dart';
import 'package:allied_api/src/model/bootstrap.dart';
import 'package:allied_api/src/model/mobile_bootstrap.dart';
import 'package:allied_api/src/model/page_response_audit_event_view.dart';
import 'package:allied_api/src/model/page_response_user_summary.dart';
import 'package:allied_api/src/model/permission_definition.dart';
import 'package:allied_api/src/model/role_ref.dart';
import 'package:allied_api/src/model/role_view.dart';
import 'package:allied_api/src/model/session_view.dart';
import 'package:allied_api/src/model/sync_policy.dart';
import 'package:allied_api/src/model/tenant_summary.dart';
import 'package:allied_api/src/model/user.dart';
import 'package:allied_api/src/model/user_detail.dart';
import 'package:allied_api/src/model/user_summary.dart';

final _regList = RegExp(r'^List<(.*)>$');
final _regSet = RegExp(r'^Set<(.*)>$');
final _regMap = RegExp(r'^Map<String,(.*)>$');

ReturnType deserialize<ReturnType, BaseType>(
  dynamic value,
  String targetType, {
  bool growable = true,
}) {
  switch (targetType) {
    case 'String':
      return '$value' as ReturnType;
    case 'int':
      return (value is int ? value : int.parse('$value')) as ReturnType;
    case 'bool':
      if (value is bool) {
        return value as ReturnType;
      }
      final valueString = '$value'.toLowerCase();
      return (valueString == 'true' || valueString == '1') as ReturnType;
    case 'double':
      return (value is double ? value : double.parse('$value')) as ReturnType;
    case 'AuditEventView':
      return AuditEventView.fromJson(value as Map<String, dynamic>)
          as ReturnType;
    case 'Bootstrap':
      return Bootstrap.fromJson(value as Map<String, dynamic>) as ReturnType;
    case 'MobileBootstrap':
      return MobileBootstrap.fromJson(value as Map<String, dynamic>)
          as ReturnType;
    case 'PageResponseAuditEventView':
      return PageResponseAuditEventView.fromJson(value as Map<String, dynamic>)
          as ReturnType;
    case 'PageResponseUserSummary':
      return PageResponseUserSummary.fromJson(value as Map<String, dynamic>)
          as ReturnType;
    case 'PermissionDefinition':
      return PermissionDefinition.fromJson(value as Map<String, dynamic>)
          as ReturnType;
    case 'RoleRef':
      return RoleRef.fromJson(value as Map<String, dynamic>) as ReturnType;
    case 'RoleView':
      return RoleView.fromJson(value as Map<String, dynamic>) as ReturnType;
    case 'SessionView':
      return SessionView.fromJson(value as Map<String, dynamic>) as ReturnType;
    case 'SyncPolicy':
      return SyncPolicy.fromJson(value as Map<String, dynamic>) as ReturnType;
    case 'TenantSummary':
      return TenantSummary.fromJson(value as Map<String, dynamic>)
          as ReturnType;
    case 'User':
      return User.fromJson(value as Map<String, dynamic>) as ReturnType;
    case 'UserDetail':
      return UserDetail.fromJson(value as Map<String, dynamic>) as ReturnType;
    case 'UserSummary':
      return UserSummary.fromJson(value as Map<String, dynamic>) as ReturnType;
    default:
      RegExpMatch? match;

      if (value is List && (match = _regList.firstMatch(targetType)) != null) {
        targetType = match![1]!; // ignore: parameter_assignments
        return value
                .map<BaseType>(
                  (dynamic v) => deserialize<BaseType, BaseType>(
                    v,
                    targetType,
                    growable: growable,
                  ),
                )
                .toList(growable: growable)
            as ReturnType;
      }
      if (value is Set && (match = _regSet.firstMatch(targetType)) != null) {
        targetType = match![1]!; // ignore: parameter_assignments
        return value
                .map<BaseType>(
                  (dynamic v) => deserialize<BaseType, BaseType>(
                    v,
                    targetType,
                    growable: growable,
                  ),
                )
                .toSet()
            as ReturnType;
      }
      if (value is Map && (match = _regMap.firstMatch(targetType)) != null) {
        targetType = match![1]!.trim(); // ignore: parameter_assignments
        return Map<String, BaseType>.fromIterables(
              value.keys as Iterable<String>,
              value.values.map(
                (dynamic v) => deserialize<BaseType, BaseType>(
                  v,
                  targetType,
                  growable: growable,
                ),
              ),
            )
            as ReturnType;
      }
      break;
  }
  throw Exception('Cannot deserialize');
}
