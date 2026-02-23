ALTER TABLE exercises DROP CONSTRAINT IF EXISTS fk_exercises_lesson;
ALTER TABLE exercises DROP COLUMN IF EXISTS lesson_id;

ALTER TABLE extra_materials ADD COLUMN IF NOT EXISTS module_id BIGINT;

UPDATE extra_materials em
SET module_id = l.module_id
FROM lessons l
WHERE em.lesson_id = l.id
  AND em.module_id IS NULL;

ALTER TABLE extra_materials ALTER COLUMN module_id SET NOT NULL;
ALTER TABLE extra_materials DROP CONSTRAINT IF EXISTS fk_extra_materials_lessons;
ALTER TABLE extra_materials DROP COLUMN IF EXISTS lesson_id;
ALTER TABLE extra_materials
    ADD CONSTRAINT fk_extra_materials_module
        FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE;
