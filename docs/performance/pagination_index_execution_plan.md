# Paginação: validação de benefício dos índices de ordenação

Este documento registra um plano de validação por `EXPLAIN (ANALYZE, BUFFERS)` para as listagens paginadas mais frequentes.

## 1) Posts (`ORDER BY updated_at DESC, id DESC`)

Consulta alvo (mesma ordenação do endpoint `/api/posts`):

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT id, updated_at, title
FROM posts
ORDER BY updated_at DESC, id DESC
LIMIT 20 OFFSET 0;
```

Validação esperada após a migration `V14`:
- presença de `Index Scan` ou `Index Only Scan` usando `idx_posts_updated_at_id_desc`;
- eliminação (ou redução relevante) de `Sort` explícito no plano para as primeiras páginas.

## 2) Contests (`ORDER BY start_datetime DESC, id DESC`)

Consulta alvo (mesma ordenação do endpoint `/api/contests`):

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT id, start_datetime, name
FROM contests
ORDER BY start_datetime DESC, id DESC
LIMIT 20 OFFSET 0;
```

Validação esperada após a migration `V14`:
- uso de `idx_contests_start_datetime_id_desc` no plano;
- menor custo de ordenação em relação ao cenário com índice apenas em `start_datetime`.

## 3) Modules (`ORDER BY id ASC`)

Consulta alvo (mesma ordenação do endpoint `/api/modules`):

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT id, title
FROM modules
ORDER BY id ASC
LIMIT 20 OFFSET 0;
```

Validação esperada:
- uso do índice implícito de chave primária (`modules_pkey`);
- nenhum índice adicional necessário para `modules(id)`.
