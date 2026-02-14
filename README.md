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
- [x] Criação do repositório e estrutura inicial.
- [x] Configuração do Git e padrão corporativo.
- [x] Definição da stack tecnológica.

### Fase 2: Backend Core (Em andamento) 🛠️
- [x] Configuração do Spring Boot (pom.xml).
- [x] Conexão com PostgreSQL via Docker.
- [ ] Criação das Entidades de Banco de Dados.
