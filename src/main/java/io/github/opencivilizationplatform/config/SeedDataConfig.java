package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import io.github.opencivilizationplatform.modules.contribution.domain.ImpactArea;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectCategory;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectStatus;
import io.github.opencivilizationplatform.modules.contribution.domain.SkillCategory;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnitStatus;
import io.github.opencivilizationplatform.modules.execution.infrastructure.AutomationUnitRepository;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnitType;
import io.github.opencivilizationplatform.modules.governance.domain.CommitteeArea;
import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import io.github.opencivilizationplatform.modules.governance.domain.ValidationLevel;
import io.github.opencivilizationplatform.modules.governance.infrastructure.ScientificCommitteeRepository;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.github.opencivilizationplatform.modules.logistics.domain.ShipmentStatus;
import io.github.opencivilizationplatform.modules.logistics.infrastructure.ShipmentRepository;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetricStatus;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.github.opencivilizationplatform.modules.needs.domain.NeedCategory;
import io.github.opencivilizationplatform.modules.needs.domain.NeedStatus;
import io.github.opencivilizationplatform.modules.needs.infrastructure.NeedRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import io.github.opencivilizationplatform.modules.participation.domain.InteractionStatus;
import io.github.opencivilizationplatform.modules.participation.domain.InteractionType;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.InteractionRepository;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.production.domain.Facility;
import io.github.opencivilizationplatform.modules.production.domain.FacilityStatus;
import io.github.opencivilizationplatform.modules.production.domain.FacilityType;
import io.github.opencivilizationplatform.modules.production.infrastructure.FacilityRepository;
import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.domain.ResourceType;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.CaseStatus;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.domain.IncidentStatus;
import io.github.opencivilizationplatform.modules.social.domain.IncidentType;
import io.github.opencivilizationplatform.modules.social.domain.RiskLevel;
import io.github.opencivilizationplatform.modules.social.infrastructure.BehaviorAssessmentRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.CaseRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
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
    private final ResourceRegionRepository resourceRegionRepository;
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
            ProjectRepository projectRepository,
            ResourceRegionRepository resourceRegionRepository) {
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
        this.resourceRegionRepository = resourceRegionRepository;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            try {
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
                seedResourceRegions();

                log.info("Seed complete at {} scale.", scale);
            } catch (Exception e) {
                log.warn("Database seeding interrupted or already completed by another instance: {}", e.getMessage());
            }
        };
    }

    private void seedResources() {
        switch (scale) {
            case LOCAL -> {
                Resource garden = resource("Local Community Garden", ResourceType.FOOD,
                        "Small-scale permaculture garden supplying the local settlement.",
                        point(-23.55, -46.63), 2.0, "Tons/Month");
                Resource pond = resource("Rainwater Retention Pond", ResourceType.WATER,
                        "Local rainwater catchment system providing potable water.",
                        point(-23.56, -46.64), 500.0, "Cubic Meters");
                resourceRepository.saveAll(List.of(garden, pond));
            }
            case REGIONAL -> {
                Resource iron = resource("Regional Iron Deposit", ResourceType.MINERAL,
                        "Medium-grade iron ore deposit supporting regional construction.",
                        point(-20.0, -44.0), 50.0, "Million Tons");
                Resource solar = resource("Regional Solar Array", ResourceType.ENERGY,
                        "Solar farm powering several communities in the region.",
                        point(-22.0, -47.0), 5.0, "GW");
                Resource farm = resource("Regional Agro-Corridor", ResourceType.FOOD,
                        "Multi-community agricultural corridor with automated irrigation.",
                        point(-21.0, -45.0), 100.0, "Thousand Tons/Year");
                resourceRepository.saveAll(List.of(iron, solar, farm));
            }
            case CONTINENTAL -> {
                Resource carajas = resource("Carajás Iron Province", ResourceType.MINERAL,
                        "Major iron ore deposits in Pará, Brazil, supplying continental industry.",
                        point(-6.06, -50.15), 18.0, "Billion Tons");
                Resource lithium = resource("Andean Lithium Triangle", ResourceType.MINERAL,
                        "Lithium brine deposits spanning Chile, Bolivia, and Argentina.",
                        point(-23.5, -67.5), 9.0, "Million Tons");
                Resource solar = resource("Sahara Solar Belt", ResourceType.ENERGY,
                        "High-intensity solar radiation zone across the Saharan corridor.",
                        point(23.5, 12.0), 500.0, "GWp");
                Resource wheat = resource("Continental Grain Belt", ResourceType.FOOD,
                        "High-yield grain production zone feeding the continent.",
                        point(45.0, -100.0), 800.0, "Million Tons");
                Resource timber = resource("Boreal Timber Reserve", ResourceType.MATERIAL,
                        "Sustainably managed boreal forest for construction materials.",
                        point(55.0, -95.0), 300.0, "Million Cubic Meters");
                resourceRepository.saveAll(List.of(carajas, lithium, solar, wheat, timber));
            }
            case GLOBAL -> {
                Resource carajas = resource("Carajás Iron Mine", ResourceType.MINERAL,
                        "Largest iron ore mine in the world, located in Pará, Brazil.",
                        point(-6.06, -50.15), 18.0, "Billion Tons");
                Resource atacama = resource("Atacama Lithium Deposit", ResourceType.MINERAL,
                        "Major lithium brine deposit in Chile, crucial for energy storage.",
                        point(-23.5, -68.25), 9.0, "Million Tons");
                Resource sahara = resource("Sahara Solar Potential Zone", ResourceType.ENERGY,
                        "High-intensity solar radiation zone with massive energy capacity.",
                        point(23.5, 12.0), 1000.0, "GWp");
                Resource northSea = resource("North Sea Wind Hub", ResourceType.ENERGY,
                        "Critical offshore wind potential for Northern Europe.",
                        point(55.0, 3.0), 50.0, "GW");
                Resource wheat = resource("Global Wheat Belt", ResourceType.FOOD,
                        "High-yield grain production zone spanning multiple continents.",
                        point(45.0, -100.0), 800.0, "Million Tons");
                Resource housing = resource("Initial Sustainable Housing Stock", ResourceType.HOUSING,
                        "Existing baseline of RBE-compliant housing worldwide.",
                        point(0.0, 0.0), 5.0, "Million Units");
                Resource amazon = resource("Amazon Freshwater Reserve", ResourceType.WATER,
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
                need(NeedCategory.HOUSING, "Local Settlement", "Basic sustainable housing for the local community.",
                        0.5, "Thousand Units", 5, NeedStatus.UNMET);
                need(NeedCategory.FOOD, "Local Settlement", "Daily nutritional requirements for the local population.",
                        2.0, "Tons/Day", 5, NeedStatus.PARTIAL);
            }
            case REGIONAL -> {
                need(NeedCategory.HOUSING, "Southeast Asia", "Unmet demand for sustainable housing in the region.",
                        5.0, "Million Units", 5, NeedStatus.UNMET);
                need(NeedCategory.FOOD, "Sub-Saharan Africa", "Daily caloric deficit for child population.",
                        2.5, "Billion kcal/day", 5, NeedStatus.PARTIAL);
                need(NeedCategory.ENERGY, "European Union", "Target for renewable energy transition in the region.",
                        300.0, "TWh/year", 4, NeedStatus.PARTIAL);
            }
            case CONTINENTAL -> {
                need(NeedCategory.HOUSING, "Southeast Asia", "Unmet demand for sustainable, high-density housing units.",
                        15.0, "Million Units", 5, NeedStatus.UNMET);
                need(NeedCategory.FOOD, "Sub-Saharan Africa", "Daily caloric target deficit for child population.",
                        2.5, "Billion kcal/day", 5, NeedStatus.PARTIAL);
                need(NeedCategory.ENERGY, "European Union", "Target for 100% renewable energy transition.",
                        300.0, "TWh/year", 4, NeedStatus.PARTIAL);
                need(NeedCategory.EDUCATION, "Global South", "Open access to advanced scientific and technical training.",
                        1.2, "Billion People", 4, NeedStatus.UNMET);
            }
            case GLOBAL -> {
                need(NeedCategory.HOUSING, "Southeast Asia", "Unmet demand for sustainable, high-density housing units.",
                        15.0, "Million Units", 5, NeedStatus.UNMET);
                need(NeedCategory.FOOD, "Sub-Saharan Africa", "Daily caloric target deficit for child population.",
                        2.5, "Billion kcal/day", 5, NeedStatus.PARTIAL);
                need(NeedCategory.ENERGY, "European Union", "Target for 100% renewable energy transition.",
                        300.0, "TWh/year", 4, NeedStatus.PARTIAL);
                need(NeedCategory.EDUCATION, "Global", "Open access to advanced scientific and technical training.",
                        1.2, "Billion People", 4, NeedStatus.UNMET);
                need(NeedCategory.MINERAL, "Global", "Resource requirement for global structural transition.",
                        5.0, "Billion Tons", 3, NeedStatus.PARTIAL);
                need(NeedCategory.HEALTH, "Global", "Universal preventive healthcare coverage target.",
                        8.0, "Billion People", 5, NeedStatus.UNMET);
            }
        }
        log.info("  needs seeded");
    }

    private void seedFacilities() {
        switch (scale) {
            case LOCAL -> {
                facility("Community Micro-Farm", FacilityType.VERTICAL_FARM, "Local Settlement",
                        0.85, FacilityStatus.ACTIVE, "500 kg/day");
            }
            case REGIONAL -> {
                facility("Regional Housing Hub", FacilityType.HOUSING_3D, "Southeast Asia",
                        0.90, FacilityStatus.ACTIVE, "200 units/month");
                facility("Regional Agro-Synthesis", FacilityType.VERTICAL_FARM, "Sub-Saharan Africa",
                        0.88, FacilityStatus.ACTIVE, "15,000 kg/day");
            }
            case CONTINENTAL -> {
                facility("Neo-Architectural Hub SEA-01", FacilityType.HOUSING_3D, "Southeast Asia",
                        0.92, FacilityStatus.ACTIVE, "450 units/month");
                facility("Agro-Synthesis Alpha", FacilityType.VERTICAL_FARM, "Sub-Saharan Africa",
                        0.88, FacilityStatus.ACTIVE, "15,000 kg/day");
                facility("Molecular Re-Integrator 01", FacilityType.RECYCLING_HUB, "Global",
                        0.95, FacilityStatus.ACTIVE, "1.2 tons/hour");
            }
            case GLOBAL -> {
                facility("Neo-Architectural Hub SEA-01", FacilityType.HOUSING_3D, "Southeast Asia",
                        0.92, FacilityStatus.ACTIVE, "450 units/month");
                facility("Agro-Synthesis Alpha", FacilityType.VERTICAL_FARM, "Sub-Saharan Africa",
                        0.88, FacilityStatus.ACTIVE, "15,000 kg/day");
                facility("Molecular Re-Integrator 01", FacilityType.RECYCLING_HUB, "Global",
                        0.95, FacilityStatus.ACTIVE, "1.2 tons/hour");
                facility("Europa Fusion Research Station", FacilityType.ENERGY_HUB, "European Union",
                        0.97, FacilityStatus.ACTIVE, "5 GW");
                facility("Nanofabrication Plant Americas", FacilityType.MANUFACTURING, "South America",
                        0.93, FacilityStatus.ACTIVE, "500 tons/month");
            }
        }
        log.info("  facilities seeded");
    }

    private void seedShipments() {
        switch (scale) {
            case LOCAL -> {
                shipment("Fresh Produce", "Community Farm", "Local Distribution Hub",
                        5.0, "Tons", ShipmentStatus.IN_TRANSIT, LocalDateTime.now().plusDays(1));
            }
            case REGIONAL -> {
                shipment("Lithium Carbonate", "Regional Mine", "Regional Battery Hub",
                        100.0, "Tons", ShipmentStatus.IN_TRANSIT, LocalDateTime.now().plusDays(3));
                shipment("Nutritional Supply", "Agro-Synthesis Hub", "Regional Distribution",
                        50.0, "Tons", ShipmentStatus.PENDING, LocalDateTime.now().plusDays(5));
            }
            case CONTINENTAL -> {
                shipment("Refined Iron Ore", "Carajás Mine", "Continental Construction Hub",
                        1200.0, "Tons", ShipmentStatus.IN_TRANSIT, LocalDateTime.now().plusDays(7));
                shipment("Bio-Nutritional Matrix", "Agro-Synthesis Alpha", "Continental Distribution",
                        500.0, "Tons", ShipmentStatus.IN_TRANSIT, LocalDateTime.now().plusDays(3));
                shipment("Solar Panels", "Solar Manufacturing Plant", "Continental Deployment",
                        10000.0, "Units", ShipmentStatus.PENDING, LocalDateTime.now().plusDays(14));
            }
            case GLOBAL -> {
                shipment("Lithium Carbonate", "Atacama Desert, Chile", "Global Battery Hub",
                        500.0, "Tons", ShipmentStatus.IN_TRANSIT, LocalDateTime.now().plusDays(5));
                shipment("Bio-Nutritional Matrix", "Agro-Synthesis Alpha, SSA", "Regional Distribution Center 04",
                        200.0, "Tons", ShipmentStatus.IN_TRANSIT, LocalDateTime.now().plusDays(2));
                shipment("Refined Iron Ore", "Carajás Mine, Brazil", "Automated Construction SEA-01",
                        1200.0, "Tons", ShipmentStatus.PENDING, LocalDateTime.now().plusDays(10));
                shipment("Microprocessor Units", "Global Fab Network", "Automation Hub EU",
                        50000.0, "Units", ShipmentStatus.IN_TRANSIT, LocalDateTime.now().plusDays(4));
            }
        }
        log.info("  shipments seeded");
    }

    private void seedInteractions() {
        switch (scale) {
            case LOCAL -> {
                interaction(InteractionType.NEED_REPORT, "Well pump needing maintenance in the central square.",
                        "Local Settlement", "CIT-LOCAL", InteractionStatus.VERIFIED);
            }
            case REGIONAL -> {
                interaction(InteractionType.NEED_REPORT, "Local aquifer levels dropping significantly in the Central Plain region.",
                        "Central Plains", "CIT-9928", InteractionStatus.VERIFIED);
                interaction(InteractionType.INNOVATION, "Proposed upgrade to irrigation efficiency using soil sensors.",
                        "Region", "CIT-4412", InteractionStatus.INTEGRATED);
            }
            case CONTINENTAL -> {
                interaction(InteractionType.NEED_REPORT, "Aquifer levels dropping — requesting continental hydro-desalination assessment.",
                        "Central Plains", "CIT-9928", InteractionStatus.VERIFIED);
                interaction(InteractionType.INNOVATION, "Proposed upgrade to 3D-Housing extrusion head for faster curing.",
                        "Continental", "CIT-4412", InteractionStatus.INTEGRATED);
                interaction(InteractionType.COLLABORATION, "Registered for experimental thorium reactor simulation.",
                        "Continental", "CIT-1055", InteractionStatus.PENDING);
            }
            case GLOBAL -> {
                interaction(InteractionType.NEED_REPORT, "Local aquifer levels dropping significantly in the Central Plain region. Requesting hydro-desalination assessment.",
                        "Central Plains", "CIT-9928", InteractionStatus.VERIFIED);
                interaction(InteractionType.INNOVATION, "Proposed upgrade to 3D-Housing extrusion head for 15% faster curing using carbon-fiber composite.",
                        "Global", "CIT-4412", InteractionStatus.INTEGRATED);
                interaction(InteractionType.COLLABORATION, "Registered for experimental thorium reactor maintenance simulation in Northern Europe sector.",
                        "EU-North", "CIT-1055", InteractionStatus.PENDING);
                interaction(InteractionType.INNOVATION, "Open-source AI diagnostic tool for predictive biosphere monitoring.",
                        "Global", "CIT-7763", InteractionStatus.VERIFIED);
            }
        }
        log.info("  interactions seeded");
    }

    private void seedBiosphereMetrics() {
        switch (scale) {
            case LOCAL -> {
                biosphereMetric("Local Air Quality Index", 42.0, "AQI", 50.0, BiosphereMetricStatus.NORMAL, 1.2);
                biosphereMetric("Local Stream pH Level", 7.2, "pH", 6.5, BiosphereMetricStatus.NORMAL, 0.1);
            }
            case REGIONAL -> {
                biosphereMetric("Regional CO2 Average", 410.0, "ppm", 350.0, BiosphereMetricStatus.WARNING, 2.0);
                biosphereMetric("Regional Temperature Deviation", 1.0, "°C", 1.5, BiosphereMetricStatus.NORMAL, 0.02);
                biosphereMetric("Regional Forest Cover Change", -0.5, "%/Year", 0.0, BiosphereMetricStatus.WARNING, -0.1);
            }
            case CONTINENTAL -> {
                biosphereMetric("Continental CO2 Concentration", 415.0, "ppm", 350.0, BiosphereMetricStatus.CRITICAL, 2.4);
                biosphereMetric("Continental Surface Temp Deviation", 1.1, "°C", 1.5, BiosphereMetricStatus.WARNING, 0.02);
                biosphereMetric("Continental Reforestation Rate", 4.2, "Million Hectares/Year", 10.0, BiosphereMetricStatus.NORMAL, 0.5);
                biosphereMetric("Continental Ocean Acidity", 8.06, "pH", 8.1, BiosphereMetricStatus.WARNING, -0.002);
            }
            case GLOBAL -> {
                biosphereMetric("Atmospheric CO2 Concentration", 419.5, "ppm", 350.0, BiosphereMetricStatus.CRITICAL, 2.4);
                biosphereMetric("Global Surface Temp Deviation", 1.15, "°C", 1.5, BiosphereMetricStatus.WARNING, 0.02);
                biosphereMetric("Ocean Surface Acidity", 8.06, "pH", 8.1, BiosphereMetricStatus.WARNING, -0.002);
                biosphereMetric("Global Reforestation Rate", 4.2, "Million Hectares/Year", 10.0, BiosphereMetricStatus.NORMAL, 0.5);
                biosphereMetric("Arctic Sea Ice Extent", 4.5, "Million km²", 5.0, BiosphereMetricStatus.CRITICAL, -0.8);
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
                        RuleStatus.ACTIVE, ValidationStatus.SCIENTIFICALLY_VALIDATED, "Local Council", 150);
            }
            case REGIONAL -> {
                rule("Regional Biosphere Stability",
                        "Industrial production must cease if biodiversity indices drop below thresholds.",
                        "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"BIOSPHERE_HEALTH\", \"action\": \"SUSPEND_PRODUCTION\"}",
                        RuleStatus.ACTIVE, ValidationStatus.SCIENTIFICALLY_VALIDATED, "Regional Commission", 2500);
            }
            case CONTINENTAL -> {
                rule("Continental Biosphere Stability Clause",
                        "All industrial production must cease in a region if local biodiversity indices drop below therapeutic thresholds.",
                        "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"BIOSPHERE_HEALTH\", \"action\": \"SUSPEND_PRODUCTION\"}",
                        RuleStatus.ACTIVE, ValidationStatus.SCIENTIFICALLY_VALIDATED, "Global Biosphere Commission", 12500);
                rule("Continental Caloric Security",
                        "Strategic reserves must maintain a 6-month buffer of essential nutrients before export is authorized.",
                        "{\"type\": \"RESERVE_CHECK\", \"metric\": \"FOOD\", \"min_buffer_months\": 6}",
                        RuleStatus.ACTIVE, ValidationStatus.SCIENTIFICALLY_VALIDATED, "Energy & Nutrition Council", 8900);
            }
            case GLOBAL -> {
                rule("Biosphere Stability Clause",
                        "All industrial production must cease in a region if local biodiversity indices drop below therapeutic thresholds.",
                        "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"BIOSPHERE_HEALTH\", \"action\": \"SUSPEND_PRODUCTION\"}",
                        RuleStatus.ACTIVE, ValidationStatus.SCIENTIFICALLY_VALIDATED, "Global Biosphere Commission", 12500);
                rule("Universal Caloric Security",
                        "Strategic reserves must maintain a 6-month buffer of essential nutrients before export is authorized.",
                        "{\"type\": \"RESERVE_CHECK\", \"metric\": \"FOOD\", \"min_buffer_months\": 6}",
                        RuleStatus.ACTIVE, ValidationStatus.SCIENTIFICALLY_VALIDATED, "Energy & Nutrition Council", 8900);
                rule("Global Water Equity",
                        "Transboundary water basins must be managed under shared scientific governance.",
                        "{\"type\": \"RESERVE_CHECK\", \"metric\": \"WATER\", \"min_buffer_months\": 3}",
                        RuleStatus.ACTIVE, ValidationStatus.SCIENTIFICALLY_VALIDATED, "Global Water Authority", 15000);
            }
        }
        log.info("  rules seeded");
    }

    private void seedAutomationUnits() {
        switch (scale) {
            case LOCAL -> {
                automationUnit("Community Maint-Bot", AutomationUnitType.BOT, "Local Settlement", AutomationUnitStatus.ACTIVE, "STANDBY");
            }
            case REGIONAL -> {
                automationUnit("Constructor Beta-1", AutomationUnitType.CONSTRUCTOR, "Southeast Asia", AutomationUnitStatus.ACTIVE, "ASSEMBLING_MODULAR_HOUSING");
                automationUnit("Agro-Drone Swarm Alpha", AutomationUnitType.DRONE, "Sub-Saharan Africa", AutomationUnitStatus.ACTIVE, "MONITORING_CROP_MATURITY");
            }
            case CONTINENTAL -> {
                automationUnit("Constructor Alpha-1", AutomationUnitType.CONSTRUCTOR, "Southeast Asia", AutomationUnitStatus.ACTIVE, "ASSEMBLING_MODULAR_HOUSING");
                automationUnit("Agro-Drone Swarm Beta", AutomationUnitType.DRONE, "Sub-Saharan Africa", AutomationUnitStatus.ACTIVE, "MONITORING_CROP_MATURITY");
                automationUnit("Maint-Bot Gamma-4", AutomationUnitType.BOT, "European Union", AutomationUnitStatus.IDLE, "STANDBY");
            }
            case GLOBAL -> {
                automationUnit("Constructor Alpha-1", AutomationUnitType.CONSTRUCTOR, "Southeast Asia", AutomationUnitStatus.ACTIVE, "ASSEMBLING_MODULAR_HOUSING");
                automationUnit("Agro-Drone Swarm Beta", AutomationUnitType.DRONE, "Sub-Saharan Africa", AutomationUnitStatus.ACTIVE, "MONITORING_CROP_MATURITY");
                automationUnit("Maint-Bot Gamma-4", AutomationUnitType.BOT, "European Union", AutomationUnitStatus.IDLE, "STANDBY");
                automationUnit("Deep Sea Probe Delta", AutomationUnitType.DRONE, "Pacific Ocean", AutomationUnitStatus.ACTIVE, "BIOSPHERE_MONITORING");
                automationUnit("Constructor Epsilon-7", AutomationUnitType.CONSTRUCTOR, "South America", AutomationUnitStatus.ACTIVE, "INFRASTRUCTURE_UPGRADE");
            }
        }
        log.info("  automation units seeded");
    }

    private void seedCommittees() {
        switch (scale) {
            case LOCAL -> {
                committee(CommitteeArea.LOCAL, "Local Community Council",
                        "Overseeing local resource allocation and community well-being.", ValidationLevel.COMMUNITY_VALIDATED);
            }
            case REGIONAL -> {
                committee(CommitteeArea.BIOSPHERE, "Regional Biosphere Commission",
                        "Auditing regional environmental indices.", ValidationLevel.PEER_REVIEWED);
                committee(CommitteeArea.ENERGY, "Regional Energy Council",
                        "Optimizing regional energy distribution.", ValidationLevel.PEER_REVIEWED);
            }
            case CONTINENTAL -> {
                committee(CommitteeArea.BIOSPHERE, "Continental Biosphere Commission",
                        "Auditing continental planetary boundaries and biodiversity indices.", ValidationLevel.EMPIRICAL_VALIDATED);
                committee(CommitteeArea.ENERGY, "Continental Energy & Nutrition Council",
                        "Optimizing thermodynamic efficiency in food and power systems.", ValidationLevel.PEER_REVIEWED);
            }
            case GLOBAL -> {
                committee(CommitteeArea.BIOSPHERE, "Global Biosphere Commission",
                        "Auditing planetary boundaries and biodiversity indices.", ValidationLevel.EMPIRICAL_VALIDATED);
                committee(CommitteeArea.ENERGY, "Energy & Nutrition Council",
                        "Optimizing thermodynamic efficiency in food and power systems.", ValidationLevel.PEER_REVIEWED);
                committee(CommitteeArea.SOCIAL, "Global Social Stability Board",
                        "Monitoring and mediating social stability across all regions.", ValidationLevel.EMPIRICAL_VALIDATED);
            }
        }
        log.info("  committees seeded");
    }

    private void seedSocial() {
        switch (scale) {
            case LOCAL -> {
                Incident dispute = incident(IncidentType.CONFLICT, "Local Settlement",
                        "Minor resource allocation dispute in community garden.", RiskLevel.LOW, IncidentStatus.ANALYZING,
                        List.of("CIT-LOCAL-01", "CIT-LOCAL-02"));
                incidentRepository.save(dispute);
            }
            case REGIONAL -> {
                Incident dispute = incident(IncidentType.CONFLICT, "Sector 7 Community Garden",
                        "Resource allocation dispute regarding irrigation timing.", RiskLevel.LOW, IncidentStatus.ANALYZING,
                        List.of("CIT-8821", "CIT-3310"));
                Incident anomaly = incident(IncidentType.BEHAVIORAL_ANOMALY, "Urban Transit Node 04",
                        "Citizen showing signs of extreme stress and erratic behavior.", RiskLevel.MEDIUM, IncidentStatus.REPORTED,
                        List.of("CIT-1055"));
                incidentRepository.saveAll(List.of(dispute, anomaly));

                Case c = new Case();
                c.setSourceIncident(anomaly);
                c.setStatus(CaseStatus.REHABILITATION);
                c.setResolutionPlan("Relocation to low-density green zone and assignment of a behavioral mediator.");
                c.setRehabilitationProgram("Cognitive-behavioral support and social integration workshop.");
                c.setMonitoringPlan("Biometric stress monitoring for 3 months.");
                c.setPanelExpertIds(List.of("EXP-PSY-01", "EXP-SOC-04"));
                caseRepository.save(c);
            }
            case CONTINENTAL -> {
                Incident dispute = incident(IncidentType.CONFLICT, "Sector 7 Community Garden",
                        "Resource allocation dispute regarding irrigation timing.", RiskLevel.LOW, IncidentStatus.ANALYZING,
                        List.of("CIT-8821", "CIT-3310"));
                Incident anomaly = incident(IncidentType.BEHAVIORAL_ANOMALY, "Urban Transit Node 04",
                        "Citizen showing signs of extreme stress and erratic behavior.", RiskLevel.MEDIUM, IncidentStatus.REPORTED,
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
                c.setStatus(CaseStatus.REHABILITATION);
                c.setResolutionPlan("Relocation to low-density green zone and assignment of a behavioral mediator.");
                c.setRehabilitationProgram("Cognitive-behavioral support and social integration workshop.");
                c.setMonitoringPlan("Biometric stress monitoring for 3 months.");
                c.setPanelExpertIds(List.of("EXP-PSY-01", "EXP-SOC-04"));
                caseRepository.save(c);
            }
            case GLOBAL -> {
                Incident dispute = incident(IncidentType.CONFLICT, "Sector 7 Community Garden",
                        "Resource allocation dispute regarding irrigation timing.", RiskLevel.LOW, IncidentStatus.ANALYZING,
                        List.of("CIT-8821", "CIT-3310"));
                Incident anomaly = incident(IncidentType.BEHAVIORAL_ANOMALY, "Urban Transit Node 04",
                        "Citizen showing signs of extreme stress and erratic behavior.", RiskLevel.MEDIUM, IncidentStatus.REPORTED,
                        List.of("CIT-1055"));
                Incident massEvent = incident(IncidentType.OTHER, "Global Forum Online",
                        "Large-scale coordinated proposal for constitutional amendment on water rights.", RiskLevel.LOW, IncidentStatus.ANALYZING,
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
                c.setStatus(CaseStatus.REHABILITATION);
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
                Skill farming = skill("Sustainable Agriculture", SkillCategory.AGRICULTURE, "Local food production techniques.");
                skillRepository.save(farming);

                Citizen local = citizen("CIT-LOCAL-01", "Local Pioneer", List.of(farming),
                        List.of("Community Building", "Permaculture"), 50.0, "Founding member of the local settlement.");
                citizenRepository.save(local);

                Project garden = project("Community Permaculture Project", "Establishing a self-sustaining food forest.",
                        ProjectCategory.AGRICULTURE, ImpactArea.FOOD_SECURITY, List.of("Sustainable Agriculture"), ProjectStatus.ACTIVE);
                projectRepository.save(garden);
            }
            case REGIONAL -> {
                Skill engineering = skill("Engineering", SkillCategory.ENGINEERING, "Sustainable systems design.");
                Skill science = skill("Science", SkillCategory.SCIENCE, "Empirical research and validation.");
                skillRepository.saveAll(List.of(engineering, science));

                Citizen jackson = citizen("CIT-0001", "Jackson Wendel", List.of(engineering, science),
                        List.of("Automation", "Sustainability"), 120.0, "Regional architect of sustainable systems.");
                Citizen maria = citizen("CIT-0002", "Maria Chen", List.of(science),
                        List.of("Biosphere", "Data Analysis"), 90.0, "Environmental data scientist.");
                citizenRepository.saveAll(List.of(jackson, maria));

                Project reforestation = project("Regional Reforestation Initiative", "Automated reforestation using seed-planting drones.",
                        ProjectCategory.ENVIRONMENTAL, ImpactArea.REFORESTATION, List.of("Engineering", "Science"), ProjectStatus.ACTIVE);
                projectRepository.save(reforestation);
            }
            case CONTINENTAL -> {
                Skill engineering = skill("Engineering", SkillCategory.ENGINEERING, "Sustainable systems design.");
                Skill science = skill("Science", SkillCategory.SCIENCE, "Empirical research and validation.");
                Skill education = skill("Education", SkillCategory.EDUCATION, "Knowledge transmission.");
                skillRepository.saveAll(List.of(engineering, science, education));

                Citizen jackson = citizen("CIT-0001", "Jackson Wendel", List.of(engineering, science),
                        List.of("Automation", "Sustainability", "DDD"), 150.0, "Lead architect of the Civilization Operating System.");
                Citizen maria = citizen("CIT-0002", "Maria Chen", List.of(science, education),
                        List.of("Biosphere", "Data Analysis", "Teaching"), 120.0, "Environmental data scientist and educator.");
                Citizen amara = citizen("CIT-0003", "Amara Osei", List.of(engineering),
                        List.of("Renewable Energy", "Infrastructure"), 110.0, "Solar infrastructure specialist.");
                citizenRepository.saveAll(List.of(jackson, maria, amara));

                Project reforestation = project("Amazon Restoration Project", "Automated reforestation using seed-planting drones and bio-monitoring.",
                        ProjectCategory.ENVIRONMENTAL, ImpactArea.REFORESTATION, List.of("Engineering", "Science"), ProjectStatus.ACTIVE);
                Project solarGrid = project("Continental Solar Grid Expansion", "Connecting continental solar farms via smart grid.",
                        ProjectCategory.ENERGY, ImpactArea.INFRASTRUCTURE, List.of("Engineering"), ProjectStatus.ACTIVE);
                projectRepository.saveAll(List.of(reforestation, solarGrid));
            }
            case GLOBAL -> {
                Skill engineering = skill("Engineering", SkillCategory.ENGINEERING, "Sustainable systems design.");
                Skill science = skill("Science", SkillCategory.SCIENCE, "Empirical research and validation.");
                Skill education = skill("Education", SkillCategory.EDUCATION, "Knowledge transmission.");
                Skill medicine = skill("Medicine", SkillCategory.HEALTH, "Preventive and regenerative healthcare.");
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
                        ProjectCategory.ENVIRONMENTAL, ImpactArea.REFORESTATION, List.of("Engineering", "Science"), ProjectStatus.ACTIVE);
                Project globalHealth = project("Global Preventive Health Initiative", "Deploying AI-driven diagnostic networks worldwide.",
                        ProjectCategory.HEALTH, ImpactArea.PUBLIC_HEALTH, List.of("Medicine", "Science"), ProjectStatus.ACTIVE);
                Project educationPlatform = project("Open Knowledge Platform", "Universal access to advanced scientific and technical education.",
                        ProjectCategory.EDUCATION, ImpactArea.KNOWLEDGE_SHARING, List.of("Education", "Science"), ProjectStatus.PROPOSED);
                projectRepository.saveAll(List.of(reforestation, globalHealth, educationPlatform));
            }
        }
        log.info("  contribution seeded");
    }

    private Resource resource(String name, ResourceType type, String description, Point location,
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

    private Need need(NeedCategory category, String region, String description, double quantity,
                      String unit, int priority, NeedStatus status) {
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

    private Facility facility(String name, FacilityType type, String region, double efficiency,
                              FacilityStatus status, String currentOutput) {
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
                              String unit, ShipmentStatus status, LocalDateTime eta) {
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

    private Interaction interaction(InteractionType type, String content, String region,
                                    String citizenId, InteractionStatus status) {
        Interaction i = new Interaction();
        i.setType(type);
        i.setContent(content);
        i.setRegion(region);
        i.setCitizenId(citizenId);
        i.setStatus(status);
        return i;
    }

    private BiosphereMetric biosphereMetric(String name, double value, String unit,
                                            double safetyLimit, BiosphereMetricStatus status, double drift) {
        BiosphereMetric m = new BiosphereMetric();
        m.setName(name);
        m.setValue(value);
        m.setUnit(unit);
        m.setSafetyLimit(safetyLimit);
        m.setStatus(status);
        m.setDrift(drift);
        return m;
    }

    private Rule rule(String title, String description, String logicCode, RuleStatus status,
                      ValidationStatus validationStatus, String validatedBy, int votesCount) {
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

    private AutomationUnit automationUnit(String name, AutomationUnitType type, String region,
                                          AutomationUnitStatus status, String currentTask) {
        AutomationUnit u = new AutomationUnit();
        u.setName(name);
        u.setType(type);
        u.setRegion(region);
        u.setStatus(status);
        u.setCurrentTask(currentTask);
        return u;
    }

    private ScientificCommittee committee(CommitteeArea area, String name, String mandate, ValidationLevel validationLevel) {
        ScientificCommittee c = new ScientificCommittee();
        c.setArea(area);
        c.setName(name);
        c.setMandate(mandate);
        c.setValidationLevel(validationLevel);
        return c;
    }

    private Incident incident(IncidentType type, String location, String description, RiskLevel riskLevel,
                              IncidentStatus status, List<String> participantIds) {
        Incident i = new Incident();
        i.setType(type);
        i.setLocation(location);
        i.setDescription(description);
        i.setRiskLevel(riskLevel);
        i.setStatus(status);
        i.setParticipantIds(participantIds);
        return i;
    }

    private Skill skill(String name, SkillCategory category, String description) {
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

    private Project project(String title, String description, ProjectCategory category, ImpactArea impactArea,
                            List<String> requiredSkills, ProjectStatus status) {
        Project p = new Project();
        p.setTitle(title);
        p.setDescription(description);
        p.setCategory(category);
        p.setImpactArea(impactArea);
        p.setRequiredSkillNames(requiredSkills);
        p.setStatus(status);
        return p;
    }

    private void seedResourceRegions() {
        switch (scale) {
            case LOCAL -> {
                region("Fertile Valley", "Rich alluvial soil with abundant freshwater springs.",
                    point(-23.55, -46.63), CivilizationScale.LOCAL,
                    85.0, 90.0, 20.0, 30.0, 40.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.FOOD, 15.0);
                region("Granite Highlands", "Mineral-rich highlands with strong winds for energy.",
                    point(-22.5, -45.0), CivilizationScale.LOCAL,
                    10.0, 40.0, 80.0, 70.0, 20.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.MINERAL, 20.0);
                region("Coastal Delta", "Mangrove delta with abundant water and marine resources.",
                    point(-24.0, -47.0), CivilizationScale.LOCAL,
                    60.0, 95.0, 10.0, 50.0, 30.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.WATER, 18.0);
            }
            case REGIONAL -> {
                region("Amazon Basin", "Dense rainforest with unparalleled biodiversity.",
                    point(-3.0, -60.0), CivilizationScale.REGIONAL,
                    80.0, 95.0, 30.0, 20.0, 10.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.WATER, 200.0);
                region("Andean Plateau", "High-altitude lithium flats and mineral deposits.",
                    point(-20.0, -67.0), CivilizationScale.REGIONAL,
                    30.0, 20.0, 95.0, 60.0, 15.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.MINERAL, 250.0);
                region("Pampas Plains", "Vast grasslands ideal for agriculture.",
                    point(-35.0, -62.0), CivilizationScale.REGIONAL,
                    95.0, 50.0, 15.0, 40.0, 50.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.FOOD, 180.0);
                region("Patagonian Coast", "Wind-battered coast with immense wind energy potential.",
                    point(-48.0, -68.0), CivilizationScale.REGIONAL,
                    40.0, 60.0, 50.0, 90.0, 20.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.ENERGY, 220.0);
            }
            case CONTINENTAL -> {
                region("Sahara Solar Corridor", "World's highest solar irradiance zone.",
                    point(23.5, 12.0), CivilizationScale.CONTINENTAL,
                    5.0, 5.0, 40.0, 98.0, 5.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.ENERGY, 800.0);
                region("Congo Basin", "Second largest rainforest on Earth, vast water reserves.",
                    point(0.0, 22.0), CivilizationScale.CONTINENTAL,
                    75.0, 95.0, 60.0, 30.0, 20.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.WATER, 700.0);
                region("Siberian Taiga", "Massive boreal forest with timber and mineral wealth.",
                    point(62.0, 95.0), CivilizationScale.CONTINENTAL,
                    30.0, 60.0, 85.0, 40.0, 10.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.MINERAL, 900.0);
                region("North American Grain Belt", "World-class agricultural soil.",
                    point(45.0, -100.0), CivilizationScale.CONTINENTAL,
                    95.0, 50.0, 20.0, 30.0, 50.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.FOOD, 600.0);
                region("North Sea Wind Zone", "Shallow waters with consistent high winds.",
                    point(55.0, 3.0), CivilizationScale.CONTINENTAL,
                    20.0, 80.0, 30.0, 95.0, 30.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.ENERGY, 500.0);
            }
            case GLOBAL -> {
                region("Sahara Solar Belt", "Highest solar potential on Earth.",
                    point(23.5, 12.0), CivilizationScale.GLOBAL,
                    5.0, 5.0, 40.0, 99.0, 5.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.ENERGY, 800.0);
                region("Amazon Rainforest", "Largest tropical rainforest, critical for climate.",
                    point(-3.5, -60.0), CivilizationScale.GLOBAL,
                    80.0, 98.0, 30.0, 20.0, 10.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.WATER, 700.0);
                region("Himalayan Watershed", "Glacial freshwater reserve for billions.",
                    point(28.0, 85.0), CivilizationScale.GLOBAL,
                    40.0, 99.0, 60.0, 50.0, 15.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.WATER, 600.0);
                region("Great Plains", "Most productive agricultural land on Earth.",
                    point(45.0, -100.0), CivilizationScale.GLOBAL,
                    97.0, 45.0, 20.0, 30.0, 55.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.FOOD, 550.0);
                region("Atacama Lithium Triangle", "World's largest lithium brine reserves.",
                    point(-23.5, -67.0), CivilizationScale.GLOBAL,
                    10.0, 5.0, 98.0, 70.0, 5.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.MINERAL, 400.0);
                region("North Sea Energy Hub", "Offshore wind + tidal + hydrogen potential.",
                    point(55.0, 3.0), CivilizationScale.GLOBAL,
                    20.0, 80.0, 30.0, 96.0, 35.0, io.github.opencivilizationplatform.modules.region.domain.ResourceType.ENERGY, 500.0);
            }
        }
        log.info("  resource regions seeded");
    }

    private void region(String name, String description, Point location, CivilizationScale scale,
                         Double food, Double water, Double mineral, Double energy, Double housing,
                         io.github.opencivilizationplatform.modules.region.domain.ResourceType dominant, Double radius) {
        ResourceRegion r = new ResourceRegion();
        r.setName(name);
        r.setDescription(description);
        r.setLocation(location);
        r.setScale(scale);
        r.setFoodAvailability(food);
        r.setWaterAvailability(water);
        r.setMineralAvailability(mineral);
        r.setEnergyAvailability(energy);
        r.setHousingAvailability(housing);
        r.setDominantResource(dominant);
        r.setRadiusKm(radius);
        r.setClaimed(false);
        resourceRegionRepository.save(r);
    }

    private Point point(double lat, double lon) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}
