ALTER TABLE lessons
    DROP COLUMN IF EXISTS slug,
    DROP COLUMN IF EXISTS summary;

ALTER TABLE exercises
    DROP COLUMN IF EXISTS oj_name;

ALTER TABLE extra_materials
    RENAME COLUMN type TO title;
