package io.github.opencivilizationplatform.multitenancy;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@MappedSuperclass
public abstract class TenantAware {

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId = TenantFilter.DEFAULT_TENANT;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @PrePersist
    @PreUpdate
    public void enforceTenant() {
        String current = TenantContext.getTenantId();
        if (current != null) {
            this.tenantId = current;
        }
    }
}
