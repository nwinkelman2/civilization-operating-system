package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.github.opencivilizationplatform.modules.needs.infrastructure.NeedRepository;
import io.github.opencivilizationplatform.modules.production.domain.Facility;
import io.github.opencivilizationplatform.modules.production.infrastructure.FacilityRepository;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.github.opencivilizationplatform.modules.logistics.infrastructure.ShipmentRepository;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import io.github.opencivilizationplatform.modules.execution.infrastructure.AutomationUnitRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import io.github.opencivilizationplatform.modules.participation.infrastructure.InteractionRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import io.github.opencivilizationplatform.modules.governance.infrastructure.ScientificCommitteeRepository;
import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.infrastructure.BehaviorAssessmentRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.CaseRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.contribution.domain.Skill;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.ProjectRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class SeedDataConfig {

    private static final Logger log = LoggerFactory.getLogger(SeedDataConfig.class);

    private final CivilizationScale scale;
    private final ResourceRepository resourceRepository;
    private final NeedRepository needRepository;
    private final FacilityRepository facilityRepository;
    private final ShipmentRepository shipmentRepository;
    private final InteractionRepository interactionRepository;
    private final BiosphereMetricRepository biosphereMetricRepository;
    private final RuleRepository ruleRepository;
    private final AutomationUnitRepository automationUnitRepository;
    private final ScientificCommitteeRepository committeeRepository;
    private final IncidentRepository incidentRepository;
    private final BehaviorAssessmentRepository assessmentRepository;
    private final CaseRepository caseRepository;
    private final CitizenRepository citizenRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public SeedDataConfig(
            @Value("${civilization.scale:LOCAL}") CivilizationScale scale,
            ResourceRepository resourceRepository,
            NeedRepository needRepository,
            FacilityRepository facilityRepository,
            ShipmentRepository shipmentRepository,
            InteractionRepository interactionRepository,
            BiosphereMetricRepository biosphereMetricRepository,
            RuleRepository ruleRepository,
            AutomationUnitRepository automationUnitRepository,
            ScientificCommitteeRepository committeeRepository,
            IncidentRepository incidentRepository,
            BehaviorAssessmentRepository assessmentRepository,
            CaseRepository caseRepository,
            CitizenRepository citizenRepository,
            SkillRepository skillRepository,
            ProjectRepository projectRepository) {
        this.scale = scale;
        this.resourceRepository = resourceRepository;
        this.needRepository = needRepository;
        this.facilityRepository = facilityRepository;
        this.shipmentRepository = shipmentRepository;
        this.interactionRepository = interactionRepository;
        this.biosphereMetricRepository = biosphereMetricRepository;
        this.ruleRepository = ruleRepository;
        this.automationUnitRepository = automationUnitRepository;
        this.committeeRepository = committeeRepository;
        this.incidentRepository = incidentRepository;
        this.assessmentRepository = assessmentRepository;
        this.caseRepository = caseRepository;
        this.citizenRepository = citizenRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (resourceRepository.count() > 0) {
                log.info("Database already seeded — skipping. Scale was set to {}", scale);
                return;
            }
            log.info("Seeding database at {} scale...", scale);

            seedResources();
            seedNeeds();
            seedFacilities();
            seedShipments();
            seedInteractions();
            seedBiosphereMetrics();
            seedRules();
            seedAutomationUnits();
            seedCommittees();
            seedSocial();
            seedContribution();

            log.info("Seed complete at {} scale.", scale);
        };
    }

    private void seedResources() {
        switch (scale) {
            case LOCAL -> {
                Resource garden = resource("Local Community Garden", "FOOD",
                        "Small-scale permaculture garden supplying the local settlement.",
                        point(-23.55, -46.63), 2.0, "Tons/Month");
                Resource pond = resource("Rainwater Retention Pond", "WATER",
                        "Local rainwater catchment system providing potable water.",
                        point(-23.56, -46.64), 500.0, "Cubic Meters");
                resourceRepository.saveAll(List.of(garden, pond));
            }
            case REGIONAL -> {
                Resource iron = resource("Regional Iron Deposit", "MINERAL",
                        "Medium-grade iron ore deposit supporting regional construction.",
                        point(-20.0, -44.0), 50.0, "Million Tons");
                Resource solar = resource("Regional Solar Array", "ENERGY",
                        "Solar farm powering several communities in the region.",
                        point(-22.0, -47.0), 5.0, "GW");
                Resource farm = resource("Regional Agro-Corridor", "FOOD",
                        "Multi-community agricultural corridor with automated irrigation.",
                        point(-21.0, -45.0), 100.0, "Thousand Tons/Year");
                resourceRepository.saveAll(List.of(iron, solar, farm));
            }
            case CONTINENTAL -> {
                Resource carajas = resource("Carajás Iron Province", "MINERAL",
                        "Major iron ore deposits in Pará, Brazil, supplying continental industry.",
                        point(-6.06, -50.15), 18.0, "Billion Tons");
                Resource lithium = resource("Andean Lithium Triangle", "MINERAL",
                        "Lithium brine deposits spanning Chile, Bolivia, and Argentina.",
                        point(-23.5, -67.5), 9.0, "Million Tons");
                Resource solar = resource("Sahara Solar Belt", "ENERGY",
                        "High-intensity solar radiation zone across the Saharan corridor.",
                        point(23.5, 12.0), 500.0, "GWp");
                Resource wheat = resource("Continental Grain Belt", "FOOD",
                        "High-yield grain production zone feeding the continent.",
                        point(45.0, -100.0), 800.0, "Million Tons");
                Resource timber = resource("Boreal Timber Reserve", "MATERIAL",
                        "Sustainably managed boreal forest for construction materials.",
                        point(55.0, -95.0), 300.0, "Million Cubic Meters");
                resourceRepository.saveAll(List.of(carajas, lithium, solar, wheat, timber));
            }
            case GLOBAL -> {
                Resource carajas = resource("Carajás Iron Mine", "MINERAL",
                        "Largest iron ore mine in the world, located in Pará, Brazil.",
                        point(-6.06, -50.15), 18.0, "Billion Tons");
                Resource atacama = resource("Atacama Lithium Deposit", "MINERAL",
                        "Major lithium brine deposit in Chile, crucial for energy storage.",
                        point(-23.5, -68.25), 9.0, "Million Tons");
                Resource sahara = resource("Sahara Solar Potential Zone", "ENERGY",
                        "High-intensity solar radiation zone with massive energy capacity.",
                        point(23.5, 12.0), 1000.0, "GWp");
                Resource northSea = resource("North Sea Wind Hub", "ENERGY",
                        "Critical offshore wind potential for Northern Europe.",
                        point(55.0, 3.0), 50.0, "GW");
                Resource wheat = resource("Global Wheat Belt", "FOOD",
                        "High-yield grain production zone spanning multiple continents.",
                        point(45.0, -100.0), 800.0, "Million Tons");
                Resource housing = resource("Initial Sustainable Housing Stock", "HOUSING",
                        "Existing baseline of RBE-compliant housing worldwide.",
                        point(0.0, 0.0), 5.0, "Million Units");
                Resource amazon = resource("Amazon Freshwater Reserve", "WATER",
                        "World's largest freshwater basin supplying global needs.",
                        point(-3.0, -60.0), 6000.0, "Billion Cubic Meters");
                resourceRepository.saveAll(List.of(carajas, atacama, sahara, northSea, wheat, housing, amazon));
            }
        }
        log.info("  resources seeded");
    }

    private void seedNeeds() {
        switch (scale) {
            case LOCAL -> {
                need("HOUSING", "Local Settlement", "Basic sustainable housing for the local community.",
                        0.5, "Thousand Units", 5, "UNMET");
                need("FOOD", "Local Settlement", "Daily nutritional requirements for the local population.",
                        2.0, "Tons/Day", 5, "PARTIAL");
            }
            case REGIONAL -> {
                need("HOUSING", "Southeast Asia", "Unmet demand for sustainable housing in the region.",
                        5.0, "Million Units", 5, "UNMET");
                need("FOOD", "Sub-Saharan Africa", "Daily caloric deficit for child population.",
                        2.5, "Billion kcal/day", 5, "PARTIAL");
                need("ENERGY", "European Union", "Target for renewable energy transition in the region.",
                        300.0, "TWh/year", 4, "PARTIAL");
            }
            case CONTINENTAL -> {
                need("HOUSING", "Southeast Asia", "Unmet demand for sustainable, high-density housing units.",
                        15.0, "Million Units", 5, "UNMET");
                need("FOOD", "Sub-Saharan Africa", "Daily caloric target deficit for child population.",
                        2.5, "Billion kcal/day", 5, "PARTIAL");
                need("ENERGY", "European Union", "Target for 100% renewable energy transition.",
                        300.0, "TWh/year", 4, "PARTIAL");
                need("EDUCATION", "Global South", "Open access to advanced scientific and technical training.",
                        1.2, "Billion People", 4, "UNMET");
            }
            case GLOBAL -> {
                need("HOUSING", "Southeast Asia", "Unmet demand for sustainable, high-density housing units.",
                        15.0, "Million Units", 5, "UNMET");
                need("FOOD", "Sub-Saharan Africa", "Daily caloric target deficit for child population.",
                        2.5, "Billion kcal/day", 5, "PARTIAL");
                need("ENERGY", "European Union", "Target for 100% renewable energy transition.",
                        300.0, "TWh/year", 4, "PARTIAL");
                need("EDUCATION", "Global", "Open access to advanced scientific and technical training.",
                        1.2, "Billion People", 4, "UNMET");
                need("MINERAL", "Global", "Resource requirement for global structural transition.",
                        5.0, "Billion Tons", 3, "PARTIAL");
                need("HEALTH", "Global", "Universal preventive healthcare coverage target.",
                        8.0, "Billion People", 5, "UNMET");
            }
        }
        log.info("  needs seeded");
    }

    private void seedFacilities() {
        switch (scale) {
            case LOCAL -> {
                facility("Community Micro-Farm", "VERTICAL_FARM", "Local Settlement",
                        0.85, "ACTIVE", "500 kg/day");
            }
            case REGIONAL -> {
                facility("Regional Housing Hub", "HOUSING_3D", "Southeast Asia",
                        0.90, "ACTIVE", "200 units/month");
                facility("Regional Agro-Synthesis", "VERTICAL_FARM", "Sub-Saharan Africa",
                        0.88, "ACTIVE", "15,000 kg/day");
            }
            case CONTINENTAL -> {
                facility("Neo-Architectural Hub SEA-01", "HOUSING_3D", "Southeast Asia",
                        0.92, "ACTIVE", "450 units/month");
                facility("Agro-Synthesis Alpha", "VERTICAL_FARM", "Sub-Saharan Africa",
                        0.88, "ACTIVE", "15,000 kg/day");
                facility("Molecular Re-Integrator 01", "RECYCLING_HUB", "Global",
                        0.95, "ACTIVE", "1.2 tons/hour");
            }
            case GLOBAL -> {
                facility("Neo-Architectural Hub SEA-01", "HOUSING_3D", "Southeast Asia",
                        0.92, "ACTIVE", "450 units/month");
                facility("Agro-Synthesis Alpha", "VERTICAL_FARM", "Sub-Saharan Africa",
                        0.88, "ACTIVE", "15,000 kg/day");
                facility("Molecular Re-Integrator 01", "RECYCLING_HUB", "Global",
                        0.95, "ACTIVE", "1.2 tons/hour");
                facility("Europa Fusion Research Station", "ENERGY_HUB", "European Union",
                        0.97, "ACTIVE", "5 GW");
                facility("Nanofabrication Plant Americas", "MANUFACTURING", "South America",
                        0.93, "ACTIVE", "500 tons/month");
            }
        }
        log.info("  facilities seeded");
    }

    private void seedShipments() {
        switch (scale) {
            case LOCAL -> {
                shipment("Fresh Produce", "Community Farm", "Local Distribution Hub",
                        5.0, "Tons", "IN_TRANSIT", LocalDateTime.now().plusDays(1));
            }
            case REGIONAL -> {
                shipment("Lithium Carbonate", "Regional Mine", "Regional Battery Hub",
                        100.0, "Tons", "IN_TRANSIT", LocalDateTime.now().plusDays(3));
                shipment("Nutritional Supply", "Agro-Synthesis Hub", "Regional Distribution",
                        50.0, "Tons", "PENDING", LocalDateTime.now().plusDays(5));
            }
            case CONTINENTAL -> {
                shipment("Refined Iron Ore", "Carajás Mine", "Continental Construction Hub",
                        1200.0, "Tons", "IN_TRANSIT", LocalDateTime.now().plusDays(7));
                shipment("Bio-Nutritional Matrix", "Agro-Synthesis Alpha", "Continental Distribution",
                        500.0, "Tons", "IN_TRANSIT", LocalDateTime.now().plusDays(3));
                shipment("Solar Panels", "Solar Manufacturing Plant", "Continental Deployment",
                        10000.0, "Units", "PENDING", LocalDateTime.now().plusDays(14));
            }
            case GLOBAL -> {
                shipment("Lithium Carbonate", "Atacama Desert, Chile", "Global Battery Hub",
                        500.0, "Tons", "IN_TRANSIT", LocalDateTime.now().plusDays(5));
                shipment("Bio-Nutritional Matrix", "Agro-Synthesis Alpha, SSA", "Regional Distribution Center 04",
                        200.0, "Tons", "IN_TRANSIT", LocalDateTime.now().plusDays(2));
                shipment("Refined Iron Ore", "Carajás Mine, Brazil", "Automated Construction SEA-01",
                        1200.0, "Tons", "PENDING", LocalDateTime.now().plusDays(10));
                shipment("Microprocessor Units", "Global Fab Network", "Automation Hub EU",
                        50000.0, "Units", "IN_TRANSIT", LocalDateTime.now().plusDays(4));
            }
        }
        log.info("  shipments seeded");
    }

    private void seedInteractions() {
        switch (scale) {
            case LOCAL -> {
                interaction("NEED_REPORT", "Well pump needing maintenance in the central square.",
                        "Local Settlement", "CIT-LOCAL", "VERIFIED");
            }
            case REGIONAL -> {
                interaction("NEED_REPORT", "Local aquifer levels dropping significantly in the Central Plain region.",
                        "Central Plains", "CIT-9928", "VERIFIED");
                interaction("INNOVATION", "Proposed upgrade to irrigation efficiency using soil sensors.",
                        "Region", "CIT-4412", "INTEGRATED");
            }
            case CONTINENTAL -> {
                interaction("NEED_REPORT", "Aquifer levels dropping — requesting continental hydro-desalination assessment.",
                        "Central Plains", "CIT-9928", "VERIFIED");
                interaction("INNOVATION", "Proposed upgrade to 3D-Housing extrusion head for faster curing.",
                        "Continental", "CIT-4412", "INTEGRATED");
                interaction("COLLABORATION", "Registered for experimental thorium reactor simulation.",
                        "Continental", "CIT-1055", "PENDING");
            }
            case GLOBAL -> {
                interaction("NEED_REPORT", "Local aquifer levels dropping significantly in the Central Plain region. Requesting hydro-desalination assessment.",
                        "Central Plains", "CIT-9928", "VERIFIED");
                interaction("INNOVATION", "Proposed upgrade to 3D-Housing extrusion head for 15% faster curing using carbon-fiber composite.",
                        "Global", "CIT-4412", "INTEGRATED");
                interaction("COLLABORATION", "Registered for experimental thorium reactor maintenance simulation in Northern Europe sector.",
                        "EU-North", "CIT-1055", "PENDING");
                interaction("INNOVATION", "Open-source AI diagnostic tool for predictive biosphere monitoring.",
                        "Global", "CIT-7763", "VERIFIED");
            }
        }
        log.info("  interactions seeded");
    }

    private void seedBiosphereMetrics() {
        switch (scale) {
            case LOCAL -> {
                biosphereMetric("Local Air Quality Index", 42.0, "AQI", 50.0, "NORMAL", 1.2);
                biosphereMetric("Local Stream pH Level", 7.2, "pH", 6.5, "NORMAL", 0.1);
            }
            case REGIONAL -> {
                biosphereMetric("Regional CO2 Average", 410.0, "ppm", 350.0, "WARNING", 2.0);
                biosphereMetric("Regional Temperature Deviation", 1.0, "°C", 1.5, "NORMAL", 0.02);
                biosphereMetric("Regional Forest Cover Change", -0.5, "%/Year", 0.0, "WARNING", -0.1);
            }
            case CONTINENTAL -> {
                biosphereMetric("Continental CO2 Concentration", 415.0, "ppm", 350.0, "CRITICAL", 2.4);
                biosphereMetric("Continental Surface Temp Deviation", 1.1, "°C", 1.5, "WARNING", 0.02);
                biosphereMetric("Continental Reforestation Rate", 4.2, "Million Hectares/Year", 10.0, "NORMAL", 0.5);
                biosphereMetric("Continental Ocean Acidity", 8.06, "pH", 8.1, "WARNING", -0.002);
            }
            case GLOBAL -> {
                biosphereMetric("Atmospheric CO2 Concentration", 419.5, "ppm", 350.0, "CRITICAL", 2.4);
                biosphereMetric("Global Surface Temp Deviation", 1.15, "°C", 1.5, "WARNING", 0.02);
                biosphereMetric("Ocean Surface Acidity", 8.06, "pH", 8.1, "WARNING", -0.002);
                biosphereMetric("Global Reforestation Rate", 4.2, "Million Hectares/Year", 10.0, "NORMAL", 0.5);
                biosphereMetric("Arctic Sea Ice Extent", 4.5, "Million km²", 5.0, "CRITICAL", -0.8);
            }
        }
        log.info("  biosphere metrics seeded");
    }

    private void seedRules() {
        switch (scale) {
            case LOCAL -> {
                rule("Community Water Stewardship",
                        "All households must maintain rainwater catchment systems.",
                        "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"WATER\", \"action\": \"RESTRICT_USAGE\"}",
                        "ACTIVE", "SCIENTIFICALLY_VALIDATED", "Local Council", 150);
            }
            case REGIONAL -> {
                rule("Regional Biosphere Stability",
                        "Industrial production must cease if biodiversity indices drop below thresholds.",
                        "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"BIOSPHERE_HEALTH\", \"action\": \"SUSPEND_PRODUCTION\"}",
                        "ACTIVE", "SCIENTIFICALLY_VALIDATED", "Regional Commission", 2500);
            }
            case CONTINENTAL -> {
                rule("Continental Biosphere Stability Clause",
                        "All industrial production must cease in a region if local biodiversity indices drop below therapeutic thresholds.",
                        "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"BIOSPHERE_HEALTH\", \"action\": \"SUSPEND_PRODUCTION\"}",
                        "ACTIVE", "SCIENTIFICALLY_VALIDATED", "Global Biosphere Commission", 12500);
                rule("Continental Caloric Security",
                        "Strategic reserves must maintain a 6-month buffer of essential nutrients before export is authorized.",
                        "{\"type\": \"RESERVE_CHECK\", \"metric\": \"FOOD\", \"min_buffer_months\": 6}",
                        "ACTIVE", "SCIENTIFICALLY_VALIDATED", "Energy & Nutrition Council", 8900);
            }
            case GLOBAL -> {
                rule("Biosphere Stability Clause",
                        "All industrial production must cease in a region if local biodiversity indices drop below therapeutic thresholds.",
                        "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"BIOSPHERE_HEALTH\", \"action\": \"SUSPEND_PRODUCTION\"}",
                        "ACTIVE", "SCIENTIFICALLY_VALIDATED", "Global Biosphere Commission", 12500);
                rule("Universal Caloric Security",
                        "Strategic reserves must maintain a 6-month buffer of essential nutrients before export is authorized.",
                        "{\"type\": \"RESERVE_CHECK\", \"metric\": \"FOOD\", \"min_buffer_months\": 6}",
                        "ACTIVE", "SCIENTIFICALLY_VALIDATED", "Energy & Nutrition Council", 8900);
                rule("Global Water Equity",
                        "Transboundary water basins must be managed under shared scientific governance.",
                        "{\"type\": \"RESERVE_CHECK\", \"metric\": \"WATER\", \"min_buffer_months\": 3}",
                        "ACTIVE", "SCIENTIFICALLY_VALIDATED", "Global Water Authority", 15000);
            }
        }
        log.info("  rules seeded");
    }

    private void seedAutomationUnits() {
        switch (scale) {
            case LOCAL -> {
                automationUnit("Community Maint-Bot", "BOT", "Local Settlement", "ACTIVE", "STANDBY");
            }
            case REGIONAL -> {
                automationUnit("Constructor Beta-1", "CONSTRUCTOR", "Southeast Asia", "ACTIVE", "ASSEMBLING_MODULAR_HOUSING");
                automationUnit("Agro-Drone Swarm Alpha", "DRONE", "Sub-Saharan Africa", "ACTIVE", "MONITORING_CROP_MATURITY");
            }
            case CONTINENTAL -> {
                automationUnit("Constructor Alpha-1", "CONSTRUCTOR", "Southeast Asia", "ACTIVE", "ASSEMBLING_MODULAR_HOUSING");
                automationUnit("Agro-Drone Swarm Beta", "DRONE", "Sub-Saharan Africa", "ACTIVE", "MONITORING_CROP_MATURITY");
                automationUnit("Maint-Bot Gamma-4", "BOT", "European Union", "IDLE", "STANDBY");
            }
            case GLOBAL -> {
                automationUnit("Constructor Alpha-1", "CONSTRUCTOR", "Southeast Asia", "ACTIVE", "ASSEMBLING_MODULAR_HOUSING");
                automationUnit("Agro-Drone Swarm Beta", "DRONE", "Sub-Saharan Africa", "ACTIVE", "MONITORING_CROP_MATURITY");
                automationUnit("Maint-Bot Gamma-4", "BOT", "European Union", "IDLE", "STANDBY");
                automationUnit("Deep Sea Probe Delta", "DRONE", "Pacific Ocean", "ACTIVE", "BIOSPHERE_MONITORING");
                automationUnit("Constructor Epsilon-7", "CONSTRUCTOR", "South America", "ACTIVE", "INFRASTRUCTURE_UPGRADE");
            }
        }
        log.info("  automation units seeded");
    }

    private void seedCommittees() {
        switch (scale) {
            case LOCAL -> {
                committee("LOCAL", "Local Community Council",
                        "Overseeing local resource allocation and community well-being.", "COMMUNITY_VALIDATED");
            }
            case REGIONAL -> {
                committee("BIOSPHERE", "Regional Biosphere Commission",
                        "Auditing regional environmental indices.", "PEER_REVIEWED");
                committee("ENERGY", "Regional Energy Council",
                        "Optimizing regional energy distribution.", "PEER_REVIEWED");
            }
            case CONTINENTAL -> {
                committee("BIOSPHERE", "Continental Biosphere Commission",
                        "Auditing continental planetary boundaries and biodiversity indices.", "EMPIRICAL_VALIDATED");
                committee("ENERGY", "Continental Energy & Nutrition Council",
                        "Optimizing thermodynamic efficiency in food and power systems.", "PEER_REVIEWED");
            }
            case GLOBAL -> {
                committee("BIOSPHERE", "Global Biosphere Commission",
                        "Auditing planetary boundaries and biodiversity indices.", "EMPIRICAL_VALIDATED");
                committee("ENERGY", "Energy & Nutrition Council",
                        "Optimizing thermodynamic efficiency in food and power systems.", "PEER_REVIEWED");
                committee("SOCIAL", "Global Social Stability Board",
                        "Monitoring and mediating social stability across all regions.", "EMPIRICAL_VALIDATED");
            }
        }
        log.info("  committees seeded");
    }

    private void seedSocial() {
        switch (scale) {
            case LOCAL -> {
                Incident dispute = incident("CONFLICT", "Local Settlement",
                        "Minor resource allocation dispute in community garden.", "LOW", "ANALYZING",
                        List.of("CIT-LOCAL-01", "CIT-LOCAL-02"));
                incidentRepository.save(dispute);
            }
            case REGIONAL -> {
                Incident dispute = incident("CONFLICT", "Sector 7 Community Garden",
                        "Resource allocation dispute regarding irrigation timing.", "LOW", "ANALYZING",
                        List.of("CIT-8821", "CIT-3310"));
                Incident anomaly = incident("BEHAVIORAL_ANOMALY", "Urban Transit Node 04",
                        "Citizen showing signs of extreme stress and erratic behavior.", "MEDIUM", "REPORTED",
                        List.of("CIT-1055"));
                incidentRepository.saveAll(List.of(dispute, anomaly));

                Case c = new Case();
                c.setSourceIncident(anomaly);
                c.setStatus("REHABILITATION");
                c.setResolutionPlan("Relocation to low-density green zone and assignment of a behavioral mediator.");
                c.setRehabilitationProgram("Cognitive-behavioral support and social integration workshop.");
                c.setMonitoringPlan("Biometric stress monitoring for 3 months.");
                c.setPanelExpertIds(List.of("EXP-PSY-01", "EXP-SOC-04"));
                caseRepository.save(c);
            }
            case CONTINENTAL -> {
                Incident dispute = incident("CONFLICT", "Sector 7 Community Garden",
                        "Resource allocation dispute regarding irrigation timing.", "LOW", "ANALYZING",
                        List.of("CIT-8821", "CIT-3310"));
                Incident anomaly = incident("BEHAVIORAL_ANOMALY", "Urban Transit Node 04",
                        "Citizen showing signs of extreme stress and erratic behavior.", "MEDIUM", "REPORTED",
                        List.of("CIT-1055"));
                incidentRepository.saveAll(List.of(dispute, anomaly));

                BehaviorAssessment assessment = new BehaviorAssessment();
                assessment.setCitizenId("CIT-1055");
                assessment.setPsychologicalProfile("High stress levels detected via biometrics. History of displacement trauma.");
                assessment.setRiskScore(0.45);
                assessment.setSocialFactors("Recent relocation to high-density zone; lack of familiar social cues.");
                assessmentRepository.save(assessment);

                Case c = new Case();
                c.setSourceIncident(anomaly);
                c.setStatus("REHABILITATION");
                c.setResolutionPlan("Relocation to low-density green zone and assignment of a behavioral mediator.");
                c.setRehabilitationProgram("Cognitive-behavioral support and social integration workshop.");
                c.setMonitoringPlan("Biometric stress monitoring for 3 months.");
                c.setPanelExpertIds(List.of("EXP-PSY-01", "EXP-SOC-04"));
                caseRepository.save(c);
            }
            case GLOBAL -> {
                Incident dispute = incident("CONFLICT", "Sector 7 Community Garden",
                        "Resource allocation dispute regarding irrigation timing.", "LOW", "ANALYZING",
                        List.of("CIT-8821", "CIT-3310"));
                Incident anomaly = incident("BEHAVIORAL_ANOMALY", "Urban Transit Node 04",
                        "Citizen showing signs of extreme stress and erratic behavior.", "MEDIUM", "REPORTED",
                        List.of("CIT-1055"));
                Incident massEvent = incident("OTHER", "Global Forum Online",
                        "Large-scale coordinated proposal for constitutional amendment on water rights.", "LOW", "ANALYZING",
                        List.of("CIT-0001", "CIT-4412", "CIT-7763"));
                incidentRepository.saveAll(List.of(dispute, anomaly, massEvent));

                BehaviorAssessment assessment = new BehaviorAssessment();
                assessment.setCitizenId("CIT-1055");
                assessment.setPsychologicalProfile("High stress levels detected via biometrics. History of displacement trauma.");
                assessment.setRiskScore(0.45);
                assessment.setSocialFactors("Recent relocation to high-density zone; lack of familiar social cues.");
                assessmentRepository.save(assessment);

                Case c = new Case();
                c.setSourceIncident(anomaly);
                c.setStatus("REHABILITATION");
                c.setResolutionPlan("Relocation to low-density green zone and assignment of a behavioral mediator.");
                c.setRehabilitationProgram("Cognitive-behavioral support and social integration workshop.");
                c.setMonitoringPlan("Biometric stress monitoring for 3 months.");
                c.setPanelExpertIds(List.of("EXP-PSY-01", "EXP-SOC-04"));
                caseRepository.save(c);
            }
        }
        log.info("  social seeded");
    }

    private void seedContribution() {
        switch (scale) {
            case LOCAL -> {
                Skill farming = skill("Sustainable Agriculture", "AGRICULTURE", "Local food production techniques.");
                skillRepository.save(farming);

                Citizen local = citizen("CIT-LOCAL-01", "Local Pioneer", List.of(farming),
                        List.of("Community Building", "Permaculture"), 50.0, "Founding member of the local settlement.");
                citizenRepository.save(local);

                Project garden = project("Community Permaculture Project", "Establishing a self-sustaining food forest.",
                        "AGRICULTURE", "FOOD_SECURITY", List.of("Sustainable Agriculture"), "ACTIVE");
                projectRepository.save(garden);
            }
            case REGIONAL -> {
                Skill engineering = skill("Engineering", "ENGINEERING", "Sustainable systems design.");
                Skill science = skill("Science", "SCIENCE", "Empirical research and validation.");
                skillRepository.saveAll(List.of(engineering, science));

                Citizen jackson = citizen("CIT-0001", "Jackson Wendel", List.of(engineering, science),
                        List.of("Automation", "Sustainability"), 120.0, "Regional architect of sustainable systems.");
                Citizen maria = citizen("CIT-0002", "Maria Chen", List.of(science),
                        List.of("Biosphere", "Data Analysis"), 90.0, "Environmental data scientist.");
                citizenRepository.saveAll(List.of(jackson, maria));

                Project reforestation = project("Regional Reforestation Initiative", "Automated reforestation using seed-planting drones.",
                        "ENVIRONMENTAL", "REFORESTATION", List.of("Engineering", "Science"), "ACTIVE");
                projectRepository.save(reforestation);
            }
            case CONTINENTAL -> {
                Skill engineering = skill("Engineering", "ENGINEERING", "Sustainable systems design.");
                Skill science = skill("Science", "SCIENCE", "Empirical research and validation.");
                Skill education = skill("Education", "EDUCATION", "Knowledge transmission.");
                skillRepository.saveAll(List.of(engineering, science, education));

                Citizen jackson = citizen("CIT-0001", "Jackson Wendel", List.of(engineering, science),
                        List.of("Automation", "Sustainability", "DDD"), 150.0, "Lead architect of the Civilization Operating System.");
                Citizen maria = citizen("CIT-0002", "Maria Chen", List.of(science, education),
                        List.of("Biosphere", "Data Analysis", "Teaching"), 120.0, "Environmental data scientist and educator.");
                Citizen amara = citizen("CIT-0003", "Amara Osei", List.of(engineering),
                        List.of("Renewable Energy", "Infrastructure"), 110.0, "Solar infrastructure specialist.");
                citizenRepository.saveAll(List.of(jackson, maria, amara));

                Project reforestation = project("Amazon Restoration Project", "Automated reforestation using seed-planting drones and bio-monitoring.",
                        "ENVIRONMENTAL", "REFORESTATION", List.of("Engineering", "Science"), "ACTIVE");
                Project solarGrid = project("Continental Solar Grid Expansion", "Connecting continental solar farms via smart grid.",
                        "ENERGY", "INFRASTRUCTURE", List.of("Engineering"), "ACTIVE");
                projectRepository.saveAll(List.of(reforestation, solarGrid));
            }
            case GLOBAL -> {
                Skill engineering = skill("Engineering", "ENGINEERING", "Sustainable systems design.");
                Skill science = skill("Science", "SCIENCE", "Empirical research and validation.");
                Skill education = skill("Education", "EDUCATION", "Knowledge transmission.");
                Skill medicine = skill("Medicine", "HEALTH", "Preventive and regenerative healthcare.");
                skillRepository.saveAll(List.of(engineering, science, education, medicine));

                Citizen jackson = citizen("CIT-0001", "Jackson Wendel", List.of(engineering, science),
                        List.of("Automation", "Sustainability", "DDD"), 150.0, "Lead architect of the Civilization Operating System.");
                Citizen maria = citizen("CIT-0002", "Maria Chen", List.of(science, education),
                        List.of("Biosphere", "Data Analysis", "Teaching"), 130.0, "Environmental data scientist and educator.");
                Citizen amara = citizen("CIT-0003", "Amara Osei", List.of(engineering),
                        List.of("Renewable Energy", "Infrastructure"), 115.0, "Solar infrastructure specialist.");
                Citizen nobel = citizen("CIT-0004", "Nobel Kim", List.of(medicine, science),
                        List.of("Regenerative Medicine", "Public Health"), 140.0, "Lead researcher in regenerative health protocols.");
                citizenRepository.saveAll(List.of(jackson, maria, amara, nobel));

                Project reforestation = project("Amazon Restoration Project", "Automated reforestation using seed-planting drones and bio-monitoring.",
                        "ENVIRONMENTAL", "REFORESTATION", List.of("Engineering", "Science"), "ACTIVE");
                Project globalHealth = project("Global Preventive Health Initiative", "Deploying AI-driven diagnostic networks worldwide.",
                        "HEALTH", "PUBLIC_HEALTH", List.of("Medicine", "Science"), "ACTIVE");
                Project educationPlatform = project("Open Knowledge Platform", "Universal access to advanced scientific and technical education.",
                        "EDUCATION", "KNOWLEDGE_SHARING", List.of("Education", "Science"), "PROPOSED");
                projectRepository.saveAll(List.of(reforestation, globalHealth, educationPlatform));
            }
        }
        log.info("  contribution seeded");
    }

    private Resource resource(String name, String type, String description, Point location,
                              double quantity, String unit) {
        Resource r = new Resource();
        r.setName(name);
        r.setType(type);
        r.setDescription(description);
        r.setLocation(location);
        r.setQuantity(quantity);
        r.setUnit(unit);
        return r;
    }

    private Need need(String category, String region, String description, double quantity,
                      String unit, int priority, String status) {
        Need n = new Need();
        n.setCategory(category);
        n.setRegion(region);
        n.setDescription(description);
        n.setQuantity(quantity);
        n.setUnit(unit);
        n.setPriority(priority);
        n.setStatus(status);
        return n;
    }

    private Facility facility(String name, String type, String region, double efficiency,
                              String status, String currentOutput) {
        Facility f = new Facility();
        f.setName(name);
        f.setType(type);
        f.setRegion(region);
        f.setEfficiency(efficiency);
        f.setStatus(status);
        f.setCurrentOutput(currentOutput);
        return f;
    }

    private Shipment shipment(String cargo, String origin, String destination, double quantity,
                              String unit, String status, LocalDateTime eta) {
        Shipment s = new Shipment();
        s.setCargo(cargo);
        s.setOrigin(origin);
        s.setDestination(destination);
        s.setQuantity(quantity);
        s.setUnit(unit);
        s.setStatus(status);
        s.setEta(eta);
        return s;
    }

    private Interaction interaction(String type, String content, String region,
                                    String citizenId, String status) {
        Interaction i = new Interaction();
        i.setType(type);
        i.setContent(content);
        i.setRegion(region);
        i.setCitizenId(citizenId);
        i.setStatus(status);
        return i;
    }

    private BiosphereMetric biosphereMetric(String name, double value, String unit,
                                            double safetyLimit, String status, double drift) {
        BiosphereMetric m = new BiosphereMetric();
        m.setName(name);
        m.setValue(value);
        m.setUnit(unit);
        m.setSafetyLimit(safetyLimit);
        m.setStatus(status);
        m.setDrift(drift);
        return m;
    }

    private Rule rule(String title, String description, String logicCode, String status,
                      String validationStatus, String validatedBy, int votesCount) {
        Rule r = new Rule();
        r.setTitle(title);
        r.setDescription(description);
        r.setLogicCode(logicCode);
        r.setStatus(status);
        r.setValidationStatus(validationStatus);
        r.setValidatedBy(validatedBy);
        r.setVotesCount(votesCount);
        return r;
    }

    private AutomationUnit automationUnit(String name, String type, String region,
                                          String status, String currentTask) {
        AutomationUnit u = new AutomationUnit();
        u.setName(name);
        u.setType(type);
        u.setRegion(region);
        u.setStatus(status);
        u.setCurrentTask(currentTask);
        return u;
    }

    private ScientificCommittee committee(String area, String name, String mandate, String validationLevel) {
        ScientificCommittee c = new ScientificCommittee();
        c.setArea(area);
        c.setName(name);
        c.setMandate(mandate);
        c.setValidationLevel(validationLevel);
        return c;
    }

    private Incident incident(String type, String location, String description, String riskLevel,
                              String status, List<String> participantIds) {
        Incident i = new Incident();
        i.setType(type);
        i.setLocation(location);
        i.setDescription(description);
        i.setRiskLevel(riskLevel);
        i.setStatus(status);
        i.setParticipantIds(participantIds);
        return i;
    }

    private Skill skill(String name, String category, String description) {
        Skill s = new Skill();
        s.setName(name);
        s.setCategory(category);
        s.setDescription(description);
        return s;
    }

    private Citizen citizen(String citizenId, String name, List<Skill> skills,
                            List<String> interests, double reputationScore, String biography) {
        Citizen c = new Citizen();
        c.setCitizenId(citizenId);
        c.setName(name);
        c.setSkills(skills);
        c.setInterests(interests);
        c.setReputationScore(reputationScore);
        c.setBiographicalNote(biography);
        return c;
    }

    private Project project(String title, String description, String category, String impactArea,
                            List<String> requiredSkills, String status) {
        Project p = new Project();
        p.setTitle(title);
        p.setDescription(description);
        p.setCategory(category);
        p.setImpactArea(impactArea);
        p.setRequiredSkillNames(requiredSkills);
        p.setStatus(status);
        return p;
    }

    private Point point(double lat, double lon) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}
