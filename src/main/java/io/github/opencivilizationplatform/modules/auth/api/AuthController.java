package io.github.opencivilizationplatform.modules.auth.api;

import io.github.opencivilizationplatform.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Client authentication endpoints")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/connect")
    @Operation(summary = "Connect and get a JWT token", description = "Returns a JWT token for the client")
    public Map<String, Object> connect(HttpServletRequest request) {
        String clientId = request.getRemoteAddr() + ":" + UUID.randomUUID().toString().substring(0, 8);
        String token = jwtService.generateToken(clientId);
        return Map.of(
            "token", token,
            "clientId", clientId,
            "expiresIn", "86400000"
        );
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate the current token")
    public Map<String, Object> validate(HttpServletRequest request) {
        String clientId = (String) request.getAttribute("X-Client-Id");
        if (clientId == null) {
            clientId = "anonymous";
        }
        return Map.of("valid", true, "clientId", clientId);
    }
}
