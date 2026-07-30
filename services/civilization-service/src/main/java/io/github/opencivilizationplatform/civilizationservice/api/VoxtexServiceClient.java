package io.github.opencivilizationplatform.civilizationservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "voxtex-service", url = "${services.voxtex.url:http://localhost:8082}")
public interface VoxtexServiceClient {

    @PostMapping("/api/v1/voxtex/nodes")
    Map<String, Object> registerNode(@RequestBody Map<String, Object> request);

    @GetMapping("/api/v1/voxtex/nodes/civilization/{civId}")
    List<Map<String, Object>> getNodesByCivilization(@PathVariable("civId") Long civId);
}
