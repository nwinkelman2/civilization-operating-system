-- Add additional robot priority weights to civilizations
ALTER TABLE civilizations ADD COLUMN eco_bots_priority INT DEFAULT 0;
ALTER TABLE civilizations ADD COLUMN science_bots_priority INT DEFAULT 0;
ALTER TABLE civilizations ADD COLUMN security_bots_priority INT DEFAULT 0;
