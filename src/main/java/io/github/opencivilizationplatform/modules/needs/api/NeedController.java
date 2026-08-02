package io.github.opencivilizationplatform.modules.needs.api;

import io.github.opencivilizationplatform.modules.needs.application.NeedService;
import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/needs")
@Tag(name = "Needs", description = "Need management endpoints")
public class NeedController {

    private final NeedService needService;

    public NeedController(NeedService needService) {
        this.needService = needService;
    }

    @GetMapping
    @Operation(summary = "List all needs", description = "Returns a paginated list of all needs")
    public Page<Need> getAllNeeds(Pageable pageable) {
        return needService.getAllNeeds(pageable);
    }

    @GetMapping("/region/{region}")
    @Operation(summary = "Get needs by region", description = "Returns needs filtered by region")
    public java.util.List<Need> getNeedsByRegion(@PathVariable String region) {
        return needService.getNeedsByRegion(region);
    }

    @PostMapping
    @Operation(summary = "Create a need", description = "Creates a new need record")
    public Need saveNeed(@Valid @RequestBody Need need) {
        return needService.saveNeed(need);
    }
}
