-- Civilizations
CREATE TABLE IF NOT EXISTS civilizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    scale VARCHAR(31) NOT NULL,
    region VARCHAR(255) NOT NULL,
    status VARCHAR(31) NOT NULL,
    owner_token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    last_active_at TIMESTAMP,
    reputation_score DOUBLE PRECISION,
    population INTEGER
);

-- Voxtex Nodes
CREATE TABLE IF NOT EXISTS voxtex_nodes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    civilization_id BIGINT NOT NULL REFERENCES civilizations(id),
    type VARCHAR(31) NOT NULL,
    status VARCHAR(31) NOT NULL,
    region VARCHAR(255),
    knowledge_base TEXT,
    last_active_at TIMESTAMP,
    message_count INTEGER
);

-- Voxtex Messages
CREATE TABLE IF NOT EXISTS voxtex_messages (
    id BIGSERIAL PRIMARY KEY,
    source_node_id BIGINT NOT NULL REFERENCES voxtex_nodes(id),
    target_node_id BIGINT NOT NULL REFERENCES voxtex_nodes(id),
    message_type VARCHAR(31) NOT NULL,
    content VARCHAR(4000) NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    delivered_at TIMESTAMP,
    hop_count INTEGER,
    delivered BOOLEAN
);

-- Voxtex Connections
CREATE TABLE IF NOT EXISTS voxtex_connections (
    id BIGSERIAL PRIMARY KEY,
    node_a_id BIGINT NOT NULL REFERENCES voxtex_nodes(id),
    node_b_id BIGINT NOT NULL REFERENCES voxtex_nodes(id),
    strength DOUBLE PRECISION,
    latency_ms BIGINT,
    messages_exchanged INTEGER,
    established_at TIMESTAMP,
    last_activity_at TIMESTAMP
);
