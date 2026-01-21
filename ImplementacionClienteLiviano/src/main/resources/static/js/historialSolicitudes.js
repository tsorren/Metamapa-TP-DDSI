// ========================================
// HISTORIAL DE SOLICITUDES - SISTEMA COMPLETO
// ========================================

let historialCompleto = null;
let filtroTipoActual = 'todos';
let filtrosEstado = {
    creacion: 'todos',
    modificacion: 'todos',
    eliminacion: 'todos'
};

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', function () {
    inicializarEventos();
    cargarHistorialCompleto();
});

// ===== EVENTOS =====
function inicializarEventos() {
    // Filtros de tipo
    document.querySelectorAll('.filtro-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const tipo = this.getAttribute('data-tipo');
            cambiarFiltroTipo(tipo);
        });
    });

    // Tabs de estado
    document.querySelectorAll('.estado-tab').forEach(tab => {
        tab.addEventListener('click', function () {
            const tipoSolicitud = this.getAttribute('data-estado-tipo');
            const estado = this.getAttribute('data-estado');
            cambiarFiltroEstado(tipoSolicitud, estado);
        });
    });

    // Cerrar modal
    document.getElementById('btn-cerrar-modal').addEventListener('click', cerrarModal);
    document.getElementById('modal-detalle').addEventListener('click', function (e) {
        if (e.target === this) {
            cerrarModal();
        }
    });
}

// ===== CARGAR HISTORIAL =====
async function cargarHistorialCompleto() {
    try {
        const response = await fetch('/historial-solicitudes/api/completo');
        if (!response.ok) {
            throw new Error('Error al cargar el historial');
        }

        historialCompleto = await response.json();
        // console.log('Historial cargado:', historialCompleto);

        renderizarHistorial();
        actualizarEstadisticas();
    } catch (error) {
        console.error('Error al cargar historial:', error);
        mostrarError('No se pudo cargar el historial de solicitudes');
    }
}

// ===== RENDERIZAR HISTORIAL =====
function renderizarHistorial() {
    if (!historialCompleto) return;

    renderizarSolicitudesCreacion();
    renderizarSolicitudesModificacion();
    renderizarSolicitudesEliminacion();
}

// ===== RENDERIZAR SOLICITUDES DE CREACIÓN =====
function renderizarSolicitudesCreacion() {
    const lista = document.getElementById('lista-creacion');
    const solicitudes = historialCompleto.creacion || {};

    let todasLasSolicitudes = [];
    Object.keys(solicitudes).forEach(estado => {
        if (Array.isArray(solicitudes[estado])) {
            solicitudes[estado].forEach(sol => {
                todasLasSolicitudes.push({ ...sol, estadoActual: estado });
            });
        }
    });

    if (todasLasSolicitudes.length === 0) {
        lista.innerHTML = '<div class="no-solicitudes">No hay solicitudes de creación registradas</div>';
        document.getElementById('contador-creacion').textContent = '0 solicitudes';
        return;
    }

    // Ordenar por fecha (más reciente primero)
    todasLasSolicitudes.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion));

    let html = '';
    todasLasSolicitudes.forEach(solicitud => {
        html += crearCardSolicitudCreacion(solicitud);
    });

    lista.innerHTML = html;
    document.getElementById('contador-creacion').textContent = `${todasLasSolicitudes.length} solicitud${todasLasSolicitudes.length !== 1 ? 'es' : ''}`;

    // Agregar eventos de click
    lista.querySelectorAll('.solicitud-card').forEach(card => {
        card.addEventListener('click', function () {
            const id = this.getAttribute('data-id');
            const solicitud = todasLasSolicitudes.find(s => s.identificador == id);
            if (solicitud) {
                mostrarDetalleSolicitud(solicitud, 'creacion');
            }
        });
    });

    aplicarFiltroEstado('creacion');
}

