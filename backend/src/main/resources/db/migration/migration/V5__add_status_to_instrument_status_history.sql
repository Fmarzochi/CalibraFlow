ALTER TABLE instrument_status_history
ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'INDEFINIDO' NOT NULL;

COMMENT ON COLUMN instrument_status_history.status IS 'Status do instrumento no momento do registro do histórico.';