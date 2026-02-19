-- Seed default authentication users
-- Idempotent: safe to run multiple times across environments
INSERT INTO users (username, password_hash, role)
VALUES
    ('admin', '$2a$10$abcdefghijklmnopqrstuu5Lo0g67CiD3M4RpN1BmBb4Crp5w7dbK', 'ADMIN'),
    ('aluno', '$2a$10$abcdefghijklmnopqrstuu5Lo0g67CiD3M4RpN1BmBb4Crp5w7dbK', 'USER')
ON CONFLICT (username) DO NOTHING;
