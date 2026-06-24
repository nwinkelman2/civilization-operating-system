-- V8: Seed 50 iconic world cities as claimable resource regions
-- Each city has real lat/lng coordinates and thematic dominant resources
-- All start as unclaimed (claimed = false)

INSERT INTO resource_regions (name, description, scale, location, food_availability, water_availability, mineral_availability, energy_availability, housing_availability, dominant_resource, radius_km, claimed)
VALUES

-- NORTH AMERICA
('New York', 'The city that never sleeps — a global financial and cultural powerhouse on the Atlantic coast.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(-74.0060, 40.7128), 4326), 55, 60, 45, 80, 90, 'HOUSING', 120, false),

('Los Angeles', 'Entertainment capital of the world, sprawling across the Pacific coastline of California.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(-118.2437, 34.0522), 4326), 60, 35, 40, 75, 85, 'ENERGY', 150, false),

('São Paulo', 'South America''s largest metropolis and Brazil''s economic engine, rich in industry and diversity.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(-46.6333, -23.5505), 4326), 80, 70, 55, 65, 75, 'FOOD', 130, false),

('Mexico City', 'Ancient Aztec capital reborn as a megacity, nestled in a high-altitude valley.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-99.1332, 19.4326), 4326), 75, 45, 60, 60, 80, 'FOOD', 110, false),

('Toronto', 'Canada''s most diverse city, a hub of finance and innovation on the shores of Lake Ontario.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-79.3832, 43.6532), 4326), 65, 80, 50, 70, 88, 'WATER', 100, false),

('Chicago', 'The Windy City — America''s heartland hub of trade, architecture, and deep-dish culture.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(-87.6298, 41.8781), 4326), 70, 75, 55, 65, 82, 'WATER', 95, false),

('Buenos Aires', 'The Paris of South America — a cultural capital on the mouth of the Rio de la Plata.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-58.3816, -34.6037), 4326), 80, 65, 45, 60, 85, 'FOOD', 105, false),

-- EUROPE
('London', 'A millennia-old metropolis at the center of global finance, culture, and history.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(-0.1278, 51.5074), 4326), 55, 65, 40, 72, 88, 'HOUSING', 125, false),

('Paris', 'The City of Light — world capital of art, fashion, gastronomy, and romance.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(2.3522, 48.8566), 4326), 70, 60, 35, 68, 90, 'FOOD', 110, false),

('Berlin', 'Germany''s vibrant capital — a city reborn from history, now leading European tech and culture.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(13.4050, 52.5200), 4326), 65, 70, 50, 75, 85, 'ENERGY', 100, false),

('Amsterdam', 'The Venice of the North — a global hub of trade, innovation, and waterway engineering.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(4.9041, 52.3676), 4326), 60, 90, 40, 70, 80, 'WATER', 80, false),

('Madrid', 'Spain''s sun-drenched capital, a powerhouse of Iberian culture and Mediterranean energy.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-3.7038, 40.4168), 4326), 72, 45, 45, 65, 82, 'FOOD', 90, false),

('Rome', 'The Eternal City — three thousand years of civilization, art, and Mediterranean flavor.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(12.4964, 41.9028), 4326), 75, 55, 60, 62, 78, 'FOOD', 95, false),

('Stockholm', 'Capital of Scandinavia, a green innovator built across fourteen islands in the Baltic.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(18.0686, 59.3293), 4326), 58, 85, 55, 80, 82, 'WATER', 85, false),

('Warsaw', 'Poland''s phoenix capital — rebuilt from ashes, now leading Central European growth.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(21.0122, 52.2297), 4326), 68, 65, 60, 65, 80, 'MINERAL', 88, false),

('Vienna', 'The imperial city of music and culture, sitting at the crossroads of Central Europe.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(16.3738, 48.2082), 4326), 72, 72, 50, 70, 90, 'HOUSING', 92, false),

('Istanbul', 'The city that bridges Europe and Asia — ancient, cosmopolitan, and endlessly dynamic.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(28.9784, 41.0082), 4326), 78, 60, 55, 65, 82, 'FOOD', 115, false),

-- ASIA
('Tokyo', 'The world''s most populous metropolis — a hyper-modern city where tradition meets the future.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(139.6917, 35.6895), 4326), 70, 65, 50, 90, 92, 'ENERGY', 140, false),

('Beijing', 'China''s imperial capital and political center — ancient palaces amid a sea of innovation.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(116.3975, 39.9075), 4326), 68, 50, 65, 85, 88, 'MINERAL', 130, false),

('Shanghai', 'China''s financial giant — a glittering skyline on the Yangtze Delta, gateway to the East.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(121.4737, 31.2304), 4326), 72, 60, 55, 88, 90, 'ENERGY', 135, false),

('Mumbai', 'India''s city of dreams — the financial and entertainment capital of the subcontinent.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(72.8777, 19.0760), 4326), 75, 55, 50, 70, 78, 'FOOD', 120, false),

('Delhi', 'India''s ancient and modern capital — the seat of power at the heart of South Asia.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(77.1025, 28.7041), 4326), 78, 45, 55, 72, 80, 'FOOD', 125, false),

('Seoul', 'South Korea''s dynamic capital — global leader in technology, K-culture, and urban density.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(126.9780, 37.5665), 4326), 65, 68, 55, 85, 88, 'ENERGY', 110, false),

('Singapore', 'The lion city — a tiny island state that became a global nexus of trade, finance, and technology.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(103.8198, 1.3521), 4326), 55, 70, 40, 88, 90, 'ENERGY', 70, false),

('Dubai', 'The desert jewel of the Gulf — a city of superlatives built on oil wealth and ambition.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(55.2708, 25.2048), 4326), 30, 20, 45, 98, 92, 'ENERGY', 100, false),

('Bangkok', 'The City of Angels — Southeast Asia''s bustling hub of temples, trade, and street food.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(100.5018, 13.7563), 4326), 85, 65, 45, 65, 75, 'FOOD', 105, false),

('Jakarta', 'Indonesia''s colossal capital — a megacity of 35 million at the maritime heart of ASEAN.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(106.8456, -6.2088), 4326), 80, 60, 50, 68, 72, 'FOOD', 120, false),

('Karachi', 'Pakistan''s coastal megacity — the subcontinent''s largest port and commercial center.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(67.0099, 24.8607), 4326), 70, 40, 55, 65, 68, 'MINERAL', 100, false),

('Tehran', 'Iran''s sprawling mountain capital — a city of ancient culture and modern geopolitical weight.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(51.3890, 35.6892), 4326), 65, 40, 70, 85, 75, 'MINERAL', 100, false),

('Riyadh', 'The heartland of Arabia — Saudi Arabia''s modern capital rising from the desert sands.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(46.7219, 24.6877), 4326), 25, 15, 50, 98, 85, 'ENERGY', 90, false),

-- AFRICA
('Cairo', 'Gift of the Nile — Africa''s largest city, guardian of the pyramids and Pharaonic legacy.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(31.2357, 30.0444), 4326), 75, 50, 60, 65, 78, 'WATER', 120, false),

('Lagos', 'The Giant of Africa — Nigeria''s electric megacity and the continent''s economic powerhouse.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(3.3792, 6.5244), 4326), 80, 55, 60, 70, 65, 'FOOD', 110, false),

('Nairobi', 'Africa''s Silicon Savannah — Kenya''s high-altitude capital amid wildlife and tech innovation.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(36.8219, -1.2921), 4326), 75, 65, 55, 60, 70, 'FOOD', 90, false),

('Johannesburg', 'City of Gold — South Africa''s economic engine built on mineral wealth and modern ambition.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(28.0473, -26.2041), 4326), 60, 50, 90, 70, 75, 'MINERAL', 100, false),

('Kinshasa', 'Capital of the Congo — a raw, explosive city on the banks of the world''s deepest river.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(15.3222, -4.3217), 4326), 85, 80, 65, 55, 58, 'WATER', 105, false),

('Casablanca', 'Morocco''s white city and Atlantic port — Africa''s gateway to Europe and global trade.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(-7.5898, 33.5731), 4326), 65, 50, 50, 60, 75, 'FOOD', 80, false),

-- OCEANIA
('Sydney', 'Australia''s iconic harbor city — where golden beaches meet global finance and culture.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(151.2093, -33.8688), 4326), 65, 75, 60, 72, 88, 'WATER', 100, false),

('Melbourne', 'Australia''s cultural capital — world-renowned for coffee, sport, and multicultural energy.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(144.9631, -37.8136), 4326), 68, 72, 60, 70, 90, 'HOUSING', 95, false),

-- CENTRAL ASIA & RUSSIA
('Moscow', 'The heart of Russia — a vast, powerful capital of history, culture, and continental dominance.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(37.6173, 55.7558), 4326), 62, 68, 80, 85, 85, 'MINERAL', 130, false),

('Almaty', 'Kazakhstan''s modern jewel nestled against the Tian Shan mountains — gateway to Central Asia.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(76.8512, 43.2220), 4326), 65, 60, 75, 70, 72, 'MINERAL', 80, false),

-- SOUTH AMERICA (EXTRA)
('Bogotá', 'Colombia''s high-altitude capital — a city of emeralds, coffee, and emerging innovation.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-74.0721, 4.7110), 4326), 80, 70, 65, 60, 75, 'FOOD', 90, false),

('Lima', 'Peru''s coastal capital — the gastronomic heart of South America and gateway to the Andes.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-77.0428, -12.0464), 4326), 78, 45, 70, 60, 72, 'MINERAL', 90, false),

('Santiago', 'Chile''s modern Andean capital — copper-rich, earthquake-tested, and innovation-hungry.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-70.6693, -33.4489), 4326), 70, 55, 88, 65, 80, 'MINERAL', 88, false),

-- SPECIAL REGIONS
('Amazônia', 'The lungs of the Earth — the world''s largest tropical rainforest, teeming with life and resources.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(-60.0000, -3.4653), 4326), 98, 98, 40, 50, 20, 'FOOD', 400, false),

('Siberia', 'The frozen giant — Russia''s vast wilderness holding a third of the world''s timber and minerals.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(105.0000, 62.0000), 4326), 40, 85, 95, 60, 15, 'MINERAL', 500, false),

('Sahara', 'The great desert sea — sun-scorched and mineral-rich, with untapped solar energy potential.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(15.0000, 23.0000), 4326), 10, 5, 65, 98, 10, 'ENERGY', 600, false),

('Arctic Circle', 'The frozen frontier — melting ice reveals vast reserves of minerals and rare earth metals.',
 'GLOBAL', ST_SetSRID(ST_MakePoint(0.0000, 80.0000), 4326), 15, 90, 88, 45, 8, 'MINERAL', 700, false),

('Silicon Valley', 'The innovation engine of humanity — where the future is coded and startups become empires.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(-122.0308, 37.3382), 4326), 50, 40, 35, 95, 85, 'ENERGY', 80, false),

('Shenzhen', 'China''s original Special Economic Zone — the world''s electronics factory and tech frontier.',
 'CONTINENTAL', ST_SetSRID(ST_MakePoint(114.0579, 22.5431), 4326), 65, 55, 60, 92, 88, 'ENERGY', 85, false),

('Cape Town', 'The Mother City — where the Atlantic meets the Indian Ocean, a jewel at Africa''s southern tip.',
 'REGIONAL', ST_SetSRID(ST_MakePoint(18.4241, -33.9249), 4326), 68, 65, 70, 62, 82, 'WATER', 88, false);
