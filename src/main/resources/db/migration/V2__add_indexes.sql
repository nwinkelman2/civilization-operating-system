-- Common query indexes for civilization-operating-system

-- Needs: filtered by region and status
CREATE INDEX IF NOT EXISTS idx_needs_region ON needs(region);
CREATE INDEX IF NOT EXISTS idx_needs_status ON needs(status);
CREATE INDEX IF NOT EXISTS idx_needs_category ON needs(category);

-- Resources: filtered by type
CREATE INDEX IF NOT EXISTS idx_resources_type ON resources(type);

-- Shipments: filtered by status and origin/destination
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_shipments_origin ON shipments(origin);
CREATE INDEX IF NOT EXISTS idx_shipments_destination ON shipments(destination);

-- Automation units: filtered by region, type, status
CREATE INDEX IF NOT EXISTS idx_automation_units_region ON automation_units(region);
CREATE INDEX IF NOT EXISTS idx_automation_units_type ON automation_units(type);
CREATE INDEX IF NOT EXISTS idx_automation_units_status ON automation_units(status);

-- Social incidents: filtered by status, risk level, type
CREATE INDEX IF NOT EXISTS idx_social_incidents_status ON social_incidents(status);
CREATE INDEX IF NOT EXISTS idx_social_incidents_risk ON social_incidents(risk_level);
CREATE INDEX IF NOT EXISTS idx_social_incidents_type ON social_incidents(type);

-- Social cases: filtered by status
CREATE INDEX IF NOT EXISTS idx_social_cases_status ON social_cases(status);
CREATE INDEX IF NOT EXISTS idx_social_cases_incident ON social_cases(incident_id);

-- Behavior assessments: filtered by citizen_id
CREATE INDEX IF NOT EXISTS idx_behavior_assessments_citizen ON behavior_assessments(citizen_id);

-- Biosphere metrics: filtered by status
CREATE INDEX IF NOT EXISTS idx_biosphere_metrics_status ON biosphere_metrics(status);
CREATE INDEX IF NOT EXISTS idx_biosphere_metrics_name ON biosphere_metrics(name);

-- Production facilities: filtered by region, type, status
CREATE INDEX IF NOT EXISTS idx_production_facilities_region ON production_facilities(region);
CREATE INDEX IF NOT EXISTS idx_production_facilities_type ON production_facilities(type);
CREATE INDEX IF NOT EXISTS idx_production_facilities_status ON production_facilities(status);

-- Contributions: filtered by citizen
CREATE INDEX IF NOT EXISTS idx_contributions_citizen ON contributions(citizen_id);
CREATE INDEX IF NOT EXISTS idx_contributions_project ON contributions(project_id);

-- Projects: filtered by status
CREATE INDEX IF NOT EXISTS idx_contribution_projects_status ON contribution_projects(status);

-- Scientific committees: filtered by area and validation level
CREATE INDEX IF NOT EXISTS idx_scientific_committees_area ON scientific_committees(area);
CREATE INDEX IF NOT EXISTS idx_scientific_committees_validation ON scientific_committees(validation_level);

-- Interactions: filtered by region, type, status
CREATE INDEX IF NOT EXISTS idx_interactions_region ON interactions(region);
CREATE INDEX IF NOT EXISTS idx_interactions_type ON interactions(type);
CREATE INDEX IF NOT EXISTS idx_interactions_status ON interactions(status);

-- Constitutional rules: filtered by status and validation status
CREATE INDEX IF NOT EXISTS idx_constitutional_rules_status ON constitutional_rules(status);
CREATE INDEX IF NOT EXISTS idx_constitutional_rules_validation ON constitutional_rules(validation_status);
