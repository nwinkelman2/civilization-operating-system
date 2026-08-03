-- Seed Resource Regions (LOCAL scale)
INSERT INTO resource_regions (name, description, scale, location, food_availability, water_availability, mineral_availability, energy_availability, housing_availability, dominant_resource, radius_km, claimed)
VALUES
    ('Fertile Valley', 'Rich alluvial soil with abundant freshwater springs.', 'LOCAL', ST_SetSRID(ST_MakePoint(-46.63, -23.55), 4326), 85.0, 90.0, 20.0, 30.0, 40.0, 'FOOD', 15.0, false),
    ('Granite Highlands', 'Mineral-rich highlands with strong winds for energy.', 'LOCAL', ST_SetSRID(ST_MakePoint(-45.0, -22.5), 4326), 10.0, 40.0, 80.0, 70.0, 20.0, 'MINERAL', 20.0, false),
    ('Coastal Delta', 'Mangrove delta with abundant water and marine resources.', 'LOCAL', ST_SetSRID(ST_MakePoint(-44.0, -23.0), 4326), 60.0, 95.0, 30.0, 50.0, 50.0, 'WATER', 18.0, false);

-- Seed Resources
INSERT INTO resources (name, type, description, location, quantity, unit)
VALUES
    ('Local Community Garden', 'FOOD', 'Small-scale permaculture garden supplying the local settlement.', ST_SetSRID(ST_MakePoint(-46.63, -23.55), 4326), 2.0, 'Tons/Month'),
    ('Rainwater Retention Pond', 'WATER', 'Local rainwater catchment system providing potable water.', ST_SetSRID(ST_MakePoint(-46.64, -23.56), 4326), 500.0, 'Cubic Meters');

-- Seed Needs
INSERT INTO needs (category, region, description, quantity, unit, priority, status)
VALUES
    ('HOUSING', 'Local Settlement', 'Basic sustainable housing for the local community.', 0.5, 'Thousand Units', 5, 'UNMET'),
    ('FOOD', 'Local Settlement', 'Daily nutritional requirements for the local population.', 2.0, 'Tons/Day', 5, 'PARTIAL');

-- Seed Facilities
INSERT INTO production_facilities (name, type, region, efficiency, status, current_output)
VALUES ('Community Micro-Farm', 'VERTICAL_FARM', 'Local Settlement', 0.85, 'ACTIVE', '500 kg/day');

-- Seed Constitutional Rules
INSERT INTO constitutional_rules (title, description, logic_code, status, validation_status, validated_by, votes_count)
VALUES
    ('Community Water Stewardship', 'All households must maintain rainwater catchment systems.',
     '{"type": "THRESHOLD_TRIGGER", "metric": "WATER", "action": "RESTRICT_USAGE"}',
     'ACTIVE', 'SCIENTIFICALLY_VALIDATED', 'Local Council', 150);

-- Seed Scientific Committees
INSERT INTO scientific_committees (area, name, mandate, validation_level)
VALUES ('LOCAL', 'Local Community Council', 'Overseeing local resource allocation and community well-being.', 'COMMUNITY_VALIDATED');

-- Seed Automation Units
INSERT INTO automation_units (name, type, region, status, current_task)
VALUES ('Community Maint-Bot', 'BOT', 'Local Settlement', 'ACTIVE', 'STANDBY');

-- Seed Biosphere Metrics
INSERT INTO biosphere_metrics (name, last_updated, metric_value, unit, safety_limit, status, drift)
VALUES
    ('Local Air Quality Index', NOW(), 42.0, 'AQI', 50.0, 'NORMAL', 1.2),
    ('Local Stream pH Level', NOW(), 7.2, 'pH', 6.5, 'NORMAL', 0.1);

-- Seed Skills
INSERT INTO skills (name, category, description)
VALUES ('Sustainable Agriculture', 'AGRICULTURE', 'Local food production techniques.');

-- Seed Citizens
INSERT INTO citizens (citizen_id, name, reputation_score, biographical_note)
VALUES ('CIT-LOCAL-01', 'Local Pioneer', 50.0, 'Founding member of the local settlement.')
ON CONFLICT (citizen_id) DO NOTHING;
