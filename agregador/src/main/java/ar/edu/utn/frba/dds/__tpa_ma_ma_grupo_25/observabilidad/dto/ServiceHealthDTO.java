package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO que representa el estado de salud de un microservicio
 */
public class ServiceHealthDTO {

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("status")
    private String status; // UP, DOWN, UNKNOWN

    @JsonProperty("response_time_ms")
    private Long responseTimeMs;

    @JsonProperty("last_check")
    private LocalDateTime lastCheck;

    @JsonProperty("url")
    private String url;

    @JsonProperty("error_message")
    private String errorMessage;

    public ServiceHealthDTO() {}

    public ServiceHealthDTO(String serviceName, String status, Long responseTimeMs, String url) {
        this.serviceName = serviceName;
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.url = url;
        this.lastCheck = LocalDateTime.now();
    }

    // Getters y Setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}


