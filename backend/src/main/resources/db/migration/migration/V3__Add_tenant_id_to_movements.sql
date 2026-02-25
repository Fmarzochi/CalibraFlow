ALTER TABLE movements ADD COLUMN tenant_id BIGINT DEFAULT 1 NOT NULL;
ALTER TABLE movements ADD CONSTRAINT fk_movements_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);