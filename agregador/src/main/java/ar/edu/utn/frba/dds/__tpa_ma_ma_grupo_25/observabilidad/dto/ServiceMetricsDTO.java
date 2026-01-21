package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que representa las métricas de un microservicio
 */
public class ServiceMetricsDTO {

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("total_requests")
    private Long totalRequests;

    @JsonProperty("avg_response_time_ms")
    private Double avgResponseTimeMs;

    @JsonProperty("error_rate")
    private Double errorRate;

    @JsonProperty("requests_per_minute")
    private Double requestsPerMinute;

    @JsonProperty("memory_used_mb")
    private Double memoryUsedMb;

    @JsonProperty("cpu_usage_percent")
    private Double cpuUsagePercent;

    public ServiceMetricsDTO() {}

    public ServiceMetricsDTO(String serviceName) {
        this.serviceName = serviceName;
    }

    // Getters y Setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Double getAvgResponseTimeMs() {
        return avgResponseTimeMs;
    }

    public void setAvgResponseTimeMs(Double avgResponseTimeMs) {
        this.avgResponseTimeMs = avgResponseTimeMs;
    }

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }

    public Double getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(Double requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public Double getMemoryUsedMb() {
        return memoryUsedMb;
    }

    public void setMemoryUsedMb(Double memoryUsedMb) {
        this.memoryUsedMb = memoryUsedMb;
    }

    public Double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(Double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }
}

