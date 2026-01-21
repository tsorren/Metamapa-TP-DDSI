package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO que representa una entrada de log estructurado
 */
public class LogEntryDTO {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("level")
    private String level; // ERROR, WARN, INFO, DEBUG

    @JsonProperty("service")
    private String service;

    @JsonProperty("message")
    private String message;

    @JsonProperty("correlation_id")
    private String correlationId;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("http_method")
    private String httpMethod;

    @JsonProperty("http_status")
    private Integer httpStatus;

    @JsonProperty("duration_ms")
    private Long durationMs;

    @JsonProperty("error_details")
    private String errorDetails;

    public LogEntryDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public LogEntryDTO(String level, String service, String message) {
        this.timestamp = LocalDateTime.now();
        this.level = level;
        this.service = service;
        this.message = message;
    }

    // Getters y Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
}

