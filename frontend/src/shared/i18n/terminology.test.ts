import { describe, expect, it } from "vitest";

import { termValues } from "./terminology";

describe("termValues", () => {
  it("names each term as an ICU argument", () => {
    expect(
      termValues({
        site: "Location",
        "site.plural": "Locations",
        "site_sub_unit.plural": "Posts",
      }),
    ).toEqual({
      site: "Location",
      sitePlural: "Locations",
      siteSubUnitPlural: "Posts",
    });
  });
});
