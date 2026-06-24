-- V10: Fix invalid CivilizationScale value NATIONAL → REGIONAL in resource_regions
-- (NATIONAL was used in V8 seed but does not exist in the CivilizationScale enum)
UPDATE resource_regions SET scale = 'REGIONAL' WHERE scale = 'NATIONAL';
