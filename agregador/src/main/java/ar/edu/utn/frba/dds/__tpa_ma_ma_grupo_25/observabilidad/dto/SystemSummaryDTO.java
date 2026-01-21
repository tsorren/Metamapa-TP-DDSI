package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que representa un resumen del sistema completo
 */
public class SystemSummaryDTO {

    @JsonProperty("total_services")
    private Integer totalServices;

    @JsonProperty("services_up")
    private Integer servicesUp;

    @JsonProperty("services_down")
    private Integer servicesDown;

    @JsonProperty("total_requests")
    private Long totalRequests;

    @JsonProperty("total_errors")
    private Long totalErrors;

    @JsonProperty("error_rate_percent")
    private Double errorRatePercent;

    @JsonProperty("avg_response_time_ms")
    private Double avgResponseTimeMs;

    public SystemSummaryDTO() {}

    // Getters y Setters
    public Integer getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(Integer totalServices) {
        this.totalServices = totalServices;
    }

    public Integer getServicesUp() {
        return servicesUp;
    }

    public void setServicesUp(Integer servicesUp) {
        this.servicesUp = servicesUp;
    }

    public Integer getServicesDown() {
        return servicesDown;
    }

    public void setServicesDown(Integer servicesDown) {
        this.servicesDown = servicesDown;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Long getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(Long totalErrors) {
        this.totalErrors = totalErrors;
    }

    public Double getErrorRatePercent() {
        return errorRatePercent;
    }

    public void setErrorRatePercent(Double errorRatePercent) {
        this.errorRatePercent = errorRatePercent;
    }

    public Double getAvgResponseTimeMs() {
        return avgResponseTimeMs;
    }

    public void setAvgResponseTimeMs(Double avgResponseTimeMs) {
        this.avgResponseTimeMs = avgResponseTimeMs;
    }
}

