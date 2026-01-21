/**
 * Observabilidad Frontend Logger
 * Sistema de logging estandarizado para el frontend siguiendo el estándar RFC 5424
 */

class FrontendLogger {
    constructor(serviceName = 'observabilidad-frontend') {
        this.serviceName = serviceName;
        this.sessionId = this.generateSessionId();
    }

    generateSessionId() {
        return `session-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    }

    /**
     * Genera un log estructurado en formato JSON siguiendo estándares
     * @param {string} level - Nivel del log (ERROR, WARN, INFO, DEBUG)
     * @param {string} message - Mensaje del log
     * @param {object} context - Contexto adicional
     */
    log(level, message, context = {}) {
        const logEntry = {
            '@timestamp': new Date().toISOString(),
            level: level,
            service: this.serviceName,
            message: message,
            sessionId: this.sessionId,
            userAgent: navigator.userAgent,
            url: window.location.href,
            ...context
        };

        // Log en consola con formato apropiado
        const consoleMethod = {
            'ERROR': 'error',
            'WARN': 'warn',
            'INFO': 'info',
            'DEBUG': 'debug'
        }[level] || 'log';

        console[consoleMethod](`[${level}] ${this.serviceName}:`, message, context);

        // Enviar al backend si está configurado (opcional)
        this.sendToBackend(logEntry);

        return logEntry;
    }

    error(message, context = {}) {
        return this.log('ERROR', message, context);
    }

    warn(message, context = {}) {
        return this.log('WARN', message, context);
    }

    info(message, context = {}) {
        return this.log('INFO', message, context);
    }

    debug(message, context = {}) {
        return this.log('DEBUG', message, context);
    }

    /**
     * Envía logs al backend (opcional)
     */
    sendToBackend(logEntry) {
        // Implementar si se necesita persistencia en backend
        // fetch('/api/logs/frontend', {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify(logEntry)
        // }).catch(() => {}); // Silent fail
    }

    /**
     * Captura errores globales de JavaScript
     */
    setupGlobalErrorHandler() {
        window.addEventListener('error', (event) => {
            this.error('Uncaught JavaScript Error', {
                message: event.message,
                filename: event.filename,
                lineno: event.lineno,
                colno: event.colno,
                error: event.error?.stack
            });
        });

        window.addEventListener('unhandledrejection', (event) => {
            this.error('Unhandled Promise Rejection', {
                reason: event.reason,
                promise: event.promise
            });
        });
    }
}

// Inicializar logger global
const logger = new FrontendLogger('observabilidad-dashboard');
logger.setupGlobalErrorHandler();

/**
 * Configuración del Dashboard de Observabilidad
 */
const DashboardConfig = {
    API_BASE_URL: '/api/observabilidad', // Ruta relativa para evitar CORS
    REFRESH_INTERVAL: 10000, // 10 segundos
    MAX_LOGS: 50
};

/**
 * Estado global del dashboard
 */
const DashboardState = {
    currentLogFilter: 'all',
    isLoading: false,
    lastUpdate: null,
    refreshTimer: null
};

/**
 * Inicialización del Dashboard
 */
document.addEventListener('DOMContentLoaded', () => {
    logger.info('Dashboard de observabilidad inicializado');

    setupEventListeners();
    loadDashboardData();
    startAutoRefresh();
});

/**
 * Configurar event listeners
 */
function setupEventListeners() {
    // Botón de refresh manual
    const refreshBtn = document.getElementById('refresh-btn');
    refreshBtn.addEventListener('click', () => {
        logger.info('Refresh manual solicitado por usuario');
        loadDashboardData();
    });

    // Filtros de logs
    const filterButtons = document.querySelectorAll('.filter-btn');
    filterButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const level = btn.dataset.level;
            logger.debug('Filtro de logs cambiado', { level });

            filterButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            DashboardState.currentLogFilter = level;
            filterLogs(level);
        });
    });
}

/**
 * Cargar datos del dashboard
 */
async function loadDashboardData() {
    if (DashboardState.isLoading) {
        logger.debug('Carga ya en progreso, ignorando solicitud');
        return;
    }

    DashboardState.isLoading = true;
    const startTime = performance.now();

    try {
        logger.info('Iniciando carga de datos del dashboard');

        // Añadir animación al botón de refresh
        const refreshBtn = document.getElementById('refresh-btn');
        refreshBtn.classList.add('spinning');

        const response = await fetch(`${DashboardConfig.API_BASE_URL}/dashboard`);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const data = await response.json();

        const duration = performance.now() - startTime;
        logger.info('Datos del dashboard cargados exitosamente', {
            duration_ms: Math.round(duration),
            services_count: data.services_health?.length || 0
        });

        // Actualizar UI
        updateSummary(data.system_summary);
        updateServicesHealth(data.services_health);
        updateServicesMetrics(data.services_metrics);
        updateLogs(data.recent_logs);

        DashboardState.lastUpdate = new Date();
        clearError();

    } catch (error) {
        const duration = performance.now() - startTime;
        logger.error('Error al cargar datos del dashboard', {
            error: error.message,
            duration_ms: Math.round(duration),
            api_url: DashboardConfig.API_BASE_URL
        });

        showError(`Error al cargar datos: ${error.message}`);
    } finally {
        DashboardState.isLoading = false;

        const refreshBtn = document.getElementById('refresh-btn');
        refreshBtn.classList.remove('spinning');
    }
}

/**
 * Actualizar resumen del sistema
 */
function updateSummary(summary) {
    if (!summary) {
        logger.warn('Resumen del sistema no disponible');
        return;
    }

    logger.debug('Actualizando resumen del sistema', summary);

    const container = document.getElementById('summary-stats');
    container.innerHTML = `
        <div class="stat">
            <span class="stat-value">${summary.services_up || 0}/${summary.total_services || 0}</span>
            <span class="stat-label">Servicios Activos</span>
        </div>
        <div class="stat">
            <span class="stat-value">${formatNumber(summary.total_requests || 0)}</span>
            <span class="stat-label">Total Requests</span>
        </div>
        <div class="stat">
            <span class="stat-value">${summary.avg_response_time_ms?.toFixed(0) || 0}ms</span>
            <span class="stat-label">Tiempo Promedio</span>
        </div>
        <div class="stat">
            <span class="stat-value">${summary.error_rate_percent?.toFixed(2) || 0}%</span>
            <span class="stat-label">Tasa de Errores</span>
        </div>
    `;
}

/**
 * Actualizar estado de servicios
 */
function updateServicesHealth(services) {
    if (!services || services.length === 0) {
        logger.warn('Estado de servicios no disponible');
        return;
    }

    logger.debug('Actualizando estado de servicios', { count: services.length });

    const container = document.getElementById('services-health');
    container.innerHTML = services.map(service => {
        const statusClass = service.status === 'UP' ? 'status-up' :
                           service.status === 'DOWN' ? 'status-down' : 'status-unknown';

        return `
            <div class="service-item">
                <div class="service-name">${service.service_name}</div>
                <div class="service-status">
                    <span class="response-time">${service.response_time_ms}ms</span>
                    <span class="status-badge ${statusClass}">${service.status}</span>
                </div>
            </div>
        `;
    }).join('');
}

/**
 * Actualizar métricas de servicios
 */
function updateServicesMetrics(metrics) {
    if (!metrics || metrics.length === 0) {
        logger.warn('Métricas de servicios no disponibles');
        return;
    }

    logger.debug('Actualizando métricas de servicios', { count: metrics.length });

    const container = document.getElementById('services-metrics');
    container.innerHTML = metrics.map(metric => `
        <div style="margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;">
            <div style="font-weight: bold; margin-bottom: 10px; color: #667eea;">
                ${metric.service_name}
            </div>
            <div class="metrics-grid">
                <div class="metric-item">
                    <span class="metric-label">Requests:</span>
                    <span class="metric-value">${formatNumber(metric.total_requests || 0)}</span>
                </div>
                <div class="metric-item">
                    <span class="metric-label">Tiempo Promedio:</span>
                    <span class="metric-value">${metric.avg_response_time_ms?.toFixed(2) || 0}ms</span>
                </div>
                <div class="metric-item">
                    <span class="metric-label">Memoria:</span>
                    <span class="metric-value">${metric.memory_used_mb?.toFixed(0) || 0}MB</span>
                </div>
            </div>
        </div>
    `).join('');
}

/**
 * Actualizar logs del sistema
 */
function updateLogs(logs) {
    if (!logs || logs.length === 0) {
        logger.warn('Logs no disponibles');
        document.getElementById('logs-container').innerHTML =
            '<div class="loading">No hay logs disponibles</div>';
        return;
    }

    logger.debug('Actualizando logs', { count: logs.length });

    const container = document.getElementById('logs-container');

    // Guardar logs en el contenedor para filtrado
    container.dataset.allLogs = JSON.stringify(logs);

    // Mostrar logs filtrados
    filterLogs(DashboardState.currentLogFilter);
}

/**
 * Filtrar logs por nivel
 */
function filterLogs(level) {
    const container = document.getElementById('logs-container');
    const allLogsData = container.dataset.allLogs;

    if (!allLogsData) {
        return;
    }

    const logs = JSON.parse(allLogsData);
    const filteredLogs = level === 'all' ? logs : logs.filter(log => log.level === level);

    logger.debug('Filtrando logs', { level, total: logs.length, filtered: filteredLogs.length });

    if (filteredLogs.length === 0) {
        container.innerHTML = '<div class="loading">No hay logs para este filtro</div>';
        return;
    }

    container.innerHTML = filteredLogs.map(log => {
        const levelClass = `log-${log.level.toLowerCase()}`;
        const timestamp = formatTimestamp(log.timestamp);

        return `
            <div class="log-entry ${levelClass}">
                <span class="log-timestamp">${timestamp}</span>
                <span class="log-service">${log.service}</span>
                <span class="log-level">[${log.level}]</span>
                <span>${log.message}</span>
                ${log.endpoint ? `<br><small style="margin-left: 120px;">→ ${log.http_method} ${log.endpoint} (${log.duration_ms}ms)</small>` : ''}
            </div>
        `;
    }).join('');
}

/**
 * Iniciar actualización automática
 */
function startAutoRefresh() {
    logger.info('Iniciando actualización automática', {
        interval_ms: DashboardConfig.REFRESH_INTERVAL
    });

    DashboardState.refreshTimer = setInterval(() => {
        logger.debug('Ejecutando actualización automática');
        loadDashboardData();
    }, DashboardConfig.REFRESH_INTERVAL);
}

/**
 * Mostrar error
 */
function showError(message) {
    const container = document.getElementById('error-container');
    container.innerHTML = `<div class="error-message">⚠️ ${message}</div>`;

    setTimeout(() => {
        container.innerHTML = '';
    }, 5000);
}

/**
 * Limpiar error
 */
function clearError() {
    document.getElementById('error-container').innerHTML = '';
}

/**
 * Formatear número con separadores de miles
 */
function formatNumber(num) {
    return num.toLocaleString('es-AR');
}

/**
 * Formatear timestamp
 */
function formatTimestamp(timestamp) {
    if (!timestamp) return '';

    const date = new Date(timestamp);
    return date.toLocaleString('es-AR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// Cleanup al cerrar la página
window.addEventListener('beforeunload', () => {
    if (DashboardState.refreshTimer) {
        clearInterval(DashboardState.refreshTimer);
    }
    logger.info('Dashboard cerrado');
});

