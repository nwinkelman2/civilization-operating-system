package io.github.opencivilizationplatform.modules.needs.api;

import io.github.opencivilizationplatform.modules.needs.application.NeedService;
import io.github.opencivilizationplatform.modules.needs.domain.Need;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/needs")
public class NeedController {

    private final NeedService needService;

    public NeedController(NeedService needService) {
        this.needService = needService;
    }

    @GetMapping
    public Page<Need> getAllNeeds(Pageable pageable) {
        return needService.getAllNeeds(pageable);
    }

    @GetMapping("/region/{region}")
    public java.util.List<Need> getNeedsByRegion(@PathVariable String region) {
        return needService.getNeedsByRegion(region);
    }

    @PostMapping
    public Need saveNeed(@Valid @RequestBody Need need) {
        return needService.saveNeed(need);
    }
}
