package io.github.opencivilizationplatform.modules.nexus.api;

import io.github.opencivilizationplatform.modules.nexus.application.MigrationService;
import io.github.opencivilizationplatform.modules.nexus.domain.MigrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/migrations")
@Tag(name = "Migrations", description = "Cross-civilization citizen migration flow endpoints")
public class MigrationController {

    private final MigrationService migrationService;

    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply for a migration to another civilization")
    public MigrationRequest apply(@Valid @RequestBody ApplyMigrationRequest request) {
        return migrationService.applyMigration(
            request.citizenName(), request.fromCivilizationId(),
            request.toCivilizationId(), request.reason()
        );
    }

    @GetMapping("/pending/{civId}")
    @Operation(summary = "List pending migration requests for a civilization")
    public List<MigrationRequest> getPending(@PathVariable Long civId) {
        return migrationService.listPending(civId);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a migration request")
    public Map<String, Object> approve(@PathVariable Long id) {
        migrationService.approveMigration(id);
        return Map.of("success", true, "message", "Migração aprovada com sucesso!");
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a migration request")
    public Map<String, Object> reject(@PathVariable Long id) {
        migrationService.rejectMigration(id);
        return Map.of("success", true, "message", "Migração rejeitada!");
    }
}

record ApplyMigrationRequest(
    @NotBlank String citizenName,
    @NotNull Long fromCivilizationId,
    @NotNull Long toCivilizationId,
    String reason
) {}
