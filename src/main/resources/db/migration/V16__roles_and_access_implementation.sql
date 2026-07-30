-- Add role and civilization link to citizens
ALTER TABLE citizens ADD COLUMN role VARCHAR(255);
ALTER TABLE citizens ADD COLUMN civilization_id BIGINT REFERENCES civilizations(id);

CREATE INDEX idx_citizens_civilization_id ON citizens(civilization_id);

-- Create citizen wallets
CREATE TABLE citizen_wallets (
    id BIGSERIAL PRIMARY KEY,
    citizen_id BIGINT NOT NULL UNIQUE REFERENCES citizens(id) ON DELETE CASCADE,
    food DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    water DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    minerals DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    energy DOUBLE PRECISION NOT NULL DEFAULT 0.0
);

-- Create delegate votes table
CREATE TABLE delegate_votes (
    id BIGSERIAL PRIMARY KEY,
    voter_citizen_id BIGINT NOT NULL REFERENCES citizens(id) ON DELETE CASCADE,
    candidate_citizen_id BIGINT NOT NULL REFERENCES citizens(id) ON DELETE CASCADE,
    sector VARCHAR(255) NOT NULL,
    civilization_id BIGINT NOT NULL REFERENCES civilizations(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_voter_sector_civ UNIQUE (voter_citizen_id, sector, civilization_id)
);
