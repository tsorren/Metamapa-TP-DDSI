/**
 * Sistema de Notificaciones Toast
 * Reemplazo moderno para alert() del navegador
 */

(function() {
    'use strict';

    // Evitar inicialización múltiple
    if (window.Toast) return;

    // Configuración por defecto
    const DEFAULT_CONFIG = {
        duration: 4000, // 4 segundos
        position: 'top-right',
        closeOnClick: true,
        pauseOnHover: true
    };

    // Iconos para cada tipo de notificación
    const ICONS = {
        success: '✓',
        error: '✗',
        warning: '⚠',
        info: 'ℹ'
    };

    /**
     * Clase principal del sistema Toast
     */
    class ToastSystem {
        constructor() {
            this.container = null;
            this.toasts = [];
            this.init();
        }

        /**
         * Inicializa el contenedor de notificaciones
         */
        init() {
            // Asegurar que el body existe
            if (!document.body) {
                console.warn('[Toast] Body no está disponible aún, esperando...');
                return;
            }

            // Crear contenedor si no existe
            if (!document.getElementById('toast-container')) {
                this.container = document.createElement('div');
                this.container.id = 'toast-container';
                this.container.className = 'toast-container';
                document.body.appendChild(this.container);
            } else {
                this.container = document.getElementById('toast-container');
            }
        }

        /**
         * Muestra una notificación
         * @param {string} message - Mensaje a mostrar
         * @param {string} type - Tipo: success, error, warning, info
         * @param {object} options - Opciones adicionales
         */
        show(message, type = 'info', options = {}) {
            // Re-inicializar el contenedor si no existe
            if (!this.container) {
                this.init();
            }

            // Si aún no existe después de init, el DOM no está listo
            if (!this.container) {
                console.error('[Toast] No se pudo crear el contenedor de notificaciones');
                return null;
            }

            const config = { ...DEFAULT_CONFIG, ...options };

            // Crear elemento toast
            const toast = this.createToast(message, type, config);

            // Agregar al contenedor
            this.container.appendChild(toast);
            this.toasts.push(toast);

            // Animar entrada
            requestAnimationFrame(() => {
                toast.style.opacity = '1';
            });

            // Configurar auto-cierre
            let timeoutId = null;
            let remainingTime = config.duration;
            let startTime = Date.now();

            const startTimer = () => {
                timeoutId = setTimeout(() => {
                    this.removeToast(toast);
                }, remainingTime);
            };

            const pauseTimer = () => {
                if (timeoutId) {
                    clearTimeout(timeoutId);
                    remainingTime -= Date.now() - startTime;
                }
            };

            const resumeTimer = () => {
                startTime = Date.now();
                startTimer();
            };

            // Pausar en hover si está habilitado
            if (config.pauseOnHover) {
                toast.addEventListener('mouseenter', pauseTimer);
                toast.addEventListener('mouseleave', resumeTimer);
            }

            // Cerrar al hacer click si está habilitado
            if (config.closeOnClick) {
                toast.addEventListener('click', (e) => {
                    if (!e.target.closest('.toast-close')) {
                        this.removeToast(toast);
                    }
                });
            }

            // Iniciar timer
            startTimer();

            return toast;
        }

        /**
         * Crea el elemento HTML del toast
         */
        createToast(message, type, config) {
            const toast = document.createElement('div');
            toast.className = `toast toast-${type}`;

            // Configurar duración de la barra de progreso
            toast.style.setProperty('--duration', `${config.duration}ms`);
            toast.querySelector = toast.querySelector || Element.prototype.querySelector;

            // Determinar si el mensaje tiene título y contenido
            let title, content;
            if (typeof message === 'object' && message.title) {
                title = message.title;
                content = message.message || '';
            } else {
                title = message;
                content = '';
            }

            // Construir HTML
            toast.innerHTML = `
                <div class="toast-icon">${ICONS[type]}</div>
                <div class="toast-content">
                    <div class="toast-title">${this.escapeHtml(title)}</div>
                    ${content ? `<div class="toast-message">${this.escapeHtml(content)}</div>` : ''}
                </div>
                <button class="toast-close" aria-label="Cerrar">&times;</button>
            `;

            // Agregar duración a la animación de la barra de progreso
            const style = document.createElement('style');
            style.textContent = `
                .toast::before {
                    animation-duration: ${config.duration}ms;
                }
            `;
            toast.appendChild(style);

            // Evento de cerrar
            const closeBtn = toast.querySelector('.toast-close');
            closeBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.removeToast(toast);
            });

            return toast;
        }

        /**
         * Remueve un toast con animación
         */
        removeToast(toast) {
            toast.classList.add('toast-exit');

            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
                const index = this.toasts.indexOf(toast);
                if (index > -1) {
                    this.toasts.splice(index, 1);
                }
            }, 300); // Duración de la animación de salida
        }

        /**
         * Escapa HTML para prevenir XSS
         */
        escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        /**
         * Métodos de conveniencia
         */
        success(message, options) {
            return this.show(message, 'success', options);
        }

        error(message, options) {
            return this.show(message, 'error', options);
        }

        warning(message, options) {
            return this.show(message, 'warning', options);
        }

        info(message, options) {
            return this.show(message, 'info', options);
        }

        /**
         * Limpia todas las notificaciones
         */
        clear() {
            this.toasts.forEach(toast => this.removeToast(toast));
        }
    }

    // Crear instancia global cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            window.Toast = new ToastSystem();
        });
    } else {
        // El DOM ya está listo
        window.Toast = new ToastSystem();
    }

    // Alias para compatibilidad con alert()
    // Puedes reemplazar alert() globalmente si lo deseas (descomentar la línea siguiente)
    // window.alert = (message) => Toast.info(message);

})();

