# Task Manager API

Uma API REST para gerenciamento de tarefas construída com **Java 21** e **Spring Boot 4**, aplicando os princípios da **Arquitetura Hexagonal (Ports & Adapters)** para garantir um código limpo, desacoplado e fácil de evoluir.

---

## 🎯 Intenção do Projeto

Este projeto foi desenvolvido com o objetivo de praticar e demonstrar na prática a **Arquitetura Hexagonal**, um dos padrões arquiteturais mais relevantes para sistemas que precisam de manutenibilidade, testabilidade e independência de frameworks.

A ideia central é simples: **o domínio da aplicação não sabe que existe um banco de dados, um framework web ou qualquer tecnologia externa**. Toda comunicação com o mundo externo passa por contratos bem definidos chamados de *portas (ports)*, e as implementações concretas desses contratos são os *adaptadores (adapters)*.

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal (Records, etc.) |
| Spring Boot | 4.0.5 | Framework base da aplicação |
| Spring Web MVC | — | Camada REST / HTTP |
| Spring Data JPA | — | Abstração de persistência |
| Hibernate | — | ORM (mapeamento objeto-relacional) |
| H2 Database | — | Banco de dados em memória (dev/teste) |
| Maven | — | Gerenciador de dependências e build |

---

## 📐 Arquitetura

O projeto segue a **Arquitetura Hexagonal**, também conhecida como *Ports & Adapters*, proposta por Alistair Cockburn. A grande vantagem é que o núcleo do negócio fica completamente isolado de detalhes de infraestrutura.

```
┌────────────────────────────────────────────────────────────────┐
│                        adapter/in/web                          │
│         (HTTP) ──► TaskController ──► TaskWebMapper            │
│         (Erros) ◄── GlobalExceptionHandler ◄── ErrorResponse  │
└───────────────────────────┬────────────────────────────────────┘
                            │  UseCase Interfaces (porta de entrada)
┌───────────────────────────▼────────────────────────────────────┐
│                        application                             │
│      CreateTaskService  /  FindTaskService                     │
│      CompleteTaskService                                       │
│      Ports In: CreateTaskUseCase | FindTaskUseCase             │
│               CompleteTaskUseCase                              │
│      Port Out: TaskRepositoryPort                              │
└───────────────────────────┬────────────────────────────────────┘
                            │  RepositoryPort Interface (porta de saída)
┌───────────────────────────▼────────────────────────────────────┐
│                          domain                                │
│         Task  |  TaskStatus  |  TaskAlreadyCompletedException  │
└────────────────────────────────────────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────────────┐
│                    adapter/out/persistence                     │
│       TaskPersistenceAdapter ──► TaskJpaRepository             │
│       TaskPersistenceMapper  ──► TaskJpaEntity (@Entity)       │
└────────────────────────────────────────────────────────────────┘
```

### Regras de dependência

- `domain` → não depende de nada
- `application` → depende apenas de `domain`
- `adapter/in` → depende de `application` (via interfaces)
- `adapter/out` → depende de `application` (implementa interfaces) e `domain`
- `configuration` → conecta tudo via injeção de dependência manual

---

## 📁 Estrutura de Pacotes

