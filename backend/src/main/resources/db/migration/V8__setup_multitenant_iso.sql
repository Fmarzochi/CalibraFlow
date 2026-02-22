CREATE TABLE tenants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    document VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO tenants (name, document)
VALUES ('Empresa Piloto', '00.000.000/0001-00');

CREATE TABLE instrument_status_history (
    id SERIAL PRIMARY KEY,
    instrument_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    previous_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responsible_id BIGINT NOT NULL,
    responsible_full_name VARCHAR(255) NOT NULL,
    responsible_cpf VARCHAR(14) NOT NULL,
    source_ip VARCHAR(45),
    justification TEXT NOT NULL
);

ALTER TABLE users ADD COLUMN tenant_id BIGINT;
ALTER TABLE users ADD COLUMN cpf VARCHAR(14) UNIQUE;

UPDATE users SET tenant_id = (SELECT id FROM tenants WHERE document = '00.000.000/0001-00') WHERE tenant_id IS NULL;

ALTER TABLE users ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE instruments ADD COLUMN tenant_id BIGINT;
ALTER TABLE instruments ADD COLUMN status VARCHAR(50);

UPDATE instruments SET tenant_id = (SELECT id FROM tenants WHERE document = '00.000.000/0001-00') WHERE tenant_id IS NULL;
UPDATE instruments SET status = 'ATIVO' WHERE status IS NULL;

ALTER TABLE instruments ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE instruments ALTER COLUMN status SET NOT NULL;

ALTER TABLE instruments ADD CONSTRAINT fk_instruments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE instrument_status_history ADD CONSTRAINT fk_history_instrument FOREIGN KEY (instrument_id) REFERENCES instruments (id);
ALTER TABLE instrument_status_history ADD CONSTRAINT fk_history_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id);