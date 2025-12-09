-- Align extra_materials table with ExtraMaterial entity
ALTER TABLE extra_materials
    ADD COLUMN type VARCHAR(200),
    ADD COLUMN url TEXT,
    ADD COLUMN lesson_id BIGINT;

-- Backfill new columns from existing data
UPDATE extra_materials em
SET type = em.title,
    url = em.link,
    lesson_id = (
        SELECT l.id
        FROM lessons l
        WHERE l.module_id = em.module_id
        ORDER BY l.position
        LIMIT 1
    );

-- Enforce new constraints
ALTER TABLE extra_materials
    ALTER COLUMN type SET NOT NULL,
    ALTER COLUMN url SET NOT NULL,
    ALTER COLUMN lesson_id SET NOT NULL;

ALTER TABLE extra_materials
    ADD CONSTRAINT fk_extra_materials_lessons
        FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE;

-- Remove obsolete columns
ALTER TABLE extra_materials
    DROP COLUMN module_id,
    DROP COLUMN title,
    DROP COLUMN link;
