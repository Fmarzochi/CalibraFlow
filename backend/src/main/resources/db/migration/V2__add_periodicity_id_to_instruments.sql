ALTER TABLE tb_instruments ADD COLUMN periodicity_id UUID;
ALTER TABLE tb_instruments ADD CONSTRAINT fk_instrument_periodicity FOREIGN KEY (periodicity_id) REFERENCES tb_periodicities(id);
