# Deployment configuration

## Required environment variables

Provide these variables to configure the application in all environments:

- `DB_URL`: JDBC URL for the PostgreSQL database.
- `DB_USERNAME`: Database username.
- `DB_PASSWORD`: Database password.
- `JWT_SECRET`: HMAC signing secret (use a rotated value of at least 32 characters).

## Secret rotation

Any previously committed secrets should be considered compromised. Rotate database
credentials and JWT signing secrets before deploying.

## Example

Copy the example env file and update with real secrets:

```bash
cp .env.example .env
```

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
