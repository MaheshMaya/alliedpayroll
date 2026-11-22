enum AppMode { employee, manager, kiosk }

/// Permissions that make the app open in manager mode. The codes follow the
/// permission catalogue the identity module will publish; until then bootstrap
/// carries none and everyone lands in employee mode.
const managerPermissions = {
  'workforce:team_read',
  'attendance:approve',
  'identity:users_read',
};

/// Kiosk mode is entered only through device provisioning, never from a
/// person's permissions.
AppMode resolveMode(Iterable<String> permissions) =>
    permissions.any(managerPermissions.contains)
    ? AppMode.manager
    : AppMode.employee;
