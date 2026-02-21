# CalibraFlow

![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Backend-Java%2021-orange)
![Spring](https://img.shields.io/badge/Framework-Spring%20Boot%203.2-green)
![Security](https://img.shields.io/badge/Security-JWT%20%26%20Spring%20Security-blue)
![Database](https://img.shields.io/badge/Database-PostgreSQL-blue)
![Data](https://img.shields.io/badge/Data-Apache%20POI%20%26%20OpenCSV-red)

## 📌 Sobre o Projeto

Eu desenvolvi o **CalibraFlow** como um sistema corporativo auditável para gestão do ciclo de vida de
calibração de instrumentos.

O objetivo do sistema é garantir:

- **Controle automático de vencimentos**
- **Rastreabilidade completa** conforme padrões ISO
- **Histórico imutável** de calibrações
- **Auditoria total** de movimentações e responsáveis

O CalibraFlow substitui controles manuais descentralizados por uma aplicação segura, centralizada e preparada
para uso multiusuário em ambiente corporativo.

---

## 🚀 Tecnologias (Stack)

- **Backend:** Java 21 + Spring Boot 3.2 (Foco em SOLID)
- **Segurança:** Spring Security + JWT (Auth0)
- **Processamento de Dados:** Apache POI (Excel/XLSX) e OpenCSV
- **Banco de Dados:** PostgreSQL (Dockerizado)
- **Persistência:** Spring Data JPA + Hibernate
- **Ferramentas:** Flyway (Migrações), Lombok e Bean Validation

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
- [x] **Refatoração de Identificadores:** Migração de UUID para sequenciais (`Long`/`BigInt`) concluída.
- [x] **Estratégia de Identidade:** Implementação de `GenerationType.IDENTITY`.

**Domínio e Mapeamento (Concluído):**
- [x] Mapeamento relacional das entidades principais.
- [x] Repositórios JPA estabilizados e validados.
- [x] **Camada de Transferência (DTO):** Implementação de `InstrumentResponseDTO` para evitar erros de
- recursividade e proteger dados sensíveis.

**Ingestão de Dados e Lógica de Negócio (Estabilizada):**
- [x] **Integração Apache POI:** Motor preparado para leitura de planilhas `.xlsx`.
- [x] **OpenCSV:** Processamento funcional de arquivos `.csv` para carga de periodicidades.
- [x] Implementação de Bean Validation para garantir integridade de entrada via API.

**Segurança e Auditoria (Concluído):**
- [x] **Spring Security + JWT:** Autenticação robusta e geração de tokens de acesso funcionando.
- [x] **Security Filter:** Implementação de filtro de interceptação para validar tokens em cada requisição.
- [x] **Global Exception Handler:** Tratamento centralizado de erros (403 Forbidden, 404, 500).
- [x] **Soft Delete:** Implementado no `InstrumentController` para garantir rastreabilidade ISO.

📌 **Status atual:**
O motor de segurança está **totalmente operacional**, protegendo as rotas da API com JWT. O backend agora 
utiliza DTOs para comunicação, eliminando falhas de processamento de JSON. O repositório Git foi saneado e 
blindado contra o envio de planilhas de dados locais. O sistema está pronto para o refinamento final da carga 
de dados antes de iniciar o Frontend.

---

### Fase 3: Frontend & UX 🎨 (Planejado)
- [ ] Configuração do ambiente React + Vite.
- [ ] Desenvolvimento de Dashboard para visualização de vencimentos.
- [ ] Implementação de filtros avançados e relatórios auditáveis.