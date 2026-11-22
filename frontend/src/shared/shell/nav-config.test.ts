import { describe, expect, it } from "vitest";

import { primaryNav, settingsNav, visibleNav } from "./nav-config";

describe("visibleNav", () => {
  it("hides groups of modules that are off", () => {
    const ids = visibleNav(primaryNav, {}, []).map((entry) => entry.id);
    expect(ids).not.toContain("clients-sites");
    expect(
      visibleNav(primaryNav, { "module:CUSTOMERS": true }, []).map((e) => e.id),
    ).toContain("clients-sites");
  });

  it("shows settings only to members the API granted the permission", () => {
    expect(visibleNav([settingsNav], {}, [])).toEqual([]);
    expect(visibleNav([settingsNav], {}, ["identity:users_read"])).toEqual([
      settingsNav,
    ]);
  });
});
