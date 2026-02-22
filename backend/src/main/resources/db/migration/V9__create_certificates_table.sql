CREATE TABLE certificates (
    id SERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    calibration_id BIGINT NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(500) NOT NULL UNIQUE,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by_id BIGINT NOT NULL,
    uploaded_by_name VARCHAR(255) NOT NULL,
    CONSTRAINT fk_certificates_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT fk_certificates_calibration FOREIGN KEY (calibration_id) REFERENCES calibrations (id)
);