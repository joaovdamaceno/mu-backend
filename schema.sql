-- 1) INSCRIÇÕES
CREATE TABLE IF NOT EXISTS registrations (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    whatsapp     VARCHAR(50),
    institution  VARCHAR(150),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 2) MÓDULOS
CREATE TABLE IF NOT EXISTS modules (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    notes        TEXT,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    published    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE OR REPLACE FUNCTION update_modules_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_modules_updated_at ON modules;
CREATE TRIGGER trg_modules_updated_at
    BEFORE UPDATE ON modules
    FOR EACH ROW
    EXECUTE FUNCTION update_modules_timestamp();

-- 3) AULAS (VÍDEOS)
CREATE TABLE IF NOT EXISTS lessons (
    id           BIGSERIAL PRIMARY KEY,
    module_id    BIGINT NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
    title        VARCHAR(200) NOT NULL,
    video_url    TEXT NOT NULL,
    position     INT NOT NULL
);

-- 4) EXERCÍCIOS
CREATE TABLE IF NOT EXISTS exercises (
    id           BIGSERIAL PRIMARY KEY,
    module_id    BIGINT NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
    title        VARCHAR(200) NOT NULL,
    link         TEXT NOT NULL,
    difficulty   SMALLINT NOT NULL  -- 1 = fácil (verde), 2 = médio, 3 = difícil (vermelho)
);

-- 5) MATERIAIS EXTRAS
CREATE TABLE IF NOT EXISTS extra_materials (
    id           BIGSERIAL PRIMARY KEY,
    module_id    BIGINT NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
    title        VARCHAR(200) NOT NULL,
    link         TEXT NOT NULL
);

-- 6) POSTS DO MURAL
CREATE TABLE IF NOT EXISTS posts (
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    tag              VARCHAR(50),
    cover_image_url  TEXT,
    main_text        TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 7) SEÇÕES DO POST (IMAGEM + TEXTO)
CREATE TABLE IF NOT EXISTS post_sections (
    id           BIGSERIAL PRIMARY KEY,
    post_id      BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    image_url    TEXT,
    text         TEXT,
    position     INT NOT NULL
);
