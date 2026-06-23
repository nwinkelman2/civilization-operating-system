CREATE TABLE IF NOT EXISTS trade_agreements (
    id BIGSERIAL PRIMARY KEY,
    from_civilization_id BIGINT NOT NULL,
    to_civilization_id BIGINT NOT NULL,
    resource_type VARCHAR(255),
    quantity DOUBLE PRECISION,
    status VARCHAR(31) NOT NULL,
    created_at TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS game_events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    type VARCHAR(31) NOT NULL,
    severity VARCHAR(31) NOT NULL,
    target_civilization_id BIGINT,
    effect_json TEXT,
    created_at TIMESTAMP,
    resolved BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_trade_from_civ ON trade_agreements(from_civilization_id);
CREATE INDEX IF NOT EXISTS idx_trade_to_civ ON trade_agreements(to_civilization_id);
CREATE INDEX IF NOT EXISTS idx_game_events_civ ON game_events(target_civilization_id);
CREATE INDEX IF NOT EXISTS idx_game_events_resolved ON game_events(resolved);
