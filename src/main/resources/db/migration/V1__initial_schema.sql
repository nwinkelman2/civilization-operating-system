CREATE EXTENSION IF NOT EXISTS postgis;

-- Resources
CREATE TABLE resources (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(31) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(255) NOT NULL,
    location geometry(Point, 4326)
);

-- Needs
CREATE TABLE needs (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(31) NOT NULL,
    region VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(255) NOT NULL,
    priority INTEGER NOT NULL,
    status VARCHAR(31) NOT NULL
);

-- Production Facilities
CREATE TABLE production_facilities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(31) NOT NULL,
    region VARCHAR(255) NOT NULL,
    efficiency DOUBLE PRECISION NOT NULL,
    status VARCHAR(31) NOT NULL,
    current_output VARCHAR(255)
);

-- Shipments
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    cargo VARCHAR(255) NOT NULL,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(255) NOT NULL,
    status VARCHAR(31) NOT NULL,
    eta TIMESTAMP
);

-- Interactions
CREATE TABLE interactions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(31) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    region VARCHAR(255) NOT NULL,
    citizen_id VARCHAR(255) NOT NULL,
    status VARCHAR(31) NOT NULL,
    created_at TIMESTAMP
);

-- Biosphere Metrics
CREATE TABLE biosphere_metrics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_updated TIMESTAMP,
    metric_value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(255) NOT NULL,
    safety_limit DOUBLE PRECISION NOT NULL,
    status VARCHAR(31) NOT NULL,
    drift DOUBLE PRECISION NOT NULL
);

-- Constitutional Rules
CREATE TABLE constitutional_rules (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    logic_code TEXT NOT NULL,
    status VARCHAR(31) NOT NULL,
    validation_status VARCHAR(31) NOT NULL,
    validated_by VARCHAR(255),
    votes_count INTEGER,
    created_at TIMESTAMP
);

-- Automation Units
CREATE TABLE automation_units (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(31) NOT NULL,
    region VARCHAR(255) NOT NULL,
    status VARCHAR(31) NOT NULL,
    current_task VARCHAR(255) NOT NULL,
    last_ping TIMESTAMP
);

-- Scientific Committees
CREATE TABLE scientific_committees (
    id BIGSERIAL PRIMARY KEY,
    area VARCHAR(31) NOT NULL,
    name VARCHAR(255) NOT NULL,
    mandate TEXT,
    validation_level VARCHAR(31) NOT NULL,
    last_audit TIMESTAMP
);

-- Skills
CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(31) NOT NULL,
    description VARCHAR(1000)
);

-- Citizens
CREATE TABLE citizens (
    id BIGSERIAL PRIMARY KEY,
    citizen_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    reputation_score DOUBLE PRECISION,
    biographical_note TEXT
);

-- Citizen-Skill many-to-many
CREATE TABLE citizen_skills (
    citizen_id BIGINT NOT NULL REFERENCES citizens(id),
    skill_id BIGINT NOT NULL REFERENCES skills(id),
    PRIMARY KEY (citizen_id, skill_id)
);

-- Citizen interests element collection
CREATE TABLE citizen_interests (
    citizen_id BIGINT NOT NULL REFERENCES citizens(id),
    interests VARCHAR(255)
);

-- Contribution Projects
CREATE TABLE contribution_projects (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    category VARCHAR(31) NOT NULL,
    impact_area VARCHAR(31) NOT NULL,
    status VARCHAR(31) NOT NULL,
    created_at TIMESTAMP
);

-- Contributions
CREATE TABLE contributions (
    id BIGSERIAL PRIMARY KEY,
    citizen_id BIGINT REFERENCES citizens(id),
    project_id BIGINT REFERENCES contribution_projects(id),
    role VARCHAR(255) NOT NULL,
    impact_score DOUBLE PRECISION NOT NULL,
    contribution_date TIMESTAMP
);

-- Social Incidents
CREATE TABLE social_incidents (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(31) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    risk_level VARCHAR(31) NOT NULL,
    status VARCHAR(31) NOT NULL,
    reported_at TIMESTAMP
);

-- Incident participant IDs element collection
CREATE TABLE social_incidents_participant_ids (
    social_incidents_id BIGINT NOT NULL REFERENCES social_incidents(id),
    participant_ids VARCHAR(255)
);

-- Behavior Assessments
CREATE TABLE behavior_assessments (
    id BIGSERIAL PRIMARY KEY,
    citizen_id VARCHAR(255) NOT NULL,
    psychological_profile TEXT,
    risk_score DOUBLE PRECISION,
    social_factors TEXT,
    assessed_at TIMESTAMP
);

-- Social Cases
CREATE TABLE social_cases (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT UNIQUE REFERENCES social_incidents(id),
    status VARCHAR(31) NOT NULL,
    resolution_plan TEXT,
    rehabilitation_program TEXT,
    monitoring_plan TEXT,
    updated_at TIMESTAMP
);

-- Case panel expert IDs element collection
CREATE TABLE social_cases_panel_expert_ids (
    social_cases_id BIGINT NOT NULL REFERENCES social_cases(id),
    panel_expert_ids VARCHAR(255)
);

-- Project required skill names element collection
CREATE TABLE contribution_projects_required_skill_names (
    contribution_projects_id BIGINT NOT NULL REFERENCES contribution_projects(id),
    required_skill_names VARCHAR(255)
);
