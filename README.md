# ChamaTI

![Status](https://img.shields.io/badge/Status-Concluído-green)

Aplicativo Android para registro e atendimento de chamados de TI e Infraestrutura.

## 📱 Funcionalidades

- **Tela Inicial**: Acesso rápido ao cadastro e listagem de chamados
- **Cadastro de Chamados**: Registro de demandas com título, descrição, local e tipo (TI/Infraestrutura)
- **Lista de Chamados**: Visualização de todos os chamados cadastrados
- **Atendimento**: Registro de solução e atualização de status (Aberto, Em Andamento, Fechado)
- **Filtros**: Busca por data e status

## 🛠️ Tecnologias

- Java
- Android SDK
- SQLite (banco de dados local)

## 📦 Estrutura do Projeto

```
app/src/main/java/com/example/chamati/
├── MainActivity.java              # Tela inicial
├── CadastroChamadoActivity.java   # Cadastro de chamados
├── ListaChamadoActivity.java      # Lista de chamados
├── AtendimentoActivity.java       # Atendimento e solução
├── FiltrosActivity.java           # Filtros de busca
├── Model/
│   └── Chamado.java               # Modelo de dados
└── DataBase/
    └── DataBaseHelper.java        # Gerenciamento do SQLite
```

## 🚀 Como Executar

1. Clone o repositório
2. Abra o projeto no Android Studio
3. Sincronize o Gradle
4. Execute no emulador ou dispositivo físico

## 📝 Modelo de Dados

**Chamado**
- ID (auto-incremento)
- Título
- Descrição
- Local
- Tipo (0: TI, 1: Infraestrutura)
- Data de Cadastro
- Status (aberto, andamento, fechado)
- Solução

## 👨‍💻 Autor

Luís Felipe Costa Ferreira

Desenvolvido como trabalho prático de Programação de Dispositivos Móveis
