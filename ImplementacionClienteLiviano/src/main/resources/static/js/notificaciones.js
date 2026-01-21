/**
 * Sistema de Notificaciones
 * Maneja la campanita de notificaciones y el badge contador
 */

// console.log para producción comentado
// console.log('[Notificaciones] 📦 Archivo notificaciones.js cargado');

// Variable global para control del interval
let notificacionesInterval = null;
let notificacionesInicializado = false;

// Handlers globales para poder removerlos
let bellClickHandler = null;
let dropdownClickHandler = null;
let documentClickHandler = null;
let documentKeydownHandler = null;

// Función de inicialización
function inicializarNotificaciones() {
    // Evitar inicialización múltiple
    if (notificacionesInicializado) {
        // console.log('[Notificaciones] Ya inicializado, omitiendo...');
        return;
    }

    // console.log('[Notificaciones] DOM Cargado, iniciando...');

    const notificationsContainer = document.querySelector('.notifications');
    const bellBtn = document.querySelector('.bell-btn');
    const dropdown = document.querySelector('.notifications-dropdown');

    // console.log('[Notificaciones] Elementos encontrados:', {
    //     notificationsContainer: !!notificationsContainer,
    //     bellBtn: !!bellBtn,
    //     dropdown: !!dropdown
    // });

    if (!notificationsContainer || !bellBtn || !dropdown) {
        // console.error('[Notificaciones] Elementos no encontrados en esta página');
        return;
    }

    // console.log('[Notificaciones] ✓ Inicializando sistema de notificaciones');

    // Toggle del dropdown al hacer click en la campanita
    bellClickHandler = function (e) {
        // console.log('[Notificaciones] 🔔 Click en campanita detectado');
        e.preventDefault();
        e.stopPropagation();

        const isOpen = notificationsContainer.classList.contains('show');
        // console.log('[Notificaciones] Estado actual:', isOpen ? 'Abierto' : 'Cerrado');

        if (isOpen) {
            // console.log('[Notificaciones] Cerrando dropdown...');
            notificationsContainer.classList.remove('show');
        } else {
            // console.log('[Notificaciones] Abriendo dropdown...');
            notificationsContainer.classList.add('show');
            // console.log('[Notificaciones] Clase .show agregada, classList:', notificationsContainer.classList.toString());
            cargarNotificaciones();
        }
    };
    bellBtn.addEventListener('click', bellClickHandler);

    // Evitar que el dropdown se cierre al hacer click dentro de él
    dropdownClickHandler = function (e) {
        // console.log('[Notificaciones] Click dentro del dropdown');
        e.stopPropagation();
    };
    dropdown.addEventListener('click', dropdownClickHandler);

    // Cerrar dropdown al hacer click fuera de él
    documentClickHandler = function (e) {
        if (!notificationsContainer.contains(e.target)) {
            notificationsContainer.classList.remove('show');
        }
    };
    document.addEventListener('click', documentClickHandler);

    // Cerrar dropdown con tecla Escape
    documentKeydownHandler = function (e) {
        if (e.key === 'Escape') {
            // console.log('[Notificaciones] Tecla ESC presionada, cerrando...');
            notificationsContainer.classList.remove('show');
        }
    };
    document.addEventListener('keydown', documentKeydownHandler);

    // Cargar contador inicial
    // console.log('[Notificaciones] Cargando contador inicial...');
    actualizarContadorNotificaciones();

    // Limpiar interval anterior si existe
    if (notificacionesInterval) {
        clearInterval(notificacionesInterval);
    }

    // Actualizar contador cada 30 segundos
    notificacionesInterval = setInterval(actualizarContadorNotificaciones, 30000);

    // Marcar como inicializado
    notificacionesInicializado = true;

    // console.log('[Notificaciones] ✅ Sistema inicializado correctamente');
}

// Función para limpiar recursos (llamar antes de navegar a otra página)
function limpiarNotificaciones() {
    // console.log('[Notificaciones] Limpiando recursos...');

    if (notificacionesInterval) {
        clearInterval(notificacionesInterval);
        notificacionesInterval = null;
    }

    const bellBtn = document.querySelector('.bell-btn');
    const dropdown = document.querySelector('.notifications-dropdown');

    if (bellBtn && bellClickHandler) {
        bellBtn.removeEventListener('click', bellClickHandler);
    }
    if (dropdown && dropdownClickHandler) {
        dropdown.removeEventListener('click', dropdownClickHandler);
    }
    if (documentClickHandler) {
        document.removeEventListener('click', documentClickHandler);
    }
    if (documentKeydownHandler) {
        document.removeEventListener('keydown', documentKeydownHandler);
    }

    notificacionesInicializado = false;
    // console.log('[Notificaciones] ✓ Recursos limpiados');
}

// Limpiar recursos al salir de la página
window.addEventListener('beforeunload', limpiarNotificaciones);

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializarNotificaciones);
} else {
    // DOM ya está listo, ejecutar inmediatamente
    inicializarNotificaciones();
}

/**
 * Actualiza el contador de notificaciones no leídas
 */
