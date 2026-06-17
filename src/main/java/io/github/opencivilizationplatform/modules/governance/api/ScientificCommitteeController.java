package io.github.opencivilizationplatform.modules.governance.api;

import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import io.github.opencivilizationplatform.modules.governance.infrastructure.ScientificCommitteeRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/governance")
public class ScientificCommitteeController {

    private final ScientificCommitteeRepository committeeRepository;

    public ScientificCommitteeController(ScientificCommitteeRepository committeeRepository) {
        this.committeeRepository = committeeRepository;
    }

    @GetMapping
    public java.util.List<ScientificCommittee> getAllCommittees() {
        return committeeRepository.findAll();
    }

    @PostMapping
    public ScientificCommittee saveCommittee(@RequestBody ScientificCommittee committee) {
        return committeeRepository.save(committee);
    }
}
