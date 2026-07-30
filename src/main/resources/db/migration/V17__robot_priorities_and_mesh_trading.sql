-- Add robot priority weights to civilizations
ALTER TABLE civilizations ADD COLUMN agri_bots_priority INT DEFAULT 25;
ALTER TABLE civilizations ADD COLUMN aqua_bots_priority INT DEFAULT 25;
ALTER TABLE civilizations ADD COLUMN explore_bots_priority INT DEFAULT 25;
ALTER TABLE civilizations ADD COLUMN utility_bots_priority INT DEFAULT 25;

-- Create ledger for autonomous mesh trades
CREATE TABLE mesh_trades (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    requested_resource VARCHAR(50) NOT NULL,
    requested_amount DOUBLE PRECISION NOT NULL,
    offered_resource VARCHAR(50) NOT NULL,
    offered_amount DOUBLE PRECISION NOT NULL,
    trade_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES civilizations(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES civilizations(id) ON DELETE CASCADE
);

-- Create table for personnel/citizen migration flow requests
CREATE TABLE migration_requests (
    id BIGSERIAL PRIMARY KEY,
    citizen_name VARCHAR(255) NOT NULL,
    from_civilization_id BIGINT NOT NULL,
    to_civilization_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_civilization_id) REFERENCES civilizations(id) ON DELETE CASCADE,
    FOREIGN KEY (to_civilization_id) REFERENCES civilizations(id) ON DELETE CASCADE
);
