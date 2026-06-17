package io.github.opencivilizationplatform.modules.needs.api;

import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.github.opencivilizationplatform.modules.needs.infrastructure.NeedRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/needs")
public class NeedController {

    private final NeedRepository needRepository;

    public NeedController(NeedRepository needRepository) {
        this.needRepository = needRepository;
    }

    @GetMapping
    public java.util.List<Need> getAllNeeds() {
        return needRepository.findAll();
    }

    @GetMapping("/region/{region}")
    public java.util.List<Need> getNeedsByRegion(@PathVariable String region) {
        return needRepository.findByRegion(region);
    }

    @PostMapping
    public Need saveNeed(@RequestBody Need need) {
        return needRepository.save(need);
    }
}
