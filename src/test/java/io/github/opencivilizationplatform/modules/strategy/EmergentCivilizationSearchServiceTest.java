package io.github.opencivilizationplatform.modules.strategy;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNode;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusNodeRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.strategy.application.EmergentCivilizationSearchService;
import io.github.opencivilizationplatform.modules.strategy.domain.EmergentArchetypeReport;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.github.opencivilizationplatform.modules.trade.infrastructure.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmergentCivilizationSearchServiceTest {

    @Mock
    private CivilizationRepository civilizationRepository;
    @Mock
    private NexusNodeRepository nodeRepository;
    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private RuleRepository ruleRepository;

    private EmergentCivilizationSearchService service;

    @BeforeEach
    void setUp() {
        service = new EmergentCivilizationSearchService(
                civilizationRepository,
                nodeRepository,
                tradeRepository,
                ruleRepository
        );
    }

    @Test
    void testEvaluateEmergentArchetypes_TechnocraticMesh() {
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("Nexus Primus");
        civ.setStatus(CivilizationStatus.ACTIVE);
        civ.setScienceBotsPriority(50);

        when(civilizationRepository.findByStatus(CivilizationStatus.ACTIVE)).thenReturn(List.of(civ));
        when(nodeRepository.findByCivilizationId(1L)).thenReturn(List.of(new NexusNode(), new NexusNode(), new NexusNode()));
        when(tradeRepository.findByFromCivilizationIdOrToCivilizationId(1L, 1L)).thenReturn(List.of());
        when(ruleRepository.findByCivilizationId(1L)).thenReturn(List.of());

        List<EmergentArchetypeReport> reports = service.evaluateEmergentArchetypes();
        assertEquals(1, reports.size());
        EmergentArchetypeReport report = reports.get(0);

        assertEquals("TECHNOCRATIC_MESH", report.archetype());
        assertEquals(1L, report.civilizationId());
        assertEquals("Nexus Primus", report.civilizationName());
        assertTrue(report.emergenceScore() > 0.0);
        assertNotNull(report.keyFeature());
    }

    @Test
    void testEvaluateEmergentArchetypes_HighVelocityTrader() {
        Civilization civ = new Civilization();
        civ.setId(2L);
        civ.setName("Mercantile Hub");
        civ.setStatus(CivilizationStatus.ACTIVE);

        when(civilizationRepository.findByStatus(CivilizationStatus.ACTIVE)).thenReturn(List.of(civ));
        when(nodeRepository.findByCivilizationId(2L)).thenReturn(List.of());
        when(tradeRepository.findByFromCivilizationIdOrToCivilizationId(2L, 2L)).thenReturn(List.of(new TradeAgreement(), new TradeAgreement(), new TradeAgreement()));
        when(ruleRepository.findByCivilizationId(2L)).thenReturn(List.of());

        List<EmergentArchetypeReport> reports = service.evaluateEmergentArchetypes();
        assertEquals(1, reports.size());
        EmergentArchetypeReport report = reports.get(0);

        assertEquals("HIGH_VELOCITY_TRADER", report.archetype());
        assertEquals("Mercantile Hub", report.civilizationName());
    }

    @Test
    void testEvaluateEmergentArchetypes_ScientificDirectorate() {
        Civilization civ = new Civilization();
        civ.setId(3L);
        civ.setName("Akademia");
        civ.setStatus(CivilizationStatus.ACTIVE);
        civ.setScienceBotsPriority(80);

        when(civilizationRepository.findByStatus(CivilizationStatus.ACTIVE)).thenReturn(List.of(civ));
        when(nodeRepository.findByCivilizationId(3L)).thenReturn(List.of());
        when(tradeRepository.findByFromCivilizationIdOrToCivilizationId(3L, 3L)).thenReturn(List.of());
        when(ruleRepository.findByCivilizationId(3L)).thenReturn(List.of(new Rule(), new Rule(), new Rule()));

        List<EmergentArchetypeReport> reports = service.evaluateEmergentArchetypes();
        assertEquals(1, reports.size());
        EmergentArchetypeReport report = reports.get(0);

        assertEquals("SCIENTIFIC_DIRECTORATE", report.archetype());
        assertEquals("Akademia", report.civilizationName());
    }

    @Test
    void testEvaluateEmergentArchetypes_DecentralizedAgrarian() {
        Civilization civ = new Civilization();
        civ.setId(4L);
        civ.setName("Green Haven");
        civ.setStatus(CivilizationStatus.ACTIVE);
        civ.setFood(300.0);
        civ.setAgriBotsPriority(50);

        when(civilizationRepository.findByStatus(CivilizationStatus.ACTIVE)).thenReturn(List.of(civ));
        when(nodeRepository.findByCivilizationId(4L)).thenReturn(List.of());
        when(tradeRepository.findByFromCivilizationIdOrToCivilizationId(4L, 4L)).thenReturn(List.of());
        when(ruleRepository.findByCivilizationId(4L)).thenReturn(List.of());

        List<EmergentArchetypeReport> reports = service.evaluateEmergentArchetypes();
        assertEquals(1, reports.size());
        EmergentArchetypeReport report = reports.get(0);

        assertEquals("DECENTRALIZED_AGRARIAN", report.archetype());
        assertEquals("Green Haven", report.civilizationName());
    }

    @Test
    void testEvaluateEmergentArchetypes_FallbackWhenActiveEmpty() {
        Civilization civ = new Civilization();
        civ.setId(5L);
        civ.setName("Emerging Colony");
        civ.setStatus(CivilizationStatus.EMERGING);
        civ.setFood(150.0);

        when(civilizationRepository.findByStatus(CivilizationStatus.ACTIVE)).thenReturn(List.of());
        when(civilizationRepository.findAll()).thenReturn(List.of(civ));
        when(nodeRepository.findByCivilizationId(5L)).thenReturn(List.of());
        when(tradeRepository.findByFromCivilizationIdOrToCivilizationId(5L, 5L)).thenReturn(List.of());
        when(ruleRepository.findByCivilizationId(5L)).thenReturn(List.of());

        List<EmergentArchetypeReport> reports = service.evaluateEmergentArchetypes();
        assertEquals(1, reports.size());
        assertEquals("Emerging Colony", reports.get(0).civilizationName());
    }
}
