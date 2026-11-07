package com.alliedpayroll.platform.tenancy;

import com.alliedpayroll.platform.core.ids.TenantId;

/** The tenant bound to the current scope: its id for data access, its slug for identity checks. */
public record CurrentTenant(TenantId id, String slug) {}
