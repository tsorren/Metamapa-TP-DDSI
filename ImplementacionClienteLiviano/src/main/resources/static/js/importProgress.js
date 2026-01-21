/**
 * Sistema de seguimiento de progreso para importación de CSV
 */

class ImportProgressTracker {
    constructor() {
        // console.log('[ProgressTracker] Constructor llamado');
        this.fuenteId = null;
        this.intervalId = null;
        this.progressBar = null;
        this.progressText = null;
        this.progressContainer = null;
    }

    /**
     * Inicializa el tracker con el ID de la fuente
     */
    iniciar(fuenteId) {
        // console.log('[ProgressTracker] Iniciando con fuenteId:', fuenteId);
        this.fuenteId = fuenteId;

        // Solo crear UI si no existe
        if (!this.progressContainer || this.progressContainer.style.display === 'none') {
            this.crearUI();
        }

        this.iniciarPolling();
    }

    /**
     * Crea la interfaz de la barra de progreso
     */
    crearUI() {
        // console.log('[ProgressTracker] Creando UI');

        // Buscar si ya existe el contenedor
        this.progressContainer = document.getElementById('import-progress-container');

        if (!this.progressContainer) {
            // console.log('[ProgressTracker] Creando nuevo contenedor');
            // Crear contenedor si no existe
            this.progressContainer = document.createElement('div');
            this.progressContainer.id = 'import-progress-container';
            this.progressContainer.className = 'import-progress-container';
            this.progressContainer.innerHTML = `
                <div class="import-progress-content">
                    <h3>⏳ Importando hechos...</h3>
                    <div class="progress-bar-container">
                        <div class="progress-bar-fill" id="progress-bar-fill"></div>
                    </div>
                    <p class="progress-text" id="progress-text">Iniciando importación...</p>
                </div>
            `;
            document.body.appendChild(this.progressContainer);
            // console.log('[ProgressTracker] Contenedor agregado al DOM');
        } else {
            // console.log('[ProgressTracker] Contenedor ya existe, reutilizando');
        }

        this.progressBar = document.getElementById('progress-bar-fill');
        this.progressText = document.getElementById('progress-text');
        this.progressContainer.style.display = 'flex';

        // console.log('[ProgressTracker] UI creada exitosamente');
        // console.log('[ProgressTracker] progressBar:', this.progressBar);
        // console.log('[ProgressTracker] progressText:', this.progressText);
    }

    /**
     * Inicia el polling para consultar el progreso
     */
    iniciarPolling() {
        // console.log('[ProgressTracker] Iniciando polling cada 500ms');

        // Consultar inmediatamente
        this.consultarProgreso();

        // Consultar cada 500ms
        this.intervalId = setInterval(() => {
            this.consultarProgreso();
        }, 500);

        // console.log('[ProgressTracker] Polling iniciado, intervalId:', this.intervalId);
    }

    /**
     * Consulta el progreso de la importación
     */
    async consultarProgreso() {
        try {
            const url = `/admin/importar/progreso/${this.fuenteId}`;
            // console.log('[ProgressTracker] Consultando progreso:', url);

            const response = await fetch(url);
            // console.log('[ProgressTracker] Respuesta recibida, status:', response.status);

            if (!response.ok) {
                // console.warn('[ProgressTracker] Respuesta no OK:', response.status);
                return;
            }

            const progreso = await response.json();
            // console.log('[ProgressTracker] Progreso:', progreso);

            this.actualizarUI(progreso);

            // Si terminó (éxito o error), detener el polling
            if (!progreso.enProgreso) {
                // console.log('[ProgressTracker] Importación finalizada');
                this.finalizar(progreso);
            }
        } catch (error) {
            // console.error('[ProgressTracker] Error al consultar progreso:', error);
        }
    }

    /**
     * Actualiza la interfaz con el progreso actual
     */
    actualizarUI(progreso) {
        const porcentaje = progreso.porcentaje || 0;
        const procesados = progreso.procesados || 0;
        const total = progreso.totalHechos || 0;
        const errores = progreso.errores || 0;

        // console.log('[ProgressTracker] Actualizando UI:', {
        //     porcentaje,
        //     procesados,
        //     total,
        //     errores
        // });

        // Actualizar barra de progreso
        if (this.progressBar) {
            this.progressBar.style.width = `${porcentaje}%`;
            // console.log('[ProgressTracker] Barra actualizada a:', porcentaje + '%');
        } else {
            // console.error('[ProgressTracker] progressBar no disponible!');
        }

        // Actualizar texto con iconos visuales
        if (this.progressText) {
            let texto = `📊 Procesados: ${procesados.toLocaleString()} / ${total.toLocaleString()} (${porcentaje}%)`;
            if (errores > 0) {
                texto += ` ⚠️ Errores: ${errores}`;
            }
            this.progressText.textContent = texto;
            // console.log('[ProgressTracker] Texto actualizado:', texto);
        } else {
            // console.error('[ProgressTracker] progressText no disponible!');
        }

        // Actualizar título según el progreso
        const titleElement = this.progressContainer?.querySelector('h3');
        if (titleElement) {
            if (porcentaje < 30) {
                titleElement.textContent = '⏳ Iniciando importación...';
            } else if (porcentaje < 70) {
                titleElement.textContent = '🔄 Procesando hechos...';
            } else if (porcentaje < 100) {
                titleElement.textContent = '⚡ Finalizando importación...';
            }
        }
    }

    /**
     * Finaliza el tracking y muestra el resultado
     */
    finalizar(progreso) {
        // console.log('[ProgressTracker] Finalizando con progreso:', progreso);

        // Detener polling
        if (this.intervalId) {
            clearInterval(this.intervalId);
            // console.log('[ProgressTracker] Polling detenido');
            this.intervalId = null;
        }

        // Mostrar resultado con toast
        if (window.Toast) {
            if (progreso.exitoso) {
                // console.log('[ProgressTracker] Mostrando toast de éxito');
                window.Toast.success(progreso.mensaje || 'Importación completada', {
                    duration: 5000
                });
            } else {
                // console.log('[ProgressTracker] Mostrando toast de error');
                window.Toast.error(progreso.mensaje || 'Error en la importación', {
                    duration: 6000
                });
            }
        } else {
            // console.error('[ProgressTracker] Toast no disponible!');
        }

        // Ocultar la barra de progreso después de 2 segundos
        setTimeout(() => {
            // console.log('[ProgressTracker] Ocultando barra y recargando página');
            if (this.progressContainer) {
                this.progressContainer.style.display = 'none';
            }

            // Recargar la página para actualizar la vista
            window.location.reload();
        }, 2000);
    }

    /**
     * Limpia y elimina el tracker
     */
    limpiar() {
        // console.log('[ProgressTracker] Limpiando tracker');
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
        if (this.progressContainer) {
            this.progressContainer.remove();
        }
    }
}

// Exponer globalmente
window.ImportProgressTracker = ImportProgressTracker;
// console.log('[ProgressTracker] Clase exportada globalmente');