// ===== RENDERIZAR SOLICITUDES DE MODIFICACIÓN =====
function renderizarSolicitudesModificacion() {
    const lista = document.getElementById('lista-modificacion');
    const solicitudes = historialCompleto.modificacion || {};

    let todasLasSolicitudes = [];
    Object.keys(solicitudes).forEach(estado => {
        if (Array.isArray(solicitudes[estado])) {
            solicitudes[estado].forEach(sol => {
                todasLasSolicitudes.push({ ...sol, estadoActual: estado });
            });
        }
    });

    if (todasLasSolicitudes.length === 0) {
        lista.innerHTML = '<div class="no-solicitudes">No hay solicitudes de modificación registradas</div>';
        document.getElementById('contador-modificacion').textContent = '0 solicitudes';
        return;
    }

    // Ordenar por fecha (más reciente primero)
    todasLasSolicitudes.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion));

    let html = '';
    todasLasSolicitudes.forEach(solicitud => {
        html += crearCardSolicitudModificacion(solicitud);
    });

    lista.innerHTML = html;
    document.getElementById('contador-modificacion').textContent = `${todasLasSolicitudes.length} solicitud${todasLasSolicitudes.length !== 1 ? 'es' : ''}`;

    // Agregar eventos de click
    lista.querySelectorAll('.solicitud-card').forEach(card => {
        card.addEventListener('click', function () {
            const id = this.getAttribute('data-id');
            const solicitud = todasLasSolicitudes.find(s => s.identificador == id);
            if (solicitud) {
                mostrarDetalleSolicitud(solicitud, 'modificacion');
            }
        });
    });

    aplicarFiltroEstado('modificacion');
}

// ===== RENDERIZAR SOLICITUDES DE ELIMINACIÓN =====
function renderizarSolicitudesEliminacion() {
    const lista = document.getElementById('lista-eliminacion');
    const solicitudes = historialCompleto.eliminacion || {};

    let todasLasSolicitudes = [];
    Object.keys(solicitudes).forEach(estado => {
        if (Array.isArray(solicitudes[estado])) {
            solicitudes[estado].forEach(sol => {
                todasLasSolicitudes.push({ ...sol, estadoActual: estado });
            });
        }
    });

    if (todasLasSolicitudes.length === 0) {
        lista.innerHTML = '<div class="no-solicitudes">No hay solicitudes de eliminación registradas</div>';
        document.getElementById('contador-eliminacion').textContent = '0 solicitudes';
        return;
    }

    // Ordenar por fecha (más reciente primero)
    todasLasSolicitudes.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion));

    let html = '';
    todasLasSolicitudes.forEach(solicitud => {
        html += crearCardSolicitudEliminacion(solicitud);
    });

    lista.innerHTML = html;
    document.getElementById('contador-eliminacion').textContent = `${todasLasSolicitudes.length} solicitud${todasLasSolicitudes.length !== 1 ? 'es' : ''}`;

    // Agregar eventos de click
    lista.querySelectorAll('.solicitud-card').forEach(card => {
        card.addEventListener('click', function () {
            const id = this.getAttribute('data-id');
            const solicitud = todasLasSolicitudes.find(s => s.id == id);
            if (solicitud) {
                mostrarDetalleSolicitud(solicitud, 'eliminacion');
            }
        });
    });

    aplicarFiltroEstado('eliminacion');
}

// ===== CREAR CARDS =====
function crearCardSolicitudCreacion(solicitud) {
    // Obtener el estado actual del último estado en la lista
    const estados = solicitud.estadosDTO || solicitud.estados || [];
    const estadoActual = solicitud.estadoActual ||
                        (estados.length > 0
                            ? (estados[estados.length - 1].tipoEstado || estados[estados.length - 1].tipoEstadoRevision)
                            : 'PENDIENTE');

    const titulo = solicitud.hecho?.titulo || 'Sin título';
    const descripcion = solicitud.hecho?.descripcion || 'Sin descripción';

    return `
        <div class="solicitud-card" data-id="${solicitud.identificador}" data-estado="${estadoActual}">
            <div class="solicitud-header">
                <div class="solicitud-info">
                    <div class="solicitud-id">ID: ${solicitud.identificador}</div>
                    <h3 class="solicitud-titulo">${titulo}</h3>
                    <span class="estado-badge ${estadoActual}">${formatearEstado(estadoActual)}</span>
                </div>
            </div>
            <p class="solicitud-descripcion">${descripcion}</p>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">Creado por:</span>
                    <span class="metadata-value">${obtenerNombreContribuyente(solicitud.contribuyente)}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Fecha de creación:</span>
                    <span class="metadata-value">${formatearFecha(solicitud.fechaCreacion)}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Categoría:</span>
                    <span class="metadata-value">${solicitud.hecho?.categoria?.nombre || 'N/A'}</span>
                </div>
                ${estados.length > 0 && estados[estados.length - 1].administradorResponsable ? `
                <div class="metadata-item">
                    <span class="metadata-label">Procesado por:</span>
                    <span class="metadata-value">${estados[estados.length - 1].administradorResponsable.username || 'N/A'}</span>
                </div>
                ` : ''}
            </div>
        </div>
    `;
}

