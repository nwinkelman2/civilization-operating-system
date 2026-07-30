ALTER TABLE resource_regions ADD COLUMN soil_fertility DOUBLE PRECISION DEFAULT 100.0;
ALTER TABLE resource_regions ADD COLUMN water_table DOUBLE PRECISION DEFAULT 100.0;

-- Update existing regions to default values
UPDATE resource_regions SET soil_fertility = 100.0, water_table = 100.0;