```
src/main/java/com/example/task_manager/
│
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── TaskController.java              ← Endpoints REST
│   │       ├── handler/
│   │       │   ├── GlobalExceptionHandler.java  ← Tratamento centralizado de exceções (@RestControllerAdvice)
│   │       │   ├── ErrorResponse.java           ← Corpo padronizado de erro (record)
│   │       │   └── ErrorResponseFactory.java    ← Fábrica do ErrorResponse
│   │       ├── mapper/TaskWebMapper.java         ← Converte request/response ↔ command/output
│   │       ├── request/CreateTaskRequest.java    ← Body de entrada com validação (@NotBlank)
│   │       └── response/TaskResponse.java        ← Body de saída (record)
│   └── out/
│       └── persistence/
│           ├── TaskPersistenceAdapter.java       ← Implementa TaskRepositoryPort
│           ├── entity/TaskJpaEntity.java         ← Entidade JPA (@Entity)
│           ├── mapper/TaskPersistenceMapper.java  ← Converte Task ↔ TaskJpaEntity
│           └── repository/TaskJpaRepository.java  ← Spring Data JPA
│
├── application/
│   ├── dto/
│   │   ├── CreateTaskCommand.java               ← Input do use case (record)
│   │   └── TaskOutput.java                      ← Output do use case (record)
│   ├── mapper/TaskApplicationMapper.java         ← Converte Task ↔ TaskOutput
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateTaskUseCase.java            ← Contrato: criar tarefa
│   │   │   ├── FindTaskUseCase.java              ← Contrato: buscar tarefa(s)
│   │   │   └── CompleteTaskUseCase.java          ← Contrato: completar tarefa
│   │   └── out/
│   │       └── TaskRepositoryPort.java           ← Contrato: persistência
│   └── usecase/
│       ├── CreateTaskService.java                ← Lógica de criação
│       ├── FindTaskService.java                  ← Lógica de busca
│       └── CompleteTaskService.java              ← Lógica de conclusão
│
├── configuration/
│   └── TaskUseCaseConfig.java                   ← Wiring manual dos beans
│
└── domain/
    ├── enums/
    │   └── TaskStatus.java                      ← PENDING | COMPLETED
    ├── exception/
    │   └── TaskAlreadyCompletedException.java    ← Exceção de domínio
    └── model/
        └── Task.java                            ← Entidade de domínio pura
```

---

## 🔌 Endpoints da API

Base URL: `http://localhost:8080`

### `POST /tasks` — Criar uma tarefa

**Request Body:**
```json
{
  "title": "Estudar Arquitetura Hexagonal",
  "description": "Ler o livro Clean Architecture e implementar um projeto exemplo"
}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "title": "Estudar Arquitetura Hexagonal",
  "description": "Ler o livro Clean Architecture e implementar um projeto exemplo",
  "status": "PENDING"
}
```

**Response `400 Bad Request`** (campos em branco ou ausentes):
```json
{
  "timestamp": "2026-04-07T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "title: must not be blank",
  "path": "/tasks"
}
```

---

### `GET /tasks` — Listar tarefas (paginado)

**Query params:** `page`, `size`, `sort` (Spring Data Pageable)

Exemplo: `GET /tasks?page=0&size=10&sort=id,asc`

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Estudar Arquitetura Hexagonal",
      "description": "...",
      "status": "PENDING"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

---

### `GET /tasks/{id}` — Buscar tarefa por ID

Exemplo: `GET /tasks/1`

**Response `200 OK`:**
```json
{
  "id": 1,
  "title": "Estudar Arquitetura Hexagonal",
  "description": "...",
  "status": "PENDING"
}
```

**Response `404 Not Found`** (ID inexistente):
```json
{
  "timestamp": "2026-04-07T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found.",
  "path": "/tasks/99"
}
```

---

### `PATCH /tasks/{id}/complete` — Completar uma tarefa

Exemplo: `PATCH /tasks/1/complete`

**Response `200 OK`:**
```json
{
  "id": 1,
  "title": "Estudar Arquitetura Hexagonal",
  "description": "...",
  "status": "COMPLETED"
}
```

**Response `409 Conflict`** (tarefa já concluída):
```json
{
  "timestamp": "2026-04-07T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Task is already completed.",
  "path": "/tasks/1/complete"
}
```

---

## 🚨 Tratamento de Erros

Todas as exceções são interceptadas pelo `GlobalExceptionHandler` (`@RestControllerAdvice`) e retornam um corpo padronizado:

```json
{
  "timestamp": "2026-04-07T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found.",
  "path": "/tasks/99"
}
```

| Exceção | Status HTTP | Cenário |
|---|---|---|
| `TaskNotFoundException` | `404 Not Found` | ID inexistente no banco |
| `TaskAlreadyCompletedException` | `409 Conflict` | Tarefa já está `COMPLETED` |
| `MethodArgumentNotValidException` | `400 Bad Request` | Falha no `@Valid` do request body |
| `ConstraintViolationException` | `400 Bad Request` | Violação de constraint de validação |
| `HttpMessageNotReadableException` | `400 Bad Request` | JSON malformado ou ilegível |
| `Exception` | `500 Internal Server Error` | Qualquer erro inesperado |

