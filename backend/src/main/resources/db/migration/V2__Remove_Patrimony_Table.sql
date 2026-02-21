-- Adiciona a coluna patrimony_code diretamente na tabela instruments
ALTER TABLE instruments ADD COLUMN patrimony_code VARCHAR(255);

-- Copia os dados da tabela tb_patrimonies para a nova coluna
UPDATE instruments i
SET patrimony_code = p.patrimony_code
FROM tb_patrimonies p
WHERE i.patrimony_id = p.id;

-- Remove a constraint de chave estrangeira
ALTER TABLE instruments DROP CONSTRAINT IF EXISTS fk_instruments_patrimony_id;

-- Remove a coluna patrimony_id
ALTER TABLE instruments DROP COLUMN patrimony_id;

-- Remove a tabela tb_patrimonies
DROP TABLE tb_patrimonies;
EO
cd src/main/resources/db/migration
cat > V2__Remove_Patrimony_Table.sql << 'EOF'
-- Adiciona a coluna patrimony_code diretamente na tabela instruments
ALTER TABLE instruments ADD COLUMN patrimony_code VARCHAR(255);

-- Copia os dados da tabela tb_patrimonies para a nova coluna
UPDATE instruments i
SET patrimony_code = p.patrimony_code
FROM tb_patrimonies p
WHERE i.patrimony_id = p.id;

-- Remove a constraint de chave estrangeira
ALTER TABLE instruments DROP CONSTRAINT IF EXISTS fk_instruments_patrimony_id;

-- Remove a coluna patrimony_id
ALTER TABLE instruments DROP COLUMN patrimony_id;

-- Remove a tabela tb_patrimonies
DROP TABLE tb_patrimonies;
