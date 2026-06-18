CREATE TABLE IF NOT EXISTS resource_regions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    scale VARCHAR(31) NOT NULL,
    location geometry(Point, 4326),
    food_availability DOUBLE PRECISION,
    water_availability DOUBLE PRECISION,
    mineral_availability DOUBLE PRECISION,
    energy_availability DOUBLE PRECISION,
    housing_availability DOUBLE PRECISION,
    dominant_resource VARCHAR(31),
    radius_km DOUBLE PRECISION,
    claimed BOOLEAN DEFAULT FALSE,
    claimed_by_civilization_id BIGINT
);

CREATE TABLE IF NOT EXISTS technologies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    category VARCHAR(31) NOT NULL,
    status VARCHAR(31) NOT NULL,
    research_cost INTEGER,
    research_progress INTEGER,
    tier INTEGER,
    unlocks_resource_bonus VARCHAR(500),
    prerequisites VARCHAR(500),
    civilization_id BIGINT
);

-- Add home_region_id to civilizations if not exists
ALTER TABLE civilizations ADD COLUMN IF NOT EXISTS home_region_id BIGINT REFERENCES resource_regions(id);

CREATE INDEX IF NOT EXISTS idx_resource_regions_scale ON resource_regions(scale);
CREATE INDEX IF NOT EXISTS idx_resource_regions_claimed ON resource_regions(claimed);
CREATE INDEX IF NOT EXISTS idx_technologies_civ ON technologies(civilization_id);
