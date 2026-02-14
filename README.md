# CalibraFlow

## 📌 Sobre o Projeto

Eu desenvolvi o **CalibrFlow** como um sistema corporativo auditável para gestão do ciclo de vida de calibração de instrumentos.

O objetivo do sistema é garantir:

- controle automático de vencimentos
- rastreabilidade completa conforme padrões ISO
- histórico imutável de calibrações
- auditoria total de movimentações e responsáveis

O CalibrFlow substitui controles manuais descentralizados por uma aplicação segura, centralizada e preparada para uso multiusuário em ambiente corporativo.

---

## 🚀 Tecnologias (Stack)

- **Backend:** Java 21 + Spring Boot (Clean Architecture + SOLID)
- **Frontend:** React + Vite
- **Banco de Dados:** PostgreSQL
- **Infraestrutura:** Docker + Vercel (ambiente de testes)

---

## 👥 Perfis de Acesso

1. **ADMIN**
   - Controle total do sistema
   - Gestão de usuários
   - Invalidação de registros via *soft delete*

2. **USUÁRIO**
   - Registro de calibrações
   - Movimentação de instrumentos entre unidades/obras
   - Sem permissão para apagar histórico

3. **AUDITOR**
   - Acesso somente leitura
   - Exportação de relatórios em PDF

---

## 📅 Histórico de Evolução

### Fase 1: Fundação (Em andamento)
- [x] Criação do repositório e estrutura inicial
- [ ] Configuração do ambiente Backend (Spring Boot)
- [ ] Configuração do banco PostgreSQL via Docker

### Próxima Fase: Backend Core
- Implementação da entidade Instrumento
- Service de cálculo automático de vencimento
- Controle de permissões por perfil
