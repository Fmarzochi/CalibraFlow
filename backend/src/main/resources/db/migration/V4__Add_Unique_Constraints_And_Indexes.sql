ALTER TABLE instruments ADD CONSTRAINT uk_instruments_tag UNIQUE (tag);

CREATE INDEX idx_instruments_name ON instruments(name);
CREATE INDEX idx_instruments_location_id ON instruments(location_id);
CREATE INDEX idx_instruments_category_id ON instruments(category_id);
CREATE INDEX idx_instruments_periodicity_id ON instruments(periodicity_id);
CREATE INDEX idx_instruments_patrimony_code ON instruments(patrimony_code);

CREATE INDEX idx_categories_name ON tb_categories(name);
CREATE INDEX idx_locations_name ON tb_locations(name);
CREATE INDEX idx_locations_active ON tb_locations(active);

CREATE INDEX idx_movements_instrument_id ON tb_movements(instrument_id);
CREATE INDEX idx_movements_movement_date ON tb_movements(movement_date);
