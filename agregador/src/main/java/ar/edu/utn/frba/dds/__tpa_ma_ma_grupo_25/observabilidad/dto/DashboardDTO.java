package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO que representa el dashboard completo de observabilidad
 */
public class DashboardDTO {

    @JsonProperty("services_health")
    private List<ServiceHealthDTO> servicesHealth;

    @JsonProperty("services_metrics")
    private List<ServiceMetricsDTO> servicesMetrics;

    @JsonProperty("recent_logs")
    private List<LogEntryDTO> recentLogs;

    @JsonProperty("system_summary")
    private SystemSummaryDTO systemSummary;

    public DashboardDTO() {}

    public DashboardDTO(List<ServiceHealthDTO> servicesHealth,
                       List<ServiceMetricsDTO> servicesMetrics,
                       List<LogEntryDTO> recentLogs,
                       SystemSummaryDTO systemSummary) {
        this.servicesHealth = servicesHealth;
        this.servicesMetrics = servicesMetrics;
        this.recentLogs = recentLogs;
        this.systemSummary = systemSummary;
    }

    // Getters y Setters
    public List<ServiceHealthDTO> getServicesHealth() {
        return servicesHealth;
    }

    public void setServicesHealth(List<ServiceHealthDTO> servicesHealth) {
        this.servicesHealth = servicesHealth;
    }

    public List<ServiceMetricsDTO> getServicesMetrics() {
        return servicesMetrics;
    }

    public void setServicesMetrics(List<ServiceMetricsDTO> servicesMetrics) {
        this.servicesMetrics = servicesMetrics;
    }

    public List<LogEntryDTO> getRecentLogs() {
        return recentLogs;
    }

    public void setRecentLogs(List<LogEntryDTO> recentLogs) {
        this.recentLogs = recentLogs;
    }

    public SystemSummaryDTO getSystemSummary() {
        return systemSummary;
    }

    public void setSystemSummary(SystemSummaryDTO systemSummary) {
        this.systemSummary = systemSummary;
    }
}

