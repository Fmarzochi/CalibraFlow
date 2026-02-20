CREATE TABLE tb_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_patrimonies (
    id BIGSERIAL PRIMARY KEY,
    patrimony_code VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_periodicities (
    id BIGSERIAL PRIMARY KEY,
    months INTEGER NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE tb_users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE instruments (
    id BIGSERIAL PRIMARY KEY,
    tag VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255),
    model VARCHAR(255),
    serial_number VARCHAR(255),
    range VARCHAR(255),
    tolerance VARCHAR(255),
    resolution VARCHAR(255),
    category_id BIGINT REFERENCES tb_categories(id),
    location_id BIGINT REFERENCES tb_locations(id),
    patrimony_id BIGINT REFERENCES tb_patrimonies(id),
    periodicity_id BIGINT REFERENCES tb_periodicities(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
);

CREATE TABLE tb_movements (
    id BIGSERIAL PRIMARY KEY,
    movement_date TIMESTAMP NOT NULL DEFAULT NOW(),
    reason TEXT,
    instrument_id BIGINT NOT NULL REFERENCES instruments(id),
    origin_id BIGINT REFERENCES tb_locations(id),
    destination_id BIGINT REFERENCES tb_locations(id),
    user_id BIGINT NOT NULL REFERENCES tb_users(id)
);

CREATE TABLE tb_calibrations (
    id BIGSERIAL PRIMARY KEY,
    instrument_id BIGINT NOT NULL REFERENCES instruments(id),
    calibration_date TIMESTAMP NOT NULL,
    next_calibration TIMESTAMP,
    certificate_number VARCHAR(255),
    approved BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);