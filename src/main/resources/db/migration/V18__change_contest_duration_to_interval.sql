ALTER TABLE contests
    ALTER COLUMN duration_minutes TYPE INTERVAL
        USING make_interval(mins => duration_minutes);

ALTER TABLE contests
    DROP CONSTRAINT IF EXISTS contests_duration_minutes_check;

ALTER TABLE contests
    ADD CONSTRAINT contests_duration_minutes_positive_check
        CHECK (duration_minutes > INTERVAL '0');
