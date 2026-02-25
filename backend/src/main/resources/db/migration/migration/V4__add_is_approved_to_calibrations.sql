ALTER TABLE calibrations
ADD COLUMN IF NOT EXISTS is_approved BOOLEAN DEFAULT FALSE NOT NULL;

COMMENT ON COLUMN calibrations.is_approved IS 'Indica se a calibração foi aprovada pelo responsável técnico.';