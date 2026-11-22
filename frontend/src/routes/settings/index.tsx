import { createFileRoute, redirect } from "@tanstack/react-router";

// Settings opens on its first section until a landing page earns its place.
export const Route = createFileRoute("/settings/")({
  beforeLoad: () => {
    // eslint-disable-next-line @typescript-eslint/only-throw-error -- the router signals a redirect by throwing its own object
    throw redirect({ to: "/settings/users" });
  },
});
