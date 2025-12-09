CREATE TABLE IF NOT EXISTS exercise_tags (
    exercise_id BIGINT NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    tag         VARCHAR(100) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_exercise_tags_exercise_id ON exercise_tags(exercise_id);
