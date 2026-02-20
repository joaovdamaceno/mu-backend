# Deployment configuration

## Required environment variables

Provide these variables to configure the application in all environments:

- `DB_URL`: JDBC URL for the PostgreSQL database.
- `DB_USERNAME`: Database username.
- `DB_PASSWORD`: Database password.
- `JWT_SECRET`: HMAC signing secret (use a rotated value of at least 32 characters).
- `SERVER_PORT` (optional): HTTP port for the Spring Boot server (defaults to `8080`).


## Bootstrap de autenticação (somente dev/local)

- Produção (`prod`) **não** cria contas padrão automaticamente.
- O bootstrap em código só roda com perfil `dev` ou `local`.
- Para criar o usuário administrativo local (`admin`), defina `BOOTSTRAP_ADMIN_PASSWORD` com uma senha forte.
- No Docker Compose, inclua `BOOTSTRAP_ADMIN_PASSWORD` no `.env` para repassar a senha ao container `app`.
- Se `BOOTSTRAP_ADMIN_PASSWORD` não for definida, nenhum usuário de autenticação é criado automaticamente.
- O processo é idempotente: se `admin` já existir, o bootstrap não recria o usuário.

## Banco e dados iniciais

- O Flyway cria/atualiza apenas o schema e não semeia usuários de autenticação padrão.
- O `populate.sql` preenche apenas dados de conteúdo/domínio (módulos, aulas, exercícios, posts e inscrições) e não altera a tabela `users`.

## JWT library decision

- O projeto está padronizado em `io.jsonwebtoken` (JJWT) para emissão e validação de tokens.
- A dependência `com.auth0:java-jwt` foi removida por não haver imports/uso no código de `src/main/java` e `src/test/java`.
- Não há migração planejada no momento entre bibliotecas JWT; qualquer mudança futura deve ser registrada via ADR antes da adoção.

## Secret rotation

Any previously committed secrets should be considered compromised. Rotate database
credentials and JWT signing secrets before deploying.

## Example

Copy the example env file and update with real secrets:

If port `8080` is already in use locally, set `SERVER_PORT` in `.env` (for example `SERVER_PORT=8081`) before starting the app.

```bash
cp .env.example .env
```


## IntelliJ local run support

The application now imports values from a project-root `.env` file automatically via
Spring Boot config import. With this in place, IntelliJ run configurations no longer
need manual environment-variable entries for local development.

## Docker Compose

1. Copy the environment file template:
   ```bash
   cp .env.example .env
   ```
2. Update values (especially `JWT_SECRET` and PostgreSQL credentials).
3. Start services:
   ```bash
   docker compose up --build -d
   ```
4. Stop services:
   ```bash
   docker compose down
   ```