function crearCardSolicitudModificacion(solicitud) {
    // Obtener el estado actual del último estado en la lista
    const estados = solicitud.estados || solicitud.estadosDTO || [];
    const estadoActual = solicitud.estadoActual ||
                        (estados.length > 0
                            ? (estados[estados.length - 1].tipoEstado || estados[estados.length - 1].tipoEstadoRevision)
                            : 'PENDIENTE');

    const titulo = solicitud.modificacion?.titulo || solicitud.hecho?.titulo || 'Sin título';
    const descripcion = solicitud.modificacion?.descripcion || solicitud.hecho?.descripcion || 'Sin descripción';

    return `
        <div class="solicitud-card" data-id="${solicitud.identificador}" data-estado="${estadoActual}">
            <div class="solicitud-header">
                <div class="solicitud-info">
                    <div class="solicitud-id">ID: ${solicitud.identificador}</div>
                    <h3 class="solicitud-titulo">${titulo}</h3>
                    <span class="estado-badge ${estadoActual}">${formatearEstado(estadoActual)}</span>
                </div>
            </div>
            <p class="solicitud-descripcion">${descripcion}</p>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">Hecho modificado:</span>
                    <span class="metadata-value">ID ${solicitud.hecho?.identificador || solicitud.hecho?.id || 'N/A'}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Creado por:</span>
                    <span class="metadata-value">${obtenerNombreContribuyente(solicitud.contribuyente)}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Fecha de creación:</span>
                    <span class="metadata-value">${formatearFecha(solicitud.fechaCreacion)}</span>
                </div>
                ${estados.length > 0 && estados[estados.length - 1].administradorResponsable ? `
                <div class="metadata-item">
                    <span class="metadata-label">Procesado por:</span>
                    <span class="metadata-value">${estados[estados.length - 1].administradorResponsable.username || 'N/A'}</span>
                </div>
                ` : ''}
            </div>
        </div>
    `;
}

function crearCardSolicitudEliminacion(solicitud) {
    const estadoActual = solicitud.estadoActual || solicitud.estadoAtencionSolicitud || 'PENDIENTE';

    return `
        <div class="solicitud-card" data-id="${solicitud.id}" data-estado="${estadoActual}">
            <div class="solicitud-header">
                <div class="solicitud-info">
                    <div class="solicitud-id">ID: ${solicitud.id}</div>
                    <h3 class="solicitud-titulo">Solicitud de Eliminación - Hecho ${solicitud.hecho?.identificador || solicitud.hecho?.id || 'N/A'}</h3>
                    <span class="estado-badge ${estadoActual}">${formatearEstado(estadoActual)}</span>
                </div>
            </div>
            <p class="solicitud-descripcion">${solicitud.justificacion || 'Sin justificación'}</p>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">Hecho a eliminar:</span>
                    <span class="metadata-value">ID ${solicitud.hecho?.identificador || solicitud.hecho?.id || 'N/A'}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Solicitado por:</span>
                    <span class="metadata-value">${obtenerNombreContribuyente(solicitud.contribuyente)}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Fecha de solicitud:</span>
                    <span class="metadata-value">${formatearFecha(solicitud.fechaCreacion)}</span>
                </div>
                ${solicitud.administradorResponsable ? `
                <div class="metadata-item">
                    <span class="metadata-label">Procesado por:</span>
                    <span class="metadata-value">${solicitud.administradorResponsable.username || 'N/A'}</span>
                </div>
                ` : ''}
            </div>
        </div>
    `;
}

