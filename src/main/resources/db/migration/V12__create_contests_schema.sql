CREATE TABLE IF NOT EXISTS contests (
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(200) NOT NULL,
    duration_minutes       INT NOT NULL CHECK (duration_minutes > 0),
    start_datetime         TIMESTAMP NOT NULL,
    is_team_based          BOOLEAN NOT NULL,
    codeforces_mirror_url  TEXT,
    created_at             TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS contest_teams (
    id                 BIGSERIAL PRIMARY KEY,
    contest_id         BIGINT NOT NULL REFERENCES contests(id) ON DELETE CASCADE,
    team_name          VARCHAR(120) NOT NULL,
    coach_name         VARCHAR(150),
    institution        VARCHAR(150),
    reserve_name       VARCHAR(120),
    is_cafe_com_leite  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_contest_teams_contest_id_team_name'
    ) THEN
        ALTER TABLE contest_teams
            ADD CONSTRAINT uk_contest_teams_contest_id_team_name UNIQUE (contest_id, team_name);
    END IF;
END;
$$;

CREATE TABLE IF NOT EXISTS contest_team_members (
    id             BIGSERIAL PRIMARY KEY,
    team_id         BIGINT NOT NULL REFERENCES contest_teams(id) ON DELETE CASCADE,
    member_index    INT NOT NULL CHECK (member_index BETWEEN 1 AND 3),
    member_name     VARCHAR(120) NOT NULL,
    CONSTRAINT uk_contest_team_member_position UNIQUE (team_id, member_index)
);

CREATE INDEX IF NOT EXISTS idx_contests_start_datetime ON contests(start_datetime);
CREATE INDEX IF NOT EXISTS idx_contest_teams_contest_id ON contest_teams(contest_id);

CREATE OR REPLACE FUNCTION update_contests_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_contests_updated_at ON contests;
CREATE TRIGGER trg_contests_updated_at
    BEFORE UPDATE ON contests
    FOR EACH ROW
    EXECUTE FUNCTION update_contests_timestamp();
