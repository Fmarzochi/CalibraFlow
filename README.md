# CalibraFlow

## 📌 Sobre o Projeto

Eu desenvolvi o **CalibraFlow** como um sistema corporativo auditável para gestão do ciclo de vida de calibração de instrumentos.

O objetivo do sistema é garantir:

- **Controle automático de vencimentos**
- **Rastreabilidade completa** conforme padrões ISO
- **Histórico imutável** de calibrações
- **Auditoria total** de movimentações e responsáveis

O CalibraFlow substitui controles manuais descentralizados por uma aplicação segura, centralizada e preparada para uso multiusuário em ambiente corporativo.

---

## 🚀 Tecnologias (Stack)

- **Backend:** Java 21 + Spring Boot 3.2 (Foco em SOLID)
- **Frontend:** React + Vite (Planejado)
- **Banco de Dados:** PostgreSQL (Dockerizado)
- **Persistência:** Spring Data JPA + Hibernate

---

## 👥 Perfis de Acesso

1. **ADMIN**
   - Controle total do sistema e usuários.
   - Invalidação de registros via *soft delete* (mantendo integridade).

2. **USUÁRIO**
   - Registro de calibrações e movimentação de instrumentos.
   - Sem permissão para apagar histórico.

3. **AUDITOR**
   - Acesso somente leitura e exportação de relatórios.

---

## 📅 Histórico de Evolução

### Fase 1: Fundação ✅
- [x] Eu criei o repositório e a estrutura inicial do CalibraFlow.
- [x] Eu configurei README.md e .gitignore corporativo.
- [x] Eu defini o padrão de commits e evolução por fases.

### Fase 2: Backend Core 🛠️ (Em andamento)

**Infraestrutura e Persistência (Estabilizada):**
- [x] Configuração do ambiente com Java 21 e Spring Boot 3.2.
- [x] Dockerização do banco de dados PostgreSQL.
- [x] **Refatoração de Identificadores:** Migração concluída de identificadores UUID para 
- identificadores sequenciais (`Long`/`BigInt`) para garantir compatibilidade e performance.
- [x] **Estratégia de Identidade:** Implementação de `GenerationType.IDENTITY` em todas as 
- entidades.

**Domínio e Mapeamento (Concluído):**
- [x] Mapeamento relacional das entidades: `Role`, `User`, `Patrimony`, `Instrument`, `Category`, `Location`, `Movement` e `Calibration`.
- [x] Repositórios JPA estabilizados e validados após refatoração de tipos.

**Ingestão de Dados e Lógica de Negócio (EM ANDAMENTO):**
- [/] **Importação de Periodicidade:** Implementado o motor de leitura para `periodicities.csv`,
- mas aguarda carga de dados completa e fiel à planilha original.
- [/] **Ingestão de Instrumentos:** O `DatabaseSeeder` está funcional para carga via CSV, porém 
- a **lógica de vínculo inteligente** (match entre descrição e categoria) ainda apresenta falhas
- de precisão e precisa de refinamento.
- [ ] Implementação de Bean Validation para garantir integridade de entrada via API.

**Segurança e Auditoria (Pendente):**
- [ ] Global Exception Handler (Tratamento de erros centralizado).
- [ ] Spring Security + JWT (Controle de acesso e autenticação).
- [ ] JPA Audit (Registro automático de autoria e data em cada registro).
- [ ] Soft Delete (Preservação de histórico para auditoria ISO).

📌 **Status atual:**
A fundação do banco de dados foi totalmente refatorada para utilizar IDs numéricos (`Long`), 
resolvendo conflitos de migração e estabilizando a compilação. O sistema já é capaz de realizar 
o `boot` completo e criar as tabelas automaticamente. No entanto, a **camada de ingestão 
de dados via Seeders ainda não é confiável**: instrumentos estão sendo vinculados a categorias 
genéricas ou incorretas. O projeto encontra-se na fase de ajuste fino da lógica de comparação
de dados antes de avançar para a implementação da segurança e APIs.

---

### Fase 3: Frontend & UX 🎨 (Planejado)
- [ ] Configuração do ambiente React + Vite.
- [ ] Desenvolvimento de Dashboard para visualização de vencimentos.
- [ ] Implementação de filtros avançados e relatórios auditáveis.