// ===== MODAL DE DETALLE =====
function mostrarDetalleSolicitud(solicitud, tipo) {
    const modal = document.getElementById('modal-detalle');
    const titulo = document.getElementById('modal-titulo');
    const body = document.getElementById('modal-body');

    titulo.textContent = `Detalle de Solicitud ${tipo.charAt(0).toUpperCase() + tipo.slice(1)} - ID ${solicitud.id}`;

    let html = '';

    if (tipo === 'creacion') {
        html = generarDetalleCreacion(solicitud);
    } else if (tipo === 'modificacion') {
        html = generarDetalleModificacion(solicitud);
    } else if (tipo === 'eliminacion') {
        html = generarDetalleEliminacion(solicitud);
    }

    body.innerHTML = html;
    modal.classList.add('show');
}

function generarDetalleCreacion(solicitud) {
    const estados = solicitud.estadosDTO || solicitud.estados || [];
    const estadoActual = solicitud.estadoActual ||
                        (estados.length > 0
                            ? (estados[estados.length - 1].tipoEstado || estados[estados.length - 1].tipoEstadoRevision)
                            : 'PENDIENTE');

    const titulo = solicitud.hecho?.titulo || 'Sin título';
    const descripcion = solicitud.hecho?.descripcion || 'Sin descripción';

    return `
        <div class="detalle-solicitud">
            <h4>Información General</h4>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">ID:</span>
                    <span class="metadata-value">${solicitud.identificador}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Estado Actual:</span>
                    <span class="metadata-value"><span class="estado-badge ${estadoActual}">${formatearEstado(estadoActual)}</span></span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Título:</span>
                    <span class="metadata-value">${titulo}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Descripción:</span>
                    <span class="metadata-value">${descripcion}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Categoría:</span>
                    <span class="metadata-value">${solicitud.hecho?.categoria?.nombre || 'N/A'}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Fecha de creación:</span>
                    <span class="metadata-value">${formatearFecha(solicitud.fechaCreacion)}</span>
                </div>
            </div>

            <h4 style="margin-top: 30px;">Participantes</h4>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">Creado por:</span>
                    <span class="metadata-value">${obtenerNombreContribuyente(solicitud.contribuyente)}</span>
                </div>
                ${estados.length > 0 && estados[estados.length - 1].administradorResponsable ? `
                <div class="metadata-item">
                    <span class="metadata-label">Procesado por:</span>
                    <span class="metadata-value">${estados[estados.length - 1].administradorResponsable.username || 'N/A'}</span>
                </div>
                ` : ''}
            </div>

            ${estados.length > 0 ? `
            <h4 style="margin-top: 30px;">Historial de Revisiones</h4>
            <div class="timeline-estados">
                ${estados.map(estado => `
                    <div class="timeline-item">
                        <div class="timeline-dot"></div>
                        <div class="timeline-content">
                            <div class="timeline-estado">${formatearEstado(estado.tipoEstado || estado.tipoEstadoRevision)}</div>
                            <div class="timeline-fecha">${formatearFecha(estado.fechaResolucion || estado.fechaCreacion)}</div>
                            ${estado.feedback ? `<div style="margin-top: 8px; color: var(--text-secondary);"><strong>Feedback:</strong> ${estado.feedback}</div>` : ''}
                            ${estado.administradorResponsable ? `<div style="margin-top: 4px; color: var(--text-muted); font-size: 0.9rem;">Por: ${estado.administradorResponsable.username || 'N/A'}</div>` : ''}
                        </div>
                    </div>
                `).join('')}
            </div>
            ` : '<p style="margin-top: 20px; color: var(--text-muted);">No hay historial de revisiones disponible.</p>'}
        </div>
    `;
}

