ALTER TABLE contribution_projects ADD COLUMN civilization_id BIGINT REFERENCES civilizations(id);
CREATE INDEX idx_projects_civilization_id ON contribution_projects(civilization_id);
