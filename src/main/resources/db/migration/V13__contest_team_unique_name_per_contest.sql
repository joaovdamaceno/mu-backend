CREATE UNIQUE INDEX IF NOT EXISTS uk_contest_teams_contest_team_name
    ON contest_teams (contest_id, LOWER(team_name));
