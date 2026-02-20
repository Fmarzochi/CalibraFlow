CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    enabled BOOLEAN NOT NULL,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE tb_categories (
    id UUID PRIMARY KEY,
    calibration_interval_days INTEGER NOT NULL,
    description VARCHAR(255),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tb_locations (
    id UUID PRIMARY KEY,
    active BOOLEAN,
    description VARCHAR(255),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tb_patrimonies (
    id UUID PRIMARY KEY,
    patrimony_code VARCHAR(255) NOT NULL UNIQUE,
    tag VARCHAR(255) NOT NULL
);

CREATE TABLE tb_instruments (
    id UUID PRIMARY KEY,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6),
    deleted BOOLEAN NOT NULL,
    deleted_at TIMESTAMP(6),
    name VARCHAR(255) NOT NULL,
    serial_number VARCHAR(255),
    category_id UUID REFERENCES tb_categories(id),
    location_id UUID REFERENCES tb_locations(id),
    patrimony_id UUID REFERENCES tb_patrimonies(id)
);

CREATE TABLE tb_calibrations (
    id UUID PRIMARY KEY,
    calibration_date DATE NOT NULL,
    certificate_number VARCHAR(255) NOT NULL,
    laboratory VARCHAR(255) NOT NULL,
    next_calibration_date DATE NOT NULL,
    instrument_id UUID NOT NULL REFERENCES tb_instruments(id)
);

CREATE TABLE tb_movements (
    id UUID PRIMARY KEY,
    movement_date TIMESTAMP(6) NOT NULL,
    reason TEXT,
    destination_id UUID NOT NULL REFERENCES tb_locations(id),
    instrument_id UUID NOT NULL REFERENCES tb_instruments(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    origin_id UUID REFERENCES tb_locations(id)
);

CREATE TABLE tb_periodicities (
    id UUID PRIMARY KEY,
    days INTEGER NOT NULL,
    instrument_name VARCHAR(255) NOT NULL UNIQUE
);