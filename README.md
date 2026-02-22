# CalibraFlow

![Status](https://img.shields.io/badge/Status-Arquitetura_SaaS_Pronta-green)
![Java](https://img.shields.io/badge/Backend-Java%2021-orange)
![Spring](https://img.shields.io/badge/Framework-Spring%20Boot%203.2-green)
![Security](https://img.shields.io/badge/Security-JWT_Multi--tenant-blue)
![Database](https://img.shields.io/badge/Database-PostgreSQL_SaaS-blue)
![Architecture](https://img.shields.io/badge/Architecture-Monolito_Conteinerizado-lightgrey)

## 📌 Sobre o Projeto

Eu desenvolvi o **CalibraFlow** como um sistema corporativo SaaS (Software as a Service) auditável
para gestão do ciclo de vida de calibração de instrumentos. O sistema foi projetado com uma arquitetura 
de isolamento total de dados, permitindo que múltiplas empresas utilizem a mesma infraestrutura de forma segura
e independente.

O objetivo do sistema é garantir:

- **Isolamento Multi-tenant:** Dados blindados por empresa (`tenant_id`).
- **Controle automático de vencimentos:** Robôs diários que gerenciam o status dos instrumentos.
- **Rastreabilidade ISO:** Histórico imutável com auditoria de IP, CPF e responsável.
- **Gestão de Provas Documentais:** Armazenamento seguro de certificados de calibração em PDF.

O CalibraFlow é uma solução de baixo custo operacional (Zero-Budget Ready) preparada para rodar 24/7 em 
infraestrutura de nuvem gratuita.

---

## 🚀 Tecnologias & Arquitetura

- **Backend:** Java 21 + Spring Boot 3.2.
- **Segurança Avançada:** JWT (Auth0) com Claims customizadas (Tenant, CPF, Nome).
- **Isolamento de Dados:** Hibernate Filters + AspectJ (AOP) para injeção automática de `WHERE tenant_id`.
- **Banco de Dados:** PostgreSQL com Migrações via Flyway.
- **Jobs Automáticos:** Spring Scheduling para verificação de conformidade diária.
- **Gestão de Arquivos:** Storage Service abstrato (Local/Cloud) para certificados.
- **Validações:** Bean Validation com algoritmo matemático de CPF customizado.

---

## 👥 Perfis de Acesso (RBAC)

1. **ADMINISTRADOR**
   - Controle total da empresa, usuários e configurações.
2. **USUÁRIO**
   - Registro de calibrações, upload de certificados e movimentação.
3. **AUDITOR**
   - Acesso exclusivo para consulta de histórico e download de evidências (PDF).

---

## 📅 Histórico de Evolução

### Fase 1: Fundação ✅
- [x] Eu criei o repositório e a estrutura inicial corporativa.
- [x] Eu defini o padrão de commits e organização por domínios.

### Fase 2: Backend Core & SaaS Multi-tenant ✅
- [x] **Arquitetura Multi-tenant:** Implementação de `TenantContext` e filtros automáticos do Hibernate.
- [x] **Segurança de Identidade:** Validação matemática de CPF e JWT enriquecido com dados de auditoria.
- [x] **Máquina de Estados ISO:** Implementação de status (`ATIVO`, `VENCIDO`, `QUARENTENA`) com histórico imutável.
- [x] **Robô de Conformidade:** Job automático (`DailyExpirationJob`) que bloqueia instrumentos vencidos às 00:01.
- [x] **Gestão de Documentos:** Motor de upload/download de certificados em PDF com isolamento físico por empresa.
- [x] **Tratamento Global:** Escudo de exceções (`GlobalExceptionHandler`) para respostas JSON padronizadas.
- [x] **CRUD de Instrumentos:** Gestão completa com validação de TAG única por tenant.

### Fase 3: Cloud & Deploy 🛠️ (Próximo Passo)
- [ ] Configuração de Docker Compose para ambiente produtivo.
- [ ] Script de deploy para Oracle Cloud (Always Free).
- [ ] Configuração de banco de dados gerenciado (Neon.tech/Supabase).

### Fase 4: Frontend & Dashboard 🎨 (Planejado)
- [ ] Desenvolvimento da interface em React + Vite.
- [ ] Dashboard de indicadores (Instrumentos próximos do vencimento).
- [ ] Visualizador de certificados integrado.

---

📌 **Status atual:**
O Backend está **Enterprise Ready**. Toda a inteligência de isolamento multi-empresa, segurança baseada em CPF 
e auditoria ISO está operacional. O sistema já é capaz de gerenciar instrumentos e certificados com total 
rastreabilidade, garantindo que o CalibraFlow seja uma ferramenta de nível profissional para auditorias reais.