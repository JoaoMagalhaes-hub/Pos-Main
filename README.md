# Projeto Pós Full Stack

Uma aplicação backend desenvolvida com **Java** e **Spring Boot** para gerenciamento de contatos. O projeto foi criado com foco em organização de código, separação de responsabilidades e aplicação de conceitos de **Clean Code**.

## Tecnologias Usadas

- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- H2 Database
- REST API

## Conceitos Aplicados

Durante o desenvolvimento foram utilizados conceitos como:

- Clean Code
- Separação de responsabilidades
- Arquitetura em camadas
- DTOs para entrada e saída de dados
- Tratamento centralizado de erros
- Organização modular do projeto

## Objetivo do Projeto

Este projeto foi desenvolvido com fins de estudo e prática de desenvolvimento backend utilizando Spring Boot, além de servir como exercício de refatoração e melhoria estrutural de um código que veio originalmente mal organizado e sem práticas de Clean Code.

## Como executar

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080` com banco H2 em memória (console em `/h2`).

Documentação Swagger disponível em `http://localhost:8080/swagger-ui.html`.

## Endpoints

| Método | URL | Descrição |
|--------|-----|-----------|
| POST   | `/contatos/incluir` | Inclui um contato (JSON no body) |
| GET    | `/contatos/{id}` | Busca um contato pelo ID |
| GET    | `/contatos/listar` | Lista todos os contatos ativos |
| GET    | `/contatos/pesquisar?tipoBusca=nome&valor=joao` | Pesquisa por critério (`nome`, `email`, `telefone`, `tipo`, `id`) |
| PUT    | `/contatos/editar/{id}` | Edita um contato |
| DELETE | `/contatos/excluir/{id}` | Desativa um contato (soft delete) |
| PATCH  | `/contatos/{id}/ativar` | Reativa um contato desativado |

### Exemplo de JSON para `POST /contatos/incluir`

```json
{
  "nome": "Maria Silva",
  "telefone": "11999998888",
  "email": "maria@exemplo.com",
  "endereco": "Rua A, 100",
  "idade": 30,
  "tipo": "AMIGO"
}
```

Tipos permitidos: `FAMILIA`, `AMIGO`, `TRABALHO`, `OUTRO`.
