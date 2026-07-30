package io.github.opencivilizationplatform.graphql;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CivilizationGraphQLController {

    private final CivilizationService civilizationService;
    private final ResourceRegionService regionService;
    private final VoxtexMeshService voxtexService;

    public CivilizationGraphQLController(CivilizationService civilizationService,
                                          ResourceRegionService regionService,
                                          VoxtexMeshService voxtexService) {
        this.civilizationService = civilizationService;
        this.regionService = regionService;
        this.voxtexService = voxtexService;
    }

    @QueryMapping
    public Map<String, Object> civilizations(@Argument int page, @Argument int size) {
        Page<Civilization> p = civilizationService.getAllCivilizations(
            PageRequest.of(page > 0 ? page : 0, size > 0 ? size : 20));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", p.getContent());
        result.put("totalPages", p.getTotalPages());
        result.put("totalElements", p.getTotalElements());
        result.put("size", p.getSize());
        result.put("number", p.getNumber());
        return result;
    }

    @QueryMapping
    public Civilization civilization(@Argument Long id) {
        return civilizationService.getCivilization(id);
    }

    @MutationMapping
    public Civilization createCivilization(@Argument String name, @Argument CivilizationScale scale, @Argument String region) {
        return civilizationService.createCivilization(name, scale != null ? scale : CivilizationScale.LOCAL,
            region != null ? region : "unknown", "graphql-user");
    }

    @MutationMapping
    public Civilization foundCivilization(@Argument String name, @Argument CivilizationScale scale,
                                           @Argument Long regionId) {
        ResourceRegion reg = regionService.getRegion(regionId);
        Civilization civ = civilizationService.createCivilization(name,
            scale != null ? scale : CivilizationScale.LOCAL, reg.getName(), "graphql-user");
        regionService.claimRegion(regionId, civ.getId());
        voxtexService.registerNode(civ.getName() + "-Primary",
            io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNodeType.PRIMARY,
            reg.getName(), civ.getId(), "Primary node for " + civ.getName());
        return civ;
    }

    @SchemaMapping(typeName = "Civilization", field = "resources")
    public Map<String, Object> resources(Civilization civ) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("food", civ.getFood() != null ? civ.getFood() : 0.0);
        r.put("water", civ.getWater() != null ? civ.getWater() : 0.0);
        r.put("minerals", civ.getMinerals() != null ? civ.getMinerals() : 0.0);
        r.put("energy", civ.getEnergy() != null ? civ.getEnergy() : 0.0);
        r.put("housing", civ.getHousing() != null ? civ.getHousing() : 0.0);
        return r;
    }

    @SchemaMapping(typeName = "Civilization", field = "voxtexNodes")
    public List<?> voxtexNodes(Civilization civ) {
        return voxtexService.getNodesForCivilization(civ.getId());
    }

    @SchemaMapping(typeName = "Civilization", field = "homeRegion")
    public ResourceRegion homeRegion(Civilization civ) {
        if (civ.getHomeRegionId() != null) {
            return regionService.getRegion(civ.getHomeRegionId());
        }
        return null;
    }
}