function generarDetalleModificacion(solicitud) {
    const estados = solicitud.estados || solicitud.estadosDTO || [];
    const estadoActual = solicitud.estadoActual ||
                        (estados.length > 0
                            ? (estados[estados.length - 1].tipoEstado || estados[estados.length - 1].tipoEstadoRevision)
                            : 'PENDIENTE');

    const titulo = solicitud.modificacion?.titulo || solicitud.hecho?.titulo || 'Sin título';
    const descripcion = solicitud.modificacion?.descripcion || solicitud.hecho?.descripcion || 'Sin descripción';

    return `
        <div class="detalle-solicitud">
            <h4>Información General</h4>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">ID:</span>
                    <span class="metadata-value">${solicitud.identificador}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Estado Actual:</span>
                    <span class="metadata-value"><span class="estado-badge ${estadoActual}">${formatearEstado(estadoActual)}</span></span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Hecho Modificado:</span>
                    <span class="metadata-value">ID ${solicitud.hecho?.identificador || solicitud.hecho?.id || 'N/A'}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Título:</span>
                    <span class="metadata-value">${titulo}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Descripción:</span>
                    <span class="metadata-value">${descripcion}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Fecha de creación:</span>
                    <span class="metadata-value">${formatearFecha(solicitud.fechaCreacion)}</span>
                </div>
            </div>

            ${solicitud.modificacion ? `
            <h4 style="margin-top: 30px;">Cambios Propuestos</h4>
            <div class="solicitud-metadata">
                ${solicitud.modificacion.titulo ? `
                <div class="metadata-item">
                    <span class="metadata-label">Nuevo Título:</span>
                    <span class="metadata-value">${solicitud.modificacion.titulo}</span>
                </div>
                ` : ''}
                ${solicitud.modificacion.descripcion ? `
                <div class="metadata-item">
                    <span class="metadata-label">Nueva Descripción:</span>
                    <span class="metadata-value">${solicitud.modificacion.descripcion}</span>
                </div>
                ` : ''}
            </div>
            ` : ''}

            <h4 style="margin-top: 30px;">Participantes</h4>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">Creado por:</span>
                    <span class="metadata-value">${obtenerNombreContribuyente(solicitud.contribuyente)}</span>
                </div>
                ${estados.length > 0 && estados[estados.length - 1].administradorResponsable ? `
                <div class="metadata-item">
                    <span class="metadata-label">Procesado por:</span>
                    <span class="metadata-value">${estados[estados.length - 1].administradorResponsable.username || 'N/A'}</span>
                </div>
                ` : ''}
            </div>

            ${estados.length > 0 ? `
            <h4 style="margin-top: 30px;">Historial de Revisiones</h4>
            <div class="timeline-estados">
                ${estados.map(estado => `
                    <div class="timeline-item">
                        <div class="timeline-dot"></div>
                        <div class="timeline-content">
                            <div class="timeline-estado">${formatearEstado(estado.tipoEstado || estado.tipoEstadoRevision)}</div>
                            <div class="timeline-fecha">${formatearFecha(estado.fechaResolucion || estado.fechaCreacion)}</div>
                            ${estado.feedback ? `<div style="margin-top: 8px; color: var(--text-secondary);"><strong>Feedback:</strong> ${estado.feedback}</div>` : ''}
                            ${estado.administradorResponsable ? `<div style="margin-top: 4px; color: var(--text-muted); font-size: 0.9rem;">Por: ${estado.administradorResponsable.username || 'N/A'}</div>` : ''}
                        </div>
                    </div>
                `).join('')}
            </div>
            ` : '<p style="margin-top: 20px; color: var(--text-muted);">No hay historial de revisiones disponible.</p>'}
        </div>
    `;
}

