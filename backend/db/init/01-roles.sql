-- Runs once as the PostgreSQL superuser when the database is created (docker-compose init and
-- Testcontainers). Flyway runs as migrate_user and owns every object as `migrate`; the backend
-- connects as app_user and can never bypass row-level security.
CREATE ROLE app NOLOGIN NOBYPASSRLS;
CREATE ROLE migrate NOLOGIN BYPASSRLS;
CREATE ROLE readonly NOLOGIN NOBYPASSRLS;

CREATE ROLE app_user LOGIN PASSWORD 'app' NOBYPASSRLS IN ROLE app;
CREATE ROLE migrate_user LOGIN PASSWORD 'migrate' BYPASSRLS IN ROLE migrate;
CREATE ROLE readonly_user LOGIN PASSWORD 'readonly' NOBYPASSRLS IN ROLE readonly;

GRANT CREATE ON DATABASE alliedpayroll TO migrate;
GRANT app TO migrate;
GRANT readonly TO migrate;
