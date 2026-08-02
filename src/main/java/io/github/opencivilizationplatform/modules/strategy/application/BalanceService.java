package io.github.opencivilizationplatform.modules.strategy.application;

import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.needs.infrastructure.NeedRepository;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BalanceService {

    private final ResourceRepository resourceRepository;
    private final NeedRepository needRepository;

    public BalanceService(ResourceRepository resourceRepository, NeedRepository needRepository) {
        this.resourceRepository = resourceRepository;
        this.needRepository = needRepository;
    }

    @Cacheable(value = "balance", key = "'report'")
    public List<BalanceDTO> getBalanceReport() {
        Map<String, Double> supply = resourceRepository.findAll().stream()
                .collect(Collectors.groupingBy(r -> r.getType().name(),
                        Collectors.summingDouble(r -> r.getQuantity())));

        Map<String, Double> demand = needRepository.findAll().stream()
                .collect(Collectors.groupingBy(n -> n.getCategory().name(),
                        Collectors.summingDouble(n -> n.getQuantity())));

        Set<String> allCategories = new HashSet<>(supply.keySet());
        allCategories.addAll(demand.keySet());

        return allCategories.stream()
                .map(category -> {
                    double s = supply.getOrDefault(category, 0.0);
                    double d = demand.getOrDefault(category, 0.0);
                    double percentage = d > 0 ? (s / d) * 100 : 100.0;
                    String status = s >= d ? "STABLE" : (percentage < 50 ? "CRITICAL" : "DEFICIT");
                    return new BalanceDTO(category, s, d, "units", percentage, status);
                })
                .collect(Collectors.toList());
    }
}