function generarDetalleEliminacion(solicitud) {
    const estadoActual = solicitud.estadoActual || solicitud.estadoAtencionSolicitud || 'PENDIENTE';

    return `
        <div class="detalle-solicitud">
            <h4>Información General</h4>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">ID:</span>
                    <span class="metadata-value">${solicitud.id}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Estado Actual:</span>
                    <span class="metadata-value"><span class="estado-badge ${estadoActual}">${formatearEstado(estadoActual)}</span></span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Hecho a Eliminar:</span>
                    <span class="metadata-value">ID ${solicitud.hecho?.identificador || solicitud.hecho?.id || 'N/A'}</span>
                </div>
                ${solicitud.hecho?.titulo ? `
                <div class="metadata-item">
                    <span class="metadata-label">Título del Hecho:</span>
                    <span class="metadata-value">${solicitud.hecho.titulo}</span>
                </div>
                ` : ''}
                <div class="metadata-item">
                    <span class="metadata-label">Justificación:</span>
                    <span class="metadata-value">${solicitud.justificacion || 'Sin justificación'}</span>
                </div>
                <div class="metadata-item">
                    <span class="metadata-label">Fecha de solicitud:</span>
                    <span class="metadata-value">${formatearFecha(solicitud.fechaCreacion)}</span>
                </div>
                ${solicitud.fechaResolucion ? `
                <div class="metadata-item">
                    <span class="metadata-label">Fecha de resolución:</span>
                    <span class="metadata-value">${formatearFecha(solicitud.fechaResolucion)}</span>
                </div>
                ` : ''}
            </div>

            <h4 style="margin-top: 30px;">Participantes</h4>
            <div class="solicitud-metadata">
                <div class="metadata-item">
                    <span class="metadata-label">Solicitado por:</span>
                    <span class="metadata-value">${obtenerNombreContribuyente(solicitud.contribuyente)}</span>
                </div>
                ${solicitud.administradorResponsable ? `
                <div class="metadata-item">
                    <span class="metadata-label">Procesado por:</span>
                    <span class="metadata-value">${solicitud.administradorResponsable.username || 'N/A'}</span>
                </div>
                ` : ''}
            </div>

            ${solicitud.justificacionRechazo ? `
            <h4 style="margin-top: 30px;">Observaciones de Rechazo</h4>
            <p style="color: var(--text-secondary); line-height: 1.6;">${solicitud.justificacionRechazo}</p>
            ` : ''}
            
            <div style="margin-top: 30px;">
                <h4>Historial de Estados</h4>
                <div class="timeline-estados">
                    <div class="timeline-item">
                        <div class="timeline-dot"></div>
                        <div class="timeline-content">
                            <div class="timeline-estado">Creada</div>
                            <div class="timeline-fecha">${formatearFecha(solicitud.fechaCreacion)}</div>
                            <div style="margin-top: 4px; color: var(--text-muted); font-size: 0.9rem;">Por: ${obtenerNombreContribuyente(solicitud.contribuyente)}</div>
                        </div>
                    </div>
                    ${solicitud.fechaResolucion ? `
                    <div class="timeline-item">
                        <div class="timeline-dot"></div>
                        <div class="timeline-content">
                            <div class="timeline-estado">${formatearEstado(estadoActual)}</div>
                            <div class="timeline-fecha">${formatearFecha(solicitud.fechaResolucion)}</div>
                            ${solicitud.administradorResponsable ? `<div style="margin-top: 4px; color: var(--text-muted); font-size: 0.9rem;">Por: ${solicitud.administradorResponsable.username || 'N/A'}</div>` : ''}
                            ${solicitud.justificacionRechazo ? `<div style="margin-top: 8px; color: var(--text-secondary);"><strong>Justificación:</strong> ${solicitud.justificacionRechazo}</div>` : ''}
                        </div>
                    </div>
                    ` : ''}
                </div>
            </div>
        </div>
    `;
}

function cerrarModal() {
    document.getElementById('modal-detalle').classList.remove('show');
}

// ===== FILTROS =====
function cambiarFiltroTipo(tipo) {
    filtroTipoActual = tipo;

    // Actualizar botones activos
    document.querySelectorAll('.filtro-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-tipo') === tipo);
    });

    // Mostrar/ocultar secciones
    const secciones = {
        creacion: document.getElementById('seccion-creacion'),
        modificacion: document.getElementById('seccion-modificacion'),
        eliminacion: document.getElementById('seccion-eliminacion')
    };

    if (tipo === 'todos') {
        Object.values(secciones).forEach(sec => sec.classList.remove('hidden'));
    } else {
        Object.keys(secciones).forEach(key => {
            secciones[key].classList.toggle('hidden', key !== tipo);
        });
    }
}

