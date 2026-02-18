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
   - Invalidação de registros via *soft delete* (mantendo integridade).

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
- [x] Persistência configurada com identificadores únicos (UUID)
- [x] Estrutura de repositórios base (JPA) criada
- [x] Serviço de migração de dados via CSV implementado
- [x] Carga inicial automatizada via DatabaseSeeder funcional

**Entidades do Domínio:**
- [x] Role.java criada (ROLE_ADMIN, ROLE_USER, ROLE_AUDITOR)
- [x] User.java concluído
- [x] Patrimony.java concluído (Gestão de códigos e TAGs)
- [x] Instrument.java concluído (Relacionamentos JPA normalizados)
- [x] Category.java concluído
- [x] Location.java concluído
- [x] Movement.java concluído
- [x] Calibration.java concluído

**Lógica e Segurança (Pendentes):**
- [ ] Implementação de Bean Validation (Validação de entradas)
- [ ] Global Exception Handler (Tratamento de erros profissional)
- [ ] Spring Security + JWT (Autenticação e Autorização)
- [ ] Auditoria JPA (AuditLog automático de criação/edição)
- [ ] Soft Delete real (Exclusão lógica de registros)

📌 **Status atual:**
A fundação do banco de dados e o mapeamento das entidades principais com UUID
estão concluídos e validados. O sistema realiza a ingestão de dados via CSV e entrega
os objetos relacionados via API. O projeto agora entra na etapa crítica de implementação
das regras de negócio, validações de integridade e camada de segurança, essenciais para 
atingir os requisitos de auditoria propostos.

---

### Fase 3: Frontend & UX 🎨 (Planejado)
- [ ] Configuração do ambiente React + Vite
- [ ] Implementação do Dashboard de vencimentos
- [ ] Criação dos formulários auditáveis de calibração