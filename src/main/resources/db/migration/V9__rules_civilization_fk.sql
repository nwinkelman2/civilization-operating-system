-- V9: Add civilization_id to constitutional_rules for per-civilization governance
ALTER TABLE constitutional_rules ADD COLUMN IF NOT EXISTS civilization_id BIGINT REFERENCES civilizations(id);

-- Index for fast per-civilization queries
CREATE INDEX IF NOT EXISTS idx_constitutional_rules_civ ON constitutional_rules(civilization_id);
