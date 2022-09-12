CREATE TABLE ifix_migration_progress (
    id character varying(64),
    tenantid character varying(128),
    resumefrom bigint,
    createdBy character varying(64),
    createdTime bigint,
    CONSTRAINT uk_ifix_migration_progress UNIQUE (id)
);

CREATE UNIQUE INDEX idx_ifix_migration_progress ON ifix_migration_progress (tenantid,createdtime);