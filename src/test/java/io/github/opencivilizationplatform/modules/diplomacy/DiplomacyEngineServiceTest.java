package io.github.opencivilizationplatform.modules.diplomacy;

import io.github.opencivilizationplatform.modules.diplomacy.application.DiplomacyEngineService;
import io.github.opencivilizationplatform.modules.diplomacy.domain.DiplomaticRelation;
import io.github.opencivilizationplatform.modules.diplomacy.domain.DiplomaticStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiplomacyEngineServiceTest {

    private DiplomacyEngineService diplomacyEngineService;

    @BeforeEach
    void setUp() {
        diplomacyEngineService = new DiplomacyEngineService();
    }

    @Test
    void testDetermineStatusForTension() {
        assertEquals(DiplomaticStatus.WAR, diplomacyEngineService.determineStatusForTension(85.0));
        assertEquals(DiplomaticStatus.HOSTILE, diplomacyEngineService.determineStatusForTension(65.0));
        assertEquals(DiplomaticStatus.NEUTRAL, diplomacyEngineService.determineStatusForTension(45.0));
        assertEquals(DiplomaticStatus.NON_AGGRESSION_PACT, diplomacyEngineService.determineStatusForTension(25.0));
        assertEquals(DiplomaticStatus.ALLIED, diplomacyEngineService.determineStatusForTension(10.0));
    }

    @Test
    void testEvaluateTensionAndAutoStatus() {
        DiplomaticRelation relation = new DiplomaticRelation("civ-A", "civ-B", DiplomaticStatus.NEUTRAL, 40.0);
        DiplomaticRelation updated = diplomacyEngineService.evaluateTension(relation, 45.0);

        assertNotNull(updated);
        assertEquals(85.0, updated.tensionIndex());
        assertEquals(DiplomaticStatus.WAR, updated.status());
    }

    @Test
    void testUpdateDiplomaticStatus() {
        DiplomaticRelation relation = new DiplomaticRelation("civ-A", "civ-B", DiplomaticStatus.NEUTRAL, 40.0);
        DiplomaticRelation updated = diplomacyEngineService.updateDiplomaticStatus(relation, DiplomaticStatus.NON_AGGRESSION_PACT);

        assertEquals(DiplomaticStatus.NON_AGGRESSION_PACT, updated.status());
        assertEquals(40.0, updated.tensionIndex());
    }

    @Test
    void testProposeAllianceAccepted() {
        DiplomaticRelation relation = new DiplomaticRelation("civ-A", "civ-B", DiplomaticStatus.NEUTRAL, 20.0);
        DiplomaticRelation result = diplomacyEngineService.proposeAlliance(relation);

        assertEquals(DiplomaticStatus.ALLIED, result.status());
        assertTrue(result.tensionIndex() <= 15.0);
    }

    @Test
    void testProposeAllianceRejected() {
        DiplomaticRelation relation = new DiplomaticRelation("civ-A", "civ-B", DiplomaticStatus.NEUTRAL, 50.0);
        DiplomaticRelation result = diplomacyEngineService.proposeAlliance(relation);

        assertEquals(DiplomaticStatus.NEUTRAL, result.status());
        assertEquals(50.0, result.tensionIndex());
    }

    @Test
    void testSignPeaceAgreement() {
        DiplomaticRelation relation = new DiplomaticRelation("civ-A", "civ-B", DiplomaticStatus.WAR, 90.0);
        DiplomaticRelation peace = diplomacyEngineService.signPeaceAgreement(relation);

        assertEquals(DiplomaticStatus.NEUTRAL, peace.status());
        assertEquals(35.0, peace.tensionIndex());
    }

    @Test
    void testSignNonAggressionPact() {
        DiplomaticRelation relation = new DiplomaticRelation("civ-A", "civ-B", DiplomaticStatus.HOSTILE, 45.0);
        DiplomaticRelation updated = diplomacyEngineService.signNonAggressionPact(relation);

        assertEquals(DiplomaticStatus.NON_AGGRESSION_PACT, updated.status());
        assertEquals(30.0, updated.tensionIndex());
    }

    @Test
    void testProcessDiplomacyCycle() {
        DiplomaticRelation rel1 = new DiplomaticRelation("civ-1", "civ-2", DiplomaticStatus.NEUTRAL, 50.0);
        diplomacyEngineService.registerRelation(rel1);

        List<DiplomaticRelation> results = diplomacyEngineService.processDiplomacyCycle();
        assertEquals(1, results.size());
        assertEquals("civ-1", results.get(0).sourceCivId());
    }
}
