ALTER TABLE instruments ADD COLUMN patrimony_code VARCHAR(255);

UPDATE instruments i
SET patrimony_code = p.patrimony_code
FROM tb_patrimonies p
WHERE i.patrimony_id = p.id;

ALTER TABLE instruments DROP CONSTRAINT IF EXISTS instruments_patrimony_id_fkey;
ALTER TABLE instruments DROP COLUMN patrimony_id;
DROP TABLE tb_patrimonies;