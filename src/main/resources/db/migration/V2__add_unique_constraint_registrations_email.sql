-- Add unique constraint on registration emails
ALTER TABLE registrations
    ADD CONSTRAINT registrations_email_unique UNIQUE (email);
