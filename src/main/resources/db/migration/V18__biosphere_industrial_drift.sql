ALTER TABLE constitutional_rules ADD COLUMN sector VARCHAR(50) DEFAULT 'GENERAL';
ALTER TABLE biosphere_metrics ADD COLUMN drift_factor DOUBLE PRECISION DEFAULT 0.0;

INSERT INTO biosphere_metrics (name, metric_value, unit, safety_limit, status, drift, drift_factor) 
VALUES ('Qualidade do Ar', 100.0, '%', 60.0, 'NORMAL', 0.0, 0.0);

INSERT INTO biosphere_metrics (name, metric_value, unit, safety_limit, status, drift, drift_factor) 
VALUES ('Pegada de Carbono', 20.0, 'ppm', 100.0, 'NORMAL', 0.0, 0.0);
