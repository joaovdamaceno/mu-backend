-- Add new registration fields to match Registration entity
ALTER TABLE registrations
    ADD COLUMN campus VARCHAR(150) NOT NULL DEFAULT '',
    ADD COLUMN course VARCHAR(150) NOT NULL DEFAULT '',
    ADD COLUMN semester VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN how_did_you_hear VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN previous_experience TEXT,
    ADD COLUMN message TEXT;
