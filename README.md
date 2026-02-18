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

- **Backend:** Java 21 + Spring Boot (Foco em SOLID)
- **Frontend:** React + Vite
- **Banco de Dados:** PostgreSQL
- **Infraestrutura:** Docker

---

## 👥 Perfis de Acesso

1. **ADMIN**
   - Controle total do sistema e usuários.
   - Invalidação de registros via *soft delete*.

2. **USUÁRIO**
   - Registro de calibrações e movimentação de instrumentos.
   - Sem permissão para apagar histórico.

3. **AUDITOR**
   - Acesso somente leitura e exportação de relatórios.

---

## 📅 Histórico de Evolução

### Fase 1: Fundação ✅
- [x] Eu criei o repositório e a estrutura inicial do CalibraFlow
- [x] Eu configurei README.md e .gitignore corporativo
- [x] Eu defini o padrão de commits e evolução por fases

---

### Fase 2: Backend Core 🛠️ (Em andamento)

**Infraestrutura e Persistência:**
- [x] Spring Boot inicializado (pom.xml confirmado no projeto)
- [x] PostgreSQL configurado via Docker
- [x] UserRepository.java implementado
- [x] UserController.java implementado (Gestão de usuários e auditoria)
- [x] MovementRepository.java implementado (Persistência com UUID e filtros de busca)
- [x] MovementController.java implementado (Endpoints de listagem, criação e histórico)

**Entidades do Domínio:**
- [x] Role.java criada (ROLE_ADMIN, ROLE_USER, ROLE_AUDITOR)
- [x] User.java concluído
- [x] Instrument.java concluído
- [x] Category.java concluído
- [x] Location.java concluído
- [x] Movement.java concluído
- [ ] Calibration.java (Em andamento - faltam dados e saneamento completo)

📌 **Status atual:**
Mapeamento das entidades do domínio em andamento, com a maioria das classes principais 
finalizadas. Eu implementei os repositórios, controladores e a infraestrutura básica 
necessária para garantir a rastreabilidade das movimentações no PostgreSQL utilizando 
identificadores únicos (UUID). O sistema já é capaz de registrar e filtrar o histórico 
de movimentações por instrumento, vinculando cada ação a um usuário responsável para
fins de auditoria. A entidade Calibration.java segue em desenvolvimento para incluir 
todos os campos técnicos necessários...