## 🔄 Fluxo de uma Requisição

### Exemplo: `POST /tasks`

```
1.  HTTP Request chega em TaskController.createTask()
2.  TaskWebMapper converte CreateTaskRequest → CreateTaskCommand
3.  CreateTaskUseCase.createTask(command) é chamado
4.  CreateTaskService cria um Task de domínio (status = PENDING)
5.  TaskRepositoryPort.save(task) é chamado (apenas a interface)
6.  TaskPersistenceAdapter recebe o Task de domínio
7.  TaskPersistenceMapper converte Task → TaskJpaEntity
8.  TaskJpaRepository.save(entity) persiste no banco H2
9.  TaskPersistenceMapper converte TaskJpaEntity → Task (com ID gerado)
10. TaskApplicationMapper converte Task → TaskOutput
11. TaskWebMapper converte TaskOutput → TaskResponse
12. HTTP Response 200 retornado com os dados da tarefa criada
```

### Exemplo: `PATCH /tasks/{id}/complete`

```
1.  HTTP Request chega em TaskController.completeTask()
2.  CompleteTaskUseCase.completeTask(id) é chamado
3.  CompleteTaskService busca a Task via TaskRepositoryPort.findById()
4.  task.complete() é executado — regra de negócio no domínio
5.  Se já estiver COMPLETED → TaskAlreadyCompletedException é lançada
6.  TaskRepositoryPort.save(task) persiste o novo status
7.  TaskOutput é retornado com status = COMPLETED
```

---

## 🧪 Testes

O projeto possui cobertura de testes em todas as camadas da arquitetura.

### Estrutura de Testes

```
src/test/java/com/example/task_manager/
│
├── domain/
│   └── model/
│       └── TaskTest.java                        ← Testes unitários do domínio (sem Spring)
│
├── application/
│   └── usecase/
│       ├── CreateTaskServiceTest.java           ← Teste unitário com mock do repositório
│       ├── FindTaskServiceTest.java             ← Teste unitário com mock do repositório
│       └── CompleteTaskServiceTest.java         ← Teste unitário com mock do repositório
│
├── adapter/
│   ├── in/
│   │   └── web/
│   │       └── TaskControllerTest.java          ← Teste da camada web com MockMvc (@WebMvcTest)
│   └── out/
│       └── persistence/
│           └── TaskPersistenceAdapterTest.java  ← Teste de integração com banco H2 (@DataJpaTest)
│
└── integration/
    └── TaskFlowIntegrationTest.java             ← Teste end-to-end com @SpringBootTest
```

### Tipos de Teste

| Arquivo | Tipo | Estratégia |
|---|---|---|
| `TaskTest` | Unitário | Sem nenhuma dependência externa — testa regras de domínio puras |
| `CreateTaskServiceTest` | Unitário | Mockito — isola o service do repositório e do mapper |
| `FindTaskServiceTest` | Unitário | Mockito — cobre busca paginada, por ID e not found |
| `CompleteTaskServiceTest` | Unitário | Mockito — cobre sucesso, not found e já concluída |
| `TaskControllerTest` | Camada Web | `@WebMvcTest` + MockMvc — testa rotas, status HTTP e JSON de resposta/erro |
| `TaskPersistenceAdapterTest` | Integração JPA | `@DataJpaTest` — testa save, findById e findTasks contra H2 real |
| `TaskFlowIntegrationTest` | End-to-End | `@SpringBootTest` — sobe contexto completo e valida fluxos reais |

### Rodando os Testes

```bash
./mvnw test
```

---

## ⚙️ Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.8+

### Rodando a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

### Build

```bash
./mvnw clean package
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```

### Console H2 (banco em memória)

Acesse `http://localhost:8080/h2-console` com as credenciais:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(vazio)* |

---

## 📚 Referências

- [Hexagonal Architecture — Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Get Your Hands Dirty on Clean Architecture — Tom Hombergs](https://reflectoring.io/book/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
