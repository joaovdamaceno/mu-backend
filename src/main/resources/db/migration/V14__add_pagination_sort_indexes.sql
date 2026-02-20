-- Rationale:
-- 1) posts list endpoint orders by updated_at DESC, id DESC (see PostController).
--    A composite index in the same ordering helps the planner avoid a full sort for
--    paginated reads and enables an index scan/top-N plan.
-- 2) contests list endpoint orders by start_datetime DESC, id DESC (see ContestController).
--    Existing idx_contests_start_datetime supports only the first key; adding id as
--    tie-breaker in the index reduces extra sorting work when many rows share timestamps.
-- 3) modules list endpoint orders by id ASC. No extra index is created because the
--    PRIMARY KEY on modules(id) already provides the required access path; adding another
--    index on (id) would be redundant.

CREATE INDEX IF NOT EXISTS idx_posts_updated_at_id_desc
    ON posts (updated_at DESC, id DESC);

CREATE INDEX IF NOT EXISTS idx_contests_start_datetime_id_desc
    ON contests (start_datetime DESC, id DESC);
