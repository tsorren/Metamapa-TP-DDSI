/**
 * Gestión de Solicitudes de Administrador
 * Integración completa con Backend (Agregador y FuenteDinamica)
 */

(function () {
    if (window.__SOLICITUDES_INIT__) return;
    window.__SOLICITUDES_INIT__ = true;

    // Estado global de la aplicación
    const state = {
        solicitudes: {
            eliminacion: {pendientes: []},
            creacion: {pendientes: [], pendientesModificacion: []},
            modificacion: {pendientes: [], pendientesModificacion: []}
        },
        solicitudActual: null,
        tipoActual: null
    };

    document.addEventListener("DOMContentLoaded", () => {
        inicializarEventos();
        cargarTodasLasSolicitudes();
    });

    /**
     * Inicializar eventos del DOM
     */
    function inicializarEventos() {
        // Popup actions
        const btnCancelar = document.querySelector(".btn-cancelar");
        const btnGuardar = document.querySelector(".btn-guardar");

        if (btnCancelar) {
            btnCancelar.addEventListener("click", cerrarPopup);
        }

        if (btnGuardar) {
            btnGuardar.addEventListener("click", procesarSolicitud);
        }
    }

    /**
     * Cargar todas las solicitudes del backend
     */
    async function cargarTodasLasSolicitudes() {
        try {
            mostrarCargando(true);

            const response = await fetch('/admin/solicitudes/pendientes', {
                credentials: 'include'
            });
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }

            const data = await response.json();
            state.solicitudes = data;

            renderizarSolicitudes();
            mostrarCargando(false);

        } catch (error) {
            console.error('Error al cargar solicitudes:', error);
            mostrarError('Error al cargar las solicitudes. Por favor, intenta de nuevo.');
            mostrarCargando(false);
        }
    }

    /**
     * Renderizar todas las solicitudes en el DOM
     */
    function renderizarSolicitudes() {
        renderizarSolicitudesCreacion();
        renderizarSolicitudesModificacion();
        renderizarSolicitudesEliminacion();
    }

    /**
     * Renderizar solicitudes de creación
     */
    function renderizarSolicitudesCreacion() {
        const contenedorCategoria = encontrarContenedorCategoria('Creación');
        if (!contenedorCategoria) return;

        const listaHechos = contenedorCategoria.querySelector('.lista-hechos');
        listaHechos.innerHTML = '';

        // Solicitudes pendientes
        const pendientes = state.solicitudes.creacion.pendientes || [];
        pendientes.forEach(solicitud => {
            listaHechos.appendChild(crearCardSolicitud(solicitud, 'creacion', 'PENDIENTE'));
        });

        // Solicitudes pendientes de modificación
        const pendientesModif = state.solicitudes.creacion.pendientesModificacion || [];
        if (pendientesModif.length > 0) {
            const separador = document.createElement('div');
            separador.className = 'separador-subseccion';
            separador.innerHTML = '<h4>Pendientes de Modificación</h4>';
            listaHechos.appendChild(separador);

            pendientesModif.forEach(solicitud => {
                listaHechos.appendChild(crearCardSolicitud(solicitud, 'creacion', 'PENDIENTE_DE_MODIFICACION'));
            });
        }

        if (pendientes.length === 0 && pendientesModif.length === 0) {
            listaHechos.innerHTML = '<p class="no-solicitudes">No hay solicitudes de creación pendientes</p>';
        }
    }

    /**
     * Renderizar solicitudes de modificación
     */
    function renderizarSolicitudesModificacion() {
        const contenedorCategoria = encontrarContenedorCategoria('Modificación');
        if (!contenedorCategoria) {
            return;
        }

        const listaHechos = contenedorCategoria.querySelector('.lista-hechos');
        listaHechos.innerHTML = '';

        const pendientes = state.solicitudes.modificacion.pendientes || [];
        
        pendientes.forEach((solicitud, index) => {
            listaHechos.appendChild(crearCardSolicitud(solicitud, 'modificacion', 'PENDIENTE'));
        });

        const pendientesModif = state.solicitudes.modificacion.pendientesModificacion || [];
        if (pendientesModif.length > 0) {
            const separador = document.createElement('div');
            separador.className = 'separador-subseccion';
            separador.innerHTML = '<h4>Pendientes de Modificación</h4>';
            listaHechos.appendChild(separador);

            pendientesModif.forEach(solicitud => {
                listaHechos.appendChild(crearCardSolicitud(solicitud, 'modificacion', 'PENDIENTE_DE_MODIFICACION'));
            });
        }

        if (pendientes.length === 0 && pendientesModif.length === 0) {
            listaHechos.innerHTML = '<p class="no-solicitudes">No hay solicitudes de modificación pendientes</p>';
        }
    }

    /**
     * Renderizar solicitudes de eliminación
     */
    function renderizarSolicitudesEliminacion() {
        const contenedorCategoria = encontrarContenedorCategoria('Eliminación');
        if (!contenedorCategoria) return;

        const listaHechos = contenedorCategoria.querySelector('.lista-hechos');
        listaHechos.innerHTML = '';

        const pendientes = state.solicitudes.eliminacion.pendientes || [];
        pendientes.forEach(solicitud => {
            listaHechos.appendChild(crearCardSolicitudEliminacion(solicitud));
        });

        if (pendientes.length === 0) {
            listaHechos.innerHTML = '<p class="no-solicitudes">No hay solicitudes de eliminación pendientes</p>';
        }
    }

    /**
     * Crear card de solicitud de revisión (creación/modificación)
     */
    function crearCardSolicitud(solicitud, tipo, estadoSubcategoria) {

        const container = document.createElement('div');
        container.className = 'solicitud-container';

        const card = document.createElement('div');
        card.className = 'hecho-card';
        card.dataset.id = solicitud.identificador;
        card.dataset.tipo = tipo;
        card.dataset.estadoSubcategoria = estadoSubcategoria;

        let titulo = solicitud.hecho?.titulo || 'Sin título';
        let contribuyente = obtenerNombreContribuyente(solicitud.contribuyente);

        // Para solicitudes de modificación, agregar botón "Ver Cambios"
        let botonesExtra = '';
        if (tipo === 'modificacion' && (solicitud.modificacion || solicitud.hechoModificacion)) {
            botonesExtra = '<button class="btn-ver-cambios">Ver Cambios</button>';
        }

        card.innerHTML = `
            <div class="card-header">
                <span class="titulo-hecho">${titulo}</span>
                <span class="contribuyente">Por: ${contribuyente}</span>
            </div>
            <div class="card-actions">
                <button class="btn-accion">Procesar</button>
                <button class="btn-historial">Ver Historial</button>
                ${botonesExtra}
            </div>
        `;

        // Crear historial
        const historial = crearHistorialSolicitud(solicitud, tipo);

        // Event listeners
        card.querySelector('.btn-accion').addEventListener('click', () => {
            abrirPopupProcesar(solicitud, tipo, estadoSubcategoria);
        });

        card.querySelector('.btn-historial').addEventListener('click', () => {
            historial.classList.toggle('hidden');
        });

        // Event listener para "Ver Cambios" (solo en solicitudes de modificación)
        const btnVerCambios = card.querySelector('.btn-ver-cambios');
        if (btnVerCambios) {
            btnVerCambios.addEventListener('click', () => {
                abrirPopupCambios(solicitud);
            });
        }

        container.appendChild(card);
        container.appendChild(historial);

        return container;
    }

    /**
     * Crear card de solicitud de eliminación
     */
    function crearCardSolicitudEliminacion(solicitud) {
        const container = document.createElement('div');
        container.className = 'solicitud-container';

        const card = document.createElement('div');
        card.className = 'hecho-card';
        card.dataset.id = solicitud.id;
        card.dataset.tipo = 'eliminacion';

        const titulo = solicitud.hecho?.titulo || 'Sin título';
        const contribuyente = obtenerNombreContribuyente(solicitud.contribuyente);

        card.innerHTML = `
            <div class="card-header">
                <span class="titulo-hecho">${titulo}</span>
                <span class="contribuyente">Solicitada por: ${contribuyente}</span>
            </div>
            <div class="card-actions">
                <button class="btn-accion">Procesar</button>
                <button class="btn-historial">Ver Detalles</button>
            </div>
        `;

        const historial = crearHistorialEliminacion(solicitud);

        card.querySelector('.btn-accion').addEventListener('click', () => {
            abrirPopupProcesar(solicitud, 'eliminacion', null);
        });

        card.querySelector('.btn-historial').addEventListener('click', () => {
            historial.classList.toggle('hidden');
        });

        container.appendChild(card);
        container.appendChild(historial);

        return container;
    }

    /**
     * Crear historial de solicitud de revisión
     */
    function crearHistorialSolicitud(solicitud, tipo) {
        const historialDiv = document.createElement('div');
        historialDiv.className = 'historial hidden';

        let hecho = solicitud.hecho || {};
        
        // IMPORTANTE: El backend devuelve "estados" no "estadosDTO"
        const estadosHTML = (solicitud.estados || solicitud.estadosDTO || []).map(estado => {
            const fecha = formatearFecha(estado.fechaResolucion);
            const admin = estado.administradorResponsable?.username || 'Sistema';
            const feedback = estado.feedback || 'Sin comentarios';
            const tipoEstado = estado.tipoEstado || estado.tipoEstadoRevision || 'DESCONOCIDO';
            return `
                <li>
                    <strong>${tipoEstado}</strong> - ${fecha}
                    <br/>Admin: ${admin}
                    <br/>Feedback: ${feedback}
                </li>
            `;
        }).join('');

        historialDiv.innerHTML = `
            <div class="historial-card">
                <div class="hist-header">
                    <strong>Título: ${hecho.titulo || 'Sin título'}</strong>
                    <button class="btn-ojo" onclick="window.location.href='/visualizarHechos'">👁</button>
                </div>
                <p class="descripcion"><strong>Descripción:</strong> ${hecho.descripcion || 'Sin descripción'}</p>
                <p class="categoria"><strong>Categoría:</strong> ${hecho.categoria?.nombre || 'Sin categoría'}</p>
                <p class="ubicacion"><strong>Ubicación:</strong> Lat ${hecho.ubicacion?.latitud || '-'}, Lng ${hecho.ubicacion?.longitud || '-'}</p>
                <p class="fecha-creacion"><strong>Fecha de creación:</strong> ${formatearFecha(solicitud.fechaCreacion)}</p>
                <p class="fecha-hecho"><strong>Fecha del hecho:</strong> ${hecho.fechaDelHecho || 'Sin fecha'}</p>
                <h4>Historial de Estados:</h4>
                <ul>${estadosHTML || '<li>Sin historial</li>'}</ul>
            </div>
        `;

        return historialDiv;
    }

    /**
     * Crear historial de eliminación
     */
    function crearHistorialEliminacion(solicitud) {
        const historialDiv = document.createElement('div');
        historialDiv.className = 'historial hidden';

        const hecho = solicitud.hecho || {};

        historialDiv.innerHTML = `
            <div class="historial-card">
                <div class="hist-header">
                    <strong>Título: ${hecho.titulo || 'Sin título'}</strong>
                    <button class="btn-ojo" onclick="window.location.href='/visualizarHechos'">👁</button>
                </div>
                <p class="descripcion"><strong>Descripción:</strong> ${hecho.descripcion || 'Sin descripción'}</p>
                <p class="justificacion"><strong>Justificación:</strong> ${solicitud.justificacion || 'Sin justificación'}</p>
                <p class="fecha-creacion"><strong>Solicitud creada:</strong> ${formatearFecha(solicitud.fechaCreacion)}</p>
                <p class="estado"><strong>Estado:</strong> ${solicitud.estadoAtencionSolicitud}</p>
            </div>
        `;

        return historialDiv;
    }

    /**
     * Abrir popup mostrando los cambios propuestos en una solicitud de modificación
     */
    function abrirPopupCambios(solicitud) {
        const mod = solicitud.modificacion || solicitud.hechoModificacion;
        const original = solicitud.hecho;
        
        if (!mod || !original) {
            mostrarError('No se pueden mostrar los cambios');
            return;
        }

        // Crear overlay del popup
        const overlay = document.createElement('div');
        overlay.className = 'popup-cambios-overlay';
        overlay.id = 'popup-cambios';
        
        // Determinar qué campos fueron modificados
        const tituloModificado = mod.titulo !== null && mod.titulo !== undefined;
        const descripcionModificada = mod.descripcion !== null && mod.descripcion !== undefined;
        const categoriaModificada = mod.categoria !== null && mod.categoria !== undefined;
        const ubicacionModificada = mod.ubicacion !== null && mod.ubicacion !== undefined;
        const fechaModificada = mod.fechaDelHecho !== null && mod.fechaDelHecho !== undefined;
        
        // Función helper para crear un campo
        const crearCampo = (label, valorOriginal, valorNuevo, modificado) => {
            const claseModificado = modificado ? 'campo-modificado' : '';
            const valorMostrar = modificado ? valorNuevo : valorOriginal;
            const indicador = modificado ? '<span class="indicador-modificado">●</span>' : '';
            
            return `
                <div class="campo-dato ${claseModificado}">
                    <label>${label}${indicador}</label>
                    <div class="valor-campo">
                        ${modificado ? `<span class="valor-original">${valorOriginal}</span>` : ''}
                        <span class="valor-mostrado">${valorMostrar}</span>
                    </div>
                </div>
            `;
        };
        
        overlay.innerHTML = `
            <div class="popup-cambios-contenido">
                <div class="popup-cambios-header">
                    <h2>Cambios Propuestos</h2>
                    <button class="btn-cerrar-popup">&times;</button>
                </div>
                
                <div class="popup-cambios-body">
                    <div class="leyenda-cambios">
                        <span class="leyenda-item">
                            <span class="indicador-modificado">●</span> Campo modificado
                        </span>
                    </div>
                    
                    <div class="datos-hecho">
                        ${crearCampo(
                            'Título',
                            original.titulo || 'Sin título',
                            mod.titulo,
                            tituloModificado
                        )}
                        
                        ${crearCampo(
                            'Descripción',
                            original.descripcion || 'Sin descripción',
                            mod.descripcion,
                            descripcionModificada
                        )}
                        
                        ${crearCampo(
                            'Categoría',
                            original.categoria?.nombre || 'Sin categoría',
                            mod.categoria?.nombre,
                            categoriaModificada
                        )}
                        
                        ${crearCampo(
                            'Fecha del Hecho',
                            original.fechaDelHecho || 'Sin fecha',
                            mod.fechaDelHecho,
                            fechaModificada
                        )}
                        
                        ${crearCampo(
                            'Ubicación',
                            `Lat: ${original.ubicacion?.latitud || 'N/A'}, Lng: ${original.ubicacion?.longitud || 'N/A'}`,
                            `Lat: ${mod.ubicacion?.latitud}, Lng: ${mod.ubicacion?.longitud}`,
                            ubicacionModificada
                        )}
                    </div>
                </div>
                
                <div class="popup-cambios-footer">
                    <button class="btn-cerrar">Cerrar</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(overlay);
        
        // Event listeners
        overlay.querySelector('.btn-cerrar-popup').addEventListener('click', () => {
            overlay.remove();
        });
        
        overlay.querySelector('.btn-cerrar').addEventListener('click', () => {
            overlay.remove();
        });
        
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                overlay.remove();
            }
        });
    }

    /**
     * Abrir popup para procesar solicitud
     */
    function abrirPopupProcesar(solicitud, tipo, estadoSubcategoria) {
        state.solicitudActual = solicitud;
        state.tipoActual = tipo;

        const popup = document.getElementById("popup");
        const selectEstado = document.getElementById("estado");
        const feedbackArea = document.getElementById("feedback");

        // Limpiar
        feedbackArea.value = '';
        selectEstado.innerHTML = '';

        // Configurar opciones según el tipo
        if (tipo === 'eliminacion') {
            selectEstado.innerHTML = `
                <option value="ACEPTADA">Aceptar</option>
                <option value="RECHAZADA">Rechazar</option>
            `;
            feedbackArea.placeholder = 'Justificación (opcional)...';
        } else {
            // Creación o Modificación
            selectEstado.innerHTML = `
                <option value="ACEPTADA">Aceptar</option>
                <option value="RECHAZADA">Rechazar</option>
                <option value="PENDIENTE_DE_MODIFICACION">Solicitar Modificación</option>
            `;
            feedbackArea.placeholder = 'Feedback para el contribuyente...';
        }

        popup.classList.add("active");
    }

    /**
     * Cerrar popup
     */
    function cerrarPopup() {
        const popup = document.getElementById("popup");
        popup.classList.remove("active");
        state.solicitudActual = null;
        state.tipoActual = null;
    }

    /**
     * Procesar solicitud
     */
    async function procesarSolicitud() {
        if (!state.solicitudActual || !state.tipoActual) return;

        const selectEstado = document.getElementById("estado");
        const feedbackArea = document.getElementById("feedback");

        const nuevoEstado = selectEstado.value;
        const feedback = feedbackArea.value.trim();


        try {
            mostrarCargando(true);

            let response;

            if (state.tipoActual === 'eliminacion') {
                const body = {
                    estado: nuevoEstado,
                    justificacion: feedback
                };

                response = await fetch(`/admin/solicitudes/eliminacion/${state.solicitudActual.id}`, {
                    method: 'PATCH',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(body),
                    credentials: 'include'
                });

            } else {
                const body = {
                    nuevoEstado: nuevoEstado,
                    feedback: feedback
                };

                const endpoint = state.tipoActual === 'creacion' ? 'creacion' : 'modificacion';
                response = await fetch(`/admin/solicitudes/${endpoint}/${state.solicitudActual.identificador}`, {
                    method: 'PATCH',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(body),
                    credentials: 'include'
                });
            }

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Error ${response.status}`);
            }

            const result = await response.json();

            if (result.success) {
                mostrarExito('Solicitud procesada correctamente');
                cerrarPopup();
                // Recargar solicitudes
                await cargarTodasLasSolicitudes();
            } else {
                throw new Error(result.error || 'Error desconocido');
            }

        } catch (error) {
            console.error('Error al procesar solicitud:', error);
            mostrarError('Error al procesar la solicitud: ' + error.message);
        } finally {
            mostrarCargando(false);
        }
    }

    /**
     * Utilidades
     */

    function obtenerNombreContribuyente(contribuyente) {
        if (!contribuyente) return 'Desconocido';
        if (contribuyente.nombre && contribuyente.apellido) {
            return `${contribuyente.nombre} ${contribuyente.apellido}`;
        }
        return contribuyente.username || contribuyente.nombreUsuario || 'Desconocido';
    }

    function encontrarContenedorCategoria(nombreCategoria) {
        const categorias = document.querySelectorAll('.categoria');
        for (let cat of categorias) {
            const h3 = cat.querySelector('h3');
            if (h3 && h3.textContent.trim() === nombreCategoria) {
                return cat;
            }
        }
        return null;
    }

    function formatearFecha(fecha) {
        if (!fecha) return 'Sin fecha';
        try {
            const d = new Date(fecha);
            return d.toLocaleString('es-AR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch {
            return fecha;
        }
    }

    function mostrarCargando(mostrar) {
        let loader = document.getElementById('loader-overlay');
        if (!loader && mostrar) {
            loader = document.createElement('div');
            loader.id = 'loader-overlay';
            loader.innerHTML = '<div class="spinner"></div><p>Cargando...</p>';
            document.body.appendChild(loader);
        }
        if (loader) {
            loader.style.display = mostrar ? 'flex' : 'none';
        }
    }

    function mostrarError(mensaje) {
        Toast.error(mensaje);
    }

    function mostrarExito(mensaje) {
        Toast.success(mensaje);
    }

})();