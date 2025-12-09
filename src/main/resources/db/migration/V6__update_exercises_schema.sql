-- Align exercises table with updated Exercise entity mappings
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS oj_url VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS oj_name VARCHAR(255) DEFAULT 'Unknown';
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS lesson_id BIGINT;

-- Store enum values as strings instead of ordinals
ALTER TABLE exercises ALTER COLUMN difficulty SET DATA TYPE VARCHAR(255);

UPDATE exercises
SET difficulty = CASE difficulty
    WHEN '0' THEN 'EASY'
    WHEN '1' THEN 'MEDIUM'
    WHEN '2' THEN 'HARD'
    -- In case some rows were already coerced into textual values without quotes
    WHEN 'EASY' THEN 'EASY'
    WHEN 'MEDIUM' THEN 'MEDIUM'
    WHEN 'HARD' THEN 'HARD'
    ELSE difficulty
END;

-- Preserve existing URLs and ensure required fields are populated
UPDATE exercises SET oj_url = link WHERE oj_url IS NULL;
UPDATE exercises SET oj_name = COALESCE(oj_name, 'Unknown');

-- Associate exercises with the first lesson of their module
UPDATE exercises e
SET lesson_id = (
    SELECT l.id
    FROM lessons l
    WHERE l.module_id = e.module_id
    ORDER BY l.position
    LIMIT 1
)
WHERE lesson_id IS NULL;

ALTER TABLE exercises ALTER COLUMN oj_url SET NOT NULL;
ALTER TABLE exercises ALTER COLUMN oj_name SET NOT NULL;
ALTER TABLE exercises ALTER COLUMN lesson_id SET NOT NULL;

ALTER TABLE exercises DROP COLUMN IF EXISTS link;

ALTER TABLE exercises
    ADD CONSTRAINT fk_exercises_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE;
