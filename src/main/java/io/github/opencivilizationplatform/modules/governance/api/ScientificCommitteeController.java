package io.github.opencivilizationplatform.modules.governance.api;

import io.github.opencivilizationplatform.modules.governance.application.ScientificCommitteeService;
import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/governance")
@Tag(name = "Scientific Committee", description = "Scientific committee management endpoints")
public class ScientificCommitteeController {

    private final ScientificCommitteeService scientificCommitteeService;

    public ScientificCommitteeController(ScientificCommitteeService scientificCommitteeService) {
        this.scientificCommitteeService = scientificCommitteeService;
    }

    @GetMapping
    @Operation(summary = "List all committees", description = "Returns a paginated list of scientific committees")
    public Page<ScientificCommittee> getAllCommittees(Pageable pageable) {
        return scientificCommitteeService.getAllCommittees(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a committee", description = "Creates a new scientific committee record")
    public ScientificCommittee saveCommittee(@Valid @RequestBody ScientificCommittee committee) {
        return scientificCommitteeService.saveCommittee(committee);
    }
}
