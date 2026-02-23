# CalibraFlow

![Status](https://img.shields.io/badge/Status-SaaS_Em_Desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Backend-Java%2021-orange)
![Spring](https://img.shields.io/badge/Framework-Spring%20Boot%203.2-green)
![Security](https://img.shields.io/badge/Security-JWT_Multi--tenant-blue)
![Database](https://img.shields.io/badge/Database-PostgreSQL_SaaS-blue)
![Architecture](https://img.shields.io/badge/Architecture-Monorepo_Conteinerizado-lightgrey)

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

- **Backend:** Java 21 + Spring Boot 3.2 (Em fase de compilação).
- **Frontend:** React + Vite + Tailwind CSS (Arquitetura de pastas inicializada).
- **Segurança Avançada:** JWT (Auth0) com Claims customizadas (Tenant, CPF, Nome).
- **Isolamento de Dados:** Hibernate Filters + AspectJ (AOP) para injeção automática de `WHERE tenant_id`.
- **Banco de Dados:** PostgreSQL com Migrações via Flyway.

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

### Fase 2: Backend Core & SaaS Multi-tenant 🛠️ (Em Desenvolvimento)
- [/] **Arquitetura Multi-tenant:** Implementação de `TenantContext` (Em refatoração).
- [/] **Segurança de Identidade:** Validação de CPF e JWT (Lógica escrita, pendente de teste funcional).
- [/] **Máquina de Estados ISO:** Implementação de status (`ATIVO`, `VENCIDO`) (Pendente de validação de tipos).
- [x] **Robô de Conformidade:** Job automático (`DailyExpirationJob`) (Draft inicial concluído).
- [/] **CRUD de Instrumentos:** Desenvolvimento do Service e Repository (Ajustando erros de tipagem).

### Fase 3: Cloud & Deploy 🛠️ (Planejado)
- [/] Configuração de Docker Compose para ambiente produtivo.
- [/] Script de deploy para Oracle Cloud (Always Free).

### Fase 4: Frontend & Dashboard 🎨 (Iniciado)
- [x] **Arquitetura Monorepo:** Criação e estruturação das pastas do frontend na raiz do projeto.
- [ ] Desenvolvimento da interface em React + Vite.
- [ ] Integração via Axios com o Backend.

---

📌 **Status atual:**
O projeto encontra-se em **Fase de Refatoração e Ajuste de Compilação**. A arquitetura Monorepo foi estabelecida, 
com as pastas de Frontend e Backend devidamente isoladas na raiz. O foco atual é a correção de erros de tipagem e
dependências (Lombok/Spring) para estabilizar o ambiente de execução e iniciar a comunicação entre as camadas.

---

## 🚀 Próximos Passos

1. **Correção do Backend:** Ajustar campos faltantes na entidade `Tenant` e erros de tipo no `InstrumentService`.
2. **Health Check:** Rodar o Spring Boot com sucesso e validar a criação das tabelas no PostgreSQL via Docker.
3. **Frontend Boilerplate:** Configurar `package.json` e Tailwind para iniciar a interface industrial.