async function actualizarContadorNotificaciones() {
    try {
        const response = await fetch('/notificaciones/contador', {
            credentials: 'include'
        });

        if (!response.ok) {
            // Si es 401, el usuario no está logueado, simplemente no hacer nada
            if (response.status === 401) {
                // console.log('[Notificaciones] Usuario no autenticado');
                const notificationsContainer = document.querySelector('.notifications');
                if (notificationsContainer) {
                    notificationsContainer.style.display = 'none';
                }
            }
            return;
        }

        const data = await response.json();
        const badge = document.querySelector('.notifications-badge');

        if (badge) {
            if (data.count > 0) {
                badge.textContent = data.count > 99 ? '99+' : data.count;
                badge.style.display = 'flex';
            } else {
                badge.style.display = 'none';
            }
        }
    } catch (error) {
        // console.error('[Notificaciones] Error al actualizar contador:', error);
    }
}

/**
 * Carga las notificaciones no leídas en el dropdown
 */
async function cargarNotificaciones() {
    const lista = document.querySelector('.notifications-list');

    if (!lista) return;

    // Mostrar loading
    lista.innerHTML = '<div class="notification-item loading">Cargando...</div>';

    try {
        const response = await fetch('/notificaciones/no-leidas', {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Error al cargar notificaciones');
        }

        const data = await response.json();
        const notificaciones = data.notificaciones || [];

        if (notificaciones.length === 0) {
            lista.innerHTML = `
                <div class="notification-item empty">
                    <div>
                        <div style="font-size: 32px; margin-bottom: 8px;">🔔</div>
                        <div style="color: #666;">No hay notificaciones nuevas</div>
                    </div>
                </div>
            `;
            return;
        }

        // Renderizar notificaciones
        lista.innerHTML = notificaciones.map(notif => crearNotificacionHTML(notif)).join('');

        // Agregar event listeners usando delegación de eventos (más eficiente)
        lista.addEventListener('click', function(e) {
            const actionBtn = e.target.closest('.notification-action');
            if (actionBtn) {
                e.stopPropagation();
                const item = actionBtn.closest('.notification-item');
                if (item) {
                    const notifId = item.dataset.id;
                    const tipoSolicitud = item.dataset.tipo;
                    marcarComoLeida(notifId, tipoSolicitud);
                }
            }
        });

    } catch (error) {
        // console.error('[Notificaciones] Error al cargar:', error);
        lista.innerHTML = `
            <div class="notification-item empty">
                <div style="color: #dc3545;">
                    <div style="font-size: 32px; margin-bottom: 8px;">⚠️</div>
                    <div>Error al cargar notificaciones</div>
                </div>
            </div>
        `;
    }
}

/**
 * Crea el HTML de una notificación
 */
function crearNotificacionHTML(notif) {
    const fecha = new Date(notif.fechaCreacion);
    const tiempoFormateado = formatearTiempo(fecha);

    // Determinar tipo e icono
    let tipo = 'info';
    let icono = 'ℹ️';

    const mensaje = notif.mensaje.toLowerCase();
    if (mensaje.includes('aprobad')) {
        tipo = 'success';
        icono = '✅';
    } else if (mensaje.includes('rechazad')) {
        tipo = 'danger';
        icono = '❌';
    } else if (mensaje.includes('pendiente')) {
        tipo = 'warning';
        icono = '⏳';
    }

    return `
        <div class="notification-item ${tipo}" 
             data-id="${notif.id}"
             data-tipo="${notif.tipoSolicitud || ''}"
             data-solicitud-id="${notif.solicitudId || ''}"
             data-tipo-solicitud="${notif.tipoSolicitud || ''}">
            <div class="notification-icon">${icono}</div>
            <div class="notification-content">
                <div class="notification-message">${notif.mensaje}</div>
                <div class="notification-time">${tiempoFormateado}</div>
            </div>
            <button class="notification-action" title="Marcar como leída">✓</button>
        </div>
    `;
}

/**
 * Formatea el tiempo transcurrido
 */
function formatearTiempo(fecha) {
    const ahora = new Date();
    const diff = Math.floor((ahora - fecha) / 1000);

    if (diff < 60) return 'Hace un momento';
    if (diff < 3600) return `Hace ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `Hace ${Math.floor(diff / 3600)} h`;
    if (diff < 604800) return `Hace ${Math.floor(diff / 86400)} días`;

    return fecha.toLocaleDateString('es-ES', {
        day: 'numeric',
        month: 'short'
    });
}

/**
 * Marca una notificación como leída
 */
async function marcarComoLeida(notifId, tipoSolicitud) {
    try {
        const response = await fetch(`/notificaciones/${notifId}/marcar-leida?tipoSolicitud=${tipoSolicitud}`, {
            method: 'PATCH',
            credentials: 'include'
        });

        if (response.ok) {
            // Recargar notificaciones y actualizar contador
            await cargarNotificaciones();
            await actualizarContadorNotificaciones();
        }
    } catch (error) {
        // console.error('[Notificaciones] Error al marcar como leída:', error);
    }
}

/**
 * Función deprecada - Las notificaciones ya no navegan a solicitudes
 * Mantenida por compatibilidad pero no realiza ninguna acción
 */
function navegarASolicitud(solicitudId, tipoSolicitud) {
    // console.log('[Notificaciones] Navegación deshabilitada');
    // Las notificaciones ya no redirigen a otra vista
}

