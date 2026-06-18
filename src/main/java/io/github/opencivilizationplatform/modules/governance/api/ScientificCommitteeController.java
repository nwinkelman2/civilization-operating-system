package io.github.opencivilizationplatform.modules.governance.api;

import io.github.opencivilizationplatform.modules.governance.application.ScientificCommitteeService;
import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/governance")
public class ScientificCommitteeController {

    private final ScientificCommitteeService scientificCommitteeService;

    public ScientificCommitteeController(ScientificCommitteeService scientificCommitteeService) {
        this.scientificCommitteeService = scientificCommitteeService;
    }

    @GetMapping
    public Page<ScientificCommittee> getAllCommittees(Pageable pageable) {
        return scientificCommitteeService.getAllCommittees(pageable);
    }

    @PostMapping
    public ScientificCommittee saveCommittee(@Valid @RequestBody ScientificCommittee committee) {
        return scientificCommitteeService.saveCommittee(committee);
    }
}
