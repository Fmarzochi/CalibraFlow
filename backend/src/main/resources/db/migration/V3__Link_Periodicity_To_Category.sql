-- Renomeia a coluna validity_months para validity_days (se existir)
ALTER TABLE tb_categories RENAME COLUMN validity_months TO validity_days;

-- Adiciona uma coluna category_id na tabela tb_periodicities
ALTER TABLE tb_periodicities ADD COLUMN category_id BIGINT;

-- Adiciona chave estrangeira
ALTER TABLE tb_periodicities ADD CONSTRAINT fk_periodicities_category 
    FOREIGN KEY (category_id) REFERENCES tb_categories(id);
