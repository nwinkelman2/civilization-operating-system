ALTER TABLE social_incidents ADD COLUMN civilization_id BIGINT REFERENCES civilizations(id);
CREATE INDEX idx_incidents_civilization_id ON social_incidents(civilization_id);
