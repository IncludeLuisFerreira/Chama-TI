# ChamaTI 🎫

![Status](https://img.shields.io/badge/Status-Concluído-green)
![Android](https://img.shields.io/badge/Android-SDK%2034-3DDC84?logo=android)
![Java](https://img.shields.io/badge/Java-17-007396?logo=java)
![SQLite](https://img.shields.io/badge/SQLite-Local-003B57?logo=sqlite)

Sistema Android para gerenciamento de chamados de TI e Infraestrutura com interface moderna seguindo Material Design 3.

---

## 📱 Funcionalidades

### Dashboard Principal
- Visualização de estatísticas em tempo real
- Contadores por status (Abertos, Em Atendimento, Concluídos)
- Acesso rápido para cadastro e listagem

### Cadastro de Chamados
- Formulário completo com validação
- Seleção de tipo: TI ou Infraestrutura
- Campos: Título, Local, Descrição
- Data automática de cadastro

### Lista de Chamados
- RecyclerView com todos os chamados
- Badges visuais para tipo e status
- Contador dinâmico de registros
- Clique para abrir detalhes

### Sistema de Filtros (BottomSheet)
- Filtro por Área (TI/Infraestrutura)
- Filtro por Status (Aberto/Em Atendimento/Concluído)
- Filtro por Período (seletor de data range)
- Botão para limpar filtros
- Interface moderna com Material Design

### Atendimento de Chamados
- Visualização completa dos dados
- Alteração de status via dropdown
- Campo para registrar solução
- Validação: solução obrigatória ao concluir
- Atualização em tempo real

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| **Java** | 17 | Linguagem principal |
| **Android SDK** | 34 (API 34) | Framework Android |
| **SQLite** | 3.x | Banco de dados local |
| **Material Design 3** | Latest | Componentes de UI |
| **RecyclerView** | - | Lista de chamados |
| **BottomSheet** | - | Modal de filtros |

---

## 📦 Estrutura do Projeto

```
app/src/main/
├── java/com/example/chamati/
│   ├── MainActivity.java                  # Dashboard com estatísticas
│   ├── CadastroChamadoActivity.java       # Formulário de cadastro
│   ├── ListaChamadoActivity.java          # Lista com filtros
│   ├── AtendimentoActivity.java           # Detalhes e atendimento
│   ├── FiltrosBottomSheet.java            # Modal de filtros (Material Design)
│   ├── ChamadoAdapter.java                # Adapter do RecyclerView
│   ├── Model/
│   │   └── Chamado.java                   # Entidade de dados
│   └── DataBase/
│       └── DataBaseHelper.java            # SQLite Helper + CRUD
│
└── res/
    ├── layout/                            # Layouts XML
    ├── drawable/                          # Ícones e badges
    └── values/                            # Cores, strings, temas

```

---

## 🗄️ Modelo de Dados

### Tabela: `chamados`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | INTEGER | Chave primária (auto-incremento) |
| `titulo` | TEXT | Título do chamado |
| `descricao` | TEXT | Descrição detalhada |
| `local` | TEXT | Local do problema |
| `tipo` | INTEGER | 0 = TI, 1 = Infraestrutura |
| `data_cadastro` | TEXT | Data no formato dd/MM/yyyy |
| `status` | TEXT | "aberto", "andamento", "fechado" |
| `solucao` | TEXT | Descrição da solução aplicada |

---

## 🚀 Como Executar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 17+
- Dispositivo Android 7.0+ (API 24) ou emulador

### Passos

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd chamaTI
```

2. **Abra no Android Studio**
```
File > Open > Selecione a pasta do projeto
```

3. **Sincronize o Gradle**
```
Aguarde a sincronização automática ou clique em "Sync Now"
```

4. **Execute o aplicativo**
```
Run > Run 'app' ou pressione Shift + F10
```

---

## 🎨 Design Pattern & Arquitetura

- **Padrão:** MVC (Model-View-Controller)
- **Model:** Classe `Chamado.java`
- **View:** Activities + Layouts XML
- **Controller:** Activities gerenciam lógica de negócio
- **Data Access:** `DataBaseHelper` (padrão DAO)

---

## 🔧 Funcionalidades Técnicas

### Validações Implementadas
✅ Campos obrigatórios no cadastro  
✅ Seleção obrigatória de tipo (TI/Infraestrutura)  
✅ Solução obrigatória ao concluir chamado  
✅ Proteção contra valores null no banco  

### Filtros Avançados
✅ Filtro por tipo de área  
✅ Filtro por status  
✅ Filtro por período (range de datas)  
✅ Comparação de datas em formato brasileiro (dd/MM/yyyy)  
✅ Botão para limpar todos os filtros  

### Interface Moderna
✅ Material Design 3  
✅ BottomSheet para filtros (padrão profissional)  
✅ Badges coloridos para status  
✅ FloatingActionButton para ações rápidas  
✅ RecyclerView otimizado  

---

## 📸 Fluxo de Navegação

```
MainActivity (Dashboard)
    ├─> CadastroChamadoActivity (Novo Chamado)
    │       └─> [Salva] → Volta para MainActivity
    │
    └─> ListaChamadoActivity (Ver Chamados)
            ├─> FiltrosBottomSheet (Modal de Filtros)
            │       └─> [Aplica/Limpa] → Atualiza lista
            │
            └─> AtendimentoActivity (Clique em item)
                    └─> [Salva] → Volta para Lista
```

---

## 🐛 Bugs Corrigidos

Veja o arquivo [VISTORIA.md](VISTORIA.md) para detalhes sobre bugs identificados e corrigidos.

---

## 📄 Documentação Adicional

- **[SOLUCAO.md](SOLUCAO.md)** - Explicação técnica detalhada da solução, fluxo de dados e chamadas

---

## 👨‍💻 Autor

**Luís Felipe Costa Ferreira**

Desenvolvido como trabalho prático da disciplina de **Programação de Dispositivos Móveis**

---

## 📝 Licença

Este projeto foi desenvolvido para fins acadêmicos.

---

**Última atualização:** Maio/2026
