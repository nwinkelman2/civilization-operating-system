package io.github.opencivilizationplatform.modules.needs.application;

import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.github.opencivilizationplatform.modules.needs.infrastructure.NeedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NeedService {
    private final NeedRepository needRepository;

    public NeedService(NeedRepository needRepository) {
        this.needRepository = needRepository;
    }

    public Page<Need> getAllNeeds(Pageable pageable) {
        return needRepository.findAll(pageable);
    }

    public java.util.List<Need> getNeedsByRegion(String region) {
        return needRepository.findByRegion(region);
    }

    public Need saveNeed(Need need) {
        return needRepository.save(need);
    }
}