function cambiarFiltroEstado(tipoSolicitud, estado) {
    filtrosEstado[tipoSolicitud] = estado;

    // Actualizar tabs activos
    document.querySelectorAll(`.estado-tab[data-estado-tipo="${tipoSolicitud}"]`).forEach(tab => {
        tab.classList.toggle('active', tab.getAttribute('data-estado') === estado);
    });

    aplicarFiltroEstado(tipoSolicitud);
}

function aplicarFiltroEstado(tipoSolicitud) {
    const estado = filtrosEstado[tipoSolicitud];
    const lista = document.getElementById(`lista-${tipoSolicitud}`);
    const cards = lista.querySelectorAll('.solicitud-card');

    cards.forEach(card => {
        const estadoCard = card.getAttribute('data-estado');
        if (estado === 'todos' || estadoCard === estado) {
            card.classList.remove('hidden');
        } else {
            card.classList.add('hidden');
        }
    });

    // Actualizar contador visible
    const visibles = lista.querySelectorAll('.solicitud-card:not(.hidden)').length;
    document.getElementById(`contador-${tipoSolicitud}`).textContent =
        `${visibles} solicitud${visibles !== 1 ? 'es' : ''} visible${visibles !== 1 ? 's' : ''}`;
}

// ===== ESTADÍSTICAS =====
function actualizarEstadisticas() {
    if (!historialCompleto) return;

    let total = 0;
    let aprobadas = 0;
    let rechazadas = 0;
    let pendientes = 0;

    // Contar solicitudes de creación
    Object.keys(historialCompleto.creacion || {}).forEach(estado => {
        const lista = historialCompleto.creacion[estado] || [];
        total += lista.length;
        if (estado === 'APROBADO' || estado === 'ACEPTADA') aprobadas += lista.length;
        else if (estado === 'RECHAZADO' || estado === 'RECHAZADA') rechazadas += lista.length;
        else if (estado === 'PENDIENTE' || estado === 'PENDIENTE_DE_MODIFICACION') pendientes += lista.length;
    });

    // Contar solicitudes de modificación
    Object.keys(historialCompleto.modificacion || {}).forEach(estado => {
        const lista = historialCompleto.modificacion[estado] || [];
        total += lista.length;
        if (estado === 'APROBADO' || estado === 'ACEPTADA') aprobadas += lista.length;
        else if (estado === 'RECHAZADO' || estado === 'RECHAZADA') rechazadas += lista.length;
        else if (estado === 'PENDIENTE' || estado === 'PENDIENTE_DE_MODIFICACION') pendientes += lista.length;
    });

    // Contar solicitudes de eliminación
    Object.keys(historialCompleto.eliminacion || {}).forEach(estado => {
        const lista = historialCompleto.eliminacion[estado] || [];
        total += lista.length;
        if (estado === 'ACEPTADA' || estado === 'APROBADO') aprobadas += lista.length;
        else if (estado === 'RECHAZADA' || estado === 'RECHAZADO') rechazadas += lista.length;
        else if (estado === 'PENDIENTE') pendientes += lista.length;
    });

    document.getElementById('total-solicitudes').textContent = total;
    document.getElementById('total-aprobadas').textContent = aprobadas;
    document.getElementById('total-rechazadas').textContent = rechazadas;
    document.getElementById('total-pendientes').textContent = pendientes;
}

// ===== UTILIDADES =====

function obtenerNombreContribuyente(contribuyente) {
    if (!contribuyente) return 'Desconocido';
    if (contribuyente.nombre && contribuyente.apellido) {
        return `${contribuyente.nombre} ${contribuyente.apellido}`;
    }
    return contribuyente.username || contribuyente.nombreUsuario || 'Desconocido';
}

function formatearEstado(estado) {
    const estados = {
        'PENDIENTE': 'Pendiente',
        'APROBADO': 'Aprobado',
        'ACEPTADA': 'Aceptada',
        'RECHAZADO': 'Rechazado',
        'RECHAZADA': 'Rechazada',
        'PENDIENTE_DE_MODIFICACION': 'Pend. Modificación'
    };
    return estados[estado] || estado;
}

function formatearFecha(fecha) {
    if (!fecha) return 'N/A';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function mostrarError(mensaje) {
    if (window.Toast) {
        Toast.error(mensaje);
    } else {
        console.error('[Error]', mensaje);
    }
}

