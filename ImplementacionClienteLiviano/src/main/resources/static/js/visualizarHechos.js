// Variables globales
let map;
let marcadores = [];
let hechoSeleccionado = null;
let coleccionHandle = null;
let hechosOriginales = [];
let seleccionandoUbicacion = false;
let marcadorUbicacion = null;

// Inicialización del mapa
function initMap() {
    map = L.map('map').setView([-34.6118, -58.3960], 12);

    // Usar MapTiler como en las otras páginas
    L.tileLayer('https://api.maptiler.com/maps/streets-v2/{z}/{x}/{y}.png?key=lsLLHrc2xX9ejIqZplai', {
        attribution: '&copy; <a href="https://www.maptiler.com/copyright/">MapTiler</a> &copy; <a href="https://www.openstreetmap.org/copyright/">OpenStreetMap</a> contributors',
        maxZoom: 18,
        noWrap: true
    }).addTo(map);

    // Cargar hechos
    cargarHechos();
}

// Cargar hechos en el mapa
function cargarHechos() {
    console.log('[VisualizarHechos] 🔄 Cargando hechos...');

    // Obtener hechos del backend (ya cargados en window.hechos por Thymeleaf)
    if (window.hechos && window.hechos.length > 0) {
        hechosOriginales = window.hechos.map(transformarHecho);
        console.log('[VisualizarHechos] ✅ Hechos cargados:', hechosOriginales.length);
    } else {
        hechosOriginales = [];
        console.log('[VisualizarHechos] ⚠️ No hay hechos disponibles');
    }

    // Inicializar valores de filtros desde la URL
    inicializarFiltrosDesdeURL();

    // Mostrar todos los hechos en el mapa (ya vienen filtrados del servidor)
    mostrarHechosEnMapa(hechosOriginales);
}

// Inicializar valores de los filtros desde los parámetros de la URL
function inicializarFiltrosDesdeURL() {
    const urlParams = new URLSearchParams(window.location.search);

    // Categoría
    const categoria = urlParams.get('categoria');
    if (categoria) {
        document.getElementById('filtroCategoria').value = categoria;
    }

    // Tipo de origen
    const tipoOrigen = urlParams.get('tipo_origen');
    if (tipoOrigen) {
        document.getElementById('filtroTipoOrigen').value = tipoOrigen;
    }

    // Navegación curada
    const navegacionCurada = urlParams.get('navegacion_curada');
    if (navegacionCurada === 'true') {
        document.getElementById('navegacionCurada').checked = true;
    }

    // Fechas de carga
    const fechaCargaInicio = urlParams.get('fecha_reporte_desde');
    if (fechaCargaInicio) {
        document.getElementById('fechaCargaInicio').value = fechaCargaInicio;
    }

    const fechaCargaFin = urlParams.get('fecha_reporte_hasta');
    if (fechaCargaFin) {
        document.getElementById('fechaCargaFin').value = fechaCargaFin;
    }

    // Fechas de ocurrencia
    const fechaOcurrenciaInicio = urlParams.get('fecha_acontecimiento_desde');
    if (fechaOcurrenciaInicio) {
        document.getElementById('fechaOcurrenciaInicio').value = fechaOcurrenciaInicio;
    }

    const fechaOcurrenciaFin = urlParams.get('fecha_acontecimiento_hasta');
    if (fechaOcurrenciaFin) {
        document.getElementById('fechaOcurrenciaFin').value = fechaOcurrenciaFin;
    }

    // Ubicación
    const latitud = urlParams.get('latitud');
    if (latitud) {
        document.getElementById('filtroLatitud').value = latitud;
    }

    const longitud = urlParams.get('longitud');
    if (longitud) {
        document.getElementById('filtroLongitud').value = longitud;
    }

    const radio = urlParams.get('radio');
    if (radio) {
        document.getElementById('filtroRadio').value = radio;
    }

    // Si hay ubicación, mostrar marcador
    if (latitud && longitud) {
        if (marcadorUbicacion) {
            map.removeLayer(marcadorUbicacion);
        }

        marcadorUbicacion = L.marker([latitud, longitud], {
            icon: L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
                shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                shadowSize: [41, 41]
            })
        }).addTo(map);

        marcadorUbicacion.bindPopup(`
            <b>Ubicación del filtro</b><br/>
            Lat: ${latitud}<br/>
            Lng: ${longitud}<br/>
            Radio: ${radio} km
        `);
    }
}

// Transformar HechoDTO del backend al formato usado en el frontend
function transformarHecho(hechoDTO) {
    // Transformar multimedia si existe
    let multimediaArray = [];
    if (hechoDTO.contenido) {
        multimediaArray.push({
            tipo: hechoDTO.contenido.tipo || hechoDTO.contenido.tipoContenido,
            url: hechoDTO.contenido.url,
            descripcion: hechoDTO.contenido.descripcion
        });
    }

    const hechoTransformado = {
        id: hechoDTO.identificador,
        titulo: hechoDTO.titulo || 'Sin título',
        descripcion: hechoDTO.descripcion || 'Sin descripción',
        categoria: hechoDTO.categoria?.nombre || 'Sin categoría',
        ubicacion: hechoDTO.ubicacion ?
            `${hechoDTO.ubicacion.latitud}, ${hechoDTO.ubicacion.longitud}` :
            'Sin ubicación',
        fechaHecho: hechoDTO.fechaDelHecho,
        fechaCarga: hechoDTO.fechaDeCarga,
        fechaModificacion: hechoDTO.fechaUltimaModificacion,
        fuente: hechoDTO.fuente?.moduloFuente?.nombre || 'Sin fuente',
        origen: hechoDTO.origen || 'DESCONOCIDO',
        etiquetas: hechoDTO.etiquetas?.map(e => e.nombre) || [],
        multimedia: multimediaArray,
        lat: hechoDTO.ubicacion?.latitud,
        lng: hechoDTO.ubicacion?.longitud,
        visible: hechoDTO.visible
    };

    return hechoTransformado;
}

// Mostrar popup completo con información del hecho (modal)
function mostrarPopupHechoCompleto(hechoId) {
    console.log('[visualizarHechos] Mostrando popup completo para hecho con ID:', hechoId);

    // Buscar el hecho por ID
    const hecho = hechosOriginales.find(h => h.id === hechoId);
    if (!hecho) {
        console.error('[visualizarHechos] Hecho no encontrado con ID:', hechoId);
        return;
    }

    hechoSeleccionado = hecho;

    // Actualizar título
    document.getElementById('popupTitulo').textContent = hecho.titulo || 'Sin título';

    // Actualizar descripción
    document.getElementById('popupDescripcion').textContent = hecho.descripcion || 'Sin descripción';

    // Actualizar categoría
    document.getElementById('popupCategoria').textContent = hecho.categoria || 'Sin categoría';

    // Actualizar ubicación
    document.getElementById('popupUbicacion').textContent = hecho.ubicacion || 'Sin ubicación';

    // Actualizar fecha del hecho
    document.getElementById('popupFechaHecho').textContent = formatearFecha(hecho.fechaHecho);

    // Actualizar fecha de carga
    document.getElementById('popupFechaCarga').textContent = formatearFechaHora(hecho.fechaCarga);

    // Actualizar fuente
    document.getElementById('popupFuente').textContent = hecho.fuente || 'Sin fuente';

    // Actualizar origen
    document.getElementById('popupOrigen').textContent = hecho.origen || 'DESCONOCIDO';

    // Actualizar etiquetas
    const etiquetasText = hecho.etiquetas && hecho.etiquetas.length > 0
        ? hecho.etiquetas.join(', ')
        : 'Sin etiquetas';
    document.getElementById('popupEtiquetas').textContent = etiquetasText;

    // Renderizar multimedia si existe
    const multimediaContainer = document.getElementById('popupMultimedia');
    if (hecho.multimedia && hecho.multimedia.length > 0) {
        multimediaContainer.innerHTML = renderizarMultimediaCompleto(hecho.multimedia);
    } else {
        multimediaContainer.innerHTML = '<p style="color: #999; font-style: italic;">Sin contenido multimedia</p>';
    }

    // Obtener elementos del modal
    const popupElement = document.getElementById('popupHecho');
    const overlayElement = document.getElementById('popupOverlay');

    // Mostrar overlay
    if (overlayElement) {
        overlayElement.classList.add('active');
        overlayElement.style.display = 'block';
    }

    // Mostrar modal
    if (popupElement) {
        popupElement.classList.remove('hidden');
        popupElement.style.display = 'block';
    }
}

// Detectar tipo de contenido por extensión de archivo
function detectarTipoPorExtension(url) {
    if (!url) return null;

    const urlLower = url.toLowerCase();

    if (urlLower.match(/\.(jpg|jpeg|png|gif|bmp|svg|webp)(\?|$)/i)) {
        return 'imagen';
    } else if (urlLower.match(/\.(mp4|webm|ogv|avi|mov|mkv)(\?|$)/i)) {
        return 'video';
    } else if (urlLower.match(/\.(mp3|wav|ogg|m4a|aac|flac)(\?|$)/i)) {
        return 'audio';
    } else if (urlLower.match(/\.(txt|text)(\?|$)/i)) {
        return 'texto';
    }

    return null;
}

// Cargar texto inline
window.cargarTextoInlineVisualizar = async function(contenedorId, url) {
    const contenedor = document.getElementById(contenedorId);
    if (!contenedor) return;

    if (contenedor.style.display === 'none' || contenedor.innerHTML === '') {
        try {
            contenedor.innerHTML = '<div style="text-align: center; color: #999;">Cargando...</div>';
            contenedor.style.display = 'block';

            const response = await fetch(url);
            const texto = await response.text();

            contenedor.innerHTML = `<pre style="white-space: pre-wrap; word-wrap: break-word; margin: 0; font-family: Arial, sans-serif; font-size: 14px;">${texto}</pre>`;
        } catch (error) {
            contenedor.innerHTML = '<div style="color: #e74c3c;">Error al cargar el texto</div>';
        }
    } else {
        contenedor.style.display = 'none';
    }
};

// Función para renderizar multimedia de forma completa
function renderizarMultimediaCompleto(multimediaArray) {
    if (!multimediaArray || multimediaArray.length === 0) {
        return '<div class="multimedia-empty">Sin contenido multimedia</div>';
    }

    return multimediaArray.map(item => {
        let tipo = typeof item === 'string' ? item : (item.tipo || item.tipoContenido);
        const url = typeof item === 'string' ? null : item.url;

        if (!url) {
            return `<div class="multimedia-empty">${tipo || 'Sin contenido'}</div>`;
        }

        // Detectar tipo por extensión si no viene del backend
        if (!tipo || tipo === '') {
            tipo = detectarTipoPorExtension(url);
        }

        const tipoLower = tipo ? tipo.toString().toLowerCase() : '';

        // Imagen
        if (tipoLower === 'imagen' || tipoLower === 'image') {
            return `<div class="multimedia-viewer multimedia-image">
                <img src="${url}" alt="Imagen del hecho" 
                     style="max-width: 100%; max-height: 400px; border-radius: 8px; display: block; margin: 0 auto;"
                     onerror="this.onerror=null; this.src='data:image/svg+xml,%3Csvg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'200\\' height=\\'200\\'%3E%3Crect fill=\\'%23ddd\\' width=\\'200\\' height=\\'200\\'/%3E%3Ctext x=\\'50%25\\' y=\\'50%25\\' dominant-baseline=\\'middle\\' text-anchor=\\'middle\\' font-family=\\'sans-serif\\' font-size=\\'14\\' fill=\\'%23999\\'%3EImagen no disponible%3C/text%3E%3C/svg%3E';" />
            </div>`;
        }

        // Video
        if (tipoLower === 'video') {
            return `<div class="multimedia-viewer multimedia-video">
                <video controls preload="metadata" style="max-width: 100%; max-height: 400px; border-radius: 8px; display: block; margin: 0 auto;">
                    <source src="${url}" type="video/mp4">
                    <source src="${url}" type="video/webm">
                    Tu navegador no soporta la reproducción de video.
                </video>
            </div>`;
        }

        // Audio
        if (tipoLower === 'audio') {
            return `<div class="multimedia-viewer multimedia-audio">
                <div style="background: #f5f5f5; padding: 15px; border-radius: 8px; text-align: center;">
                    <div style="margin-bottom: 10px; color: #666;">🎵 Contenido de Audio</div>
                    <audio controls preload="metadata" style="width: 100%; max-width: 400px;">
                        <source src="${url}" type="audio/mpeg">
                        <source src="${url}" type="audio/ogg">
                        <source src="${url}" type="audio/wav">
                        Tu navegador no soporta la reproducción de audio.
                    </audio>
                </div>
            </div>`;
        }

        // Texto - Mostrar inline
        if (tipoLower === 'texto' || tipoLower === 'text') {
            const contenedorId = 'texto-vis-' + Math.random().toString(36).substr(2, 9);
            return `<div class="multimedia-viewer multimedia-text">
                <div style="background: #f9f9f9; padding: 15px; border-radius: 8px; border: 1px solid #e0e0e0;">
                    <div style="margin-bottom: 10px; color: #666; font-weight: bold;">📄 Contenido de Texto</div>
                    <button onclick="cargarTextoInlineVisualizar('${contenedorId}', '${url}')" 
                            style="padding: 8px 16px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
                        Ver contenido
                    </button>
                    <div id="${contenedorId}" style="margin-top: 10px; display: none; max-height: 300px; overflow-y: auto; text-align: left; padding: 10px; background: white; border-radius: 4px;"></div>
                </div>
            </div>`;
        }

        // Tipo desconocido
        return `<div class="multimedia-viewer multimedia-unknown">
            <div style="background: #f0f0f0; padding: 15px; border-radius: 8px; text-align: center;">
                <div style="margin-bottom: 10px; color: #666;">Tipo: ${tipo || 'Desconocido'}</div>
                <div style="margin-bottom: 10px; font-size: 12px; color: #999;">URL: ${url}</div>
                <a href="${url}" target="_blank" 
                   style="color: #007bff; text-decoration: none; padding: 8px 16px; background: white; border: 1px solid #007bff; border-radius: 4px; display: inline-block;">
                    Ver contenido
                </a>
            </div>
        </div>`;
    }).join('');
}

// Exponer la función globalmente para que pueda ser llamada desde los popups de Leaflet
window.mostrarPopupHechoCompleto = mostrarPopupHechoCompleto;

// Función global para mostrar detalles completos (llamada desde popups de Leaflet)
window.mostrarDetallesCompletos = function(hechoId) {
    console.log('[visualizarHechos] Mostrando detalles completos para hecho ID:', hechoId);
    const hecho = hechosOriginales.find(h => h.id === hechoId);
    if (hecho) {
        mostrarPopupHechoCompleto(hechoId);
    } else {
        console.error('[visualizarHechos] Hecho no encontrado:', hechoId);
    }
};

// Función para solicitar eliminación directamente desde el popup de Leaflet
function solicitarEliminacionDirecta(hechoId, event) {
    console.log('[DEBUG] Abriendo popup de eliminación para hecho ID:', hechoId);

    // Buscar el hecho por ID
    const hecho = hechosOriginales.find(h => h.id === hechoId);
    if (!hecho) {
        console.error('Hecho no encontrado con ID:', hechoId);
        if (window.Toast) {
            Toast.error('Error: No se pudo encontrar el hecho.');
        }
        return;
    }

    // Guardar el hecho seleccionado
    hechoSeleccionado = hecho;

    // Resetear el formulario
    document.getElementById('justificacion').value = '';
    document.getElementById('contadorPalabras').textContent = '0 / 500 caracteres';
    document.getElementById('contadorPalabras').className = '';
    document.getElementById('btnConfirmarEliminacion').disabled = true;

    // Mostrar el overlay
    document.getElementById('popupOverlay').classList.add('active');

    // Mostrar el popup de eliminación (remover clase hidden)
    document.getElementById('popupEliminar').classList.remove('hidden');
}

// Exponer la función globalmente
window.solicitarEliminacionDirecta = solicitarEliminacionDirecta;

// Formatear fecha
function formatearFecha(fechaStr) {
    if (!fechaStr) return 'No especificada';
    const fecha = new Date(fechaStr);
    return fecha.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// Formatear fecha y hora
function formatearFechaHora(fechaStr) {
    if (!fechaStr) return 'No especificada';
    const fecha = new Date(fechaStr);
    return fecha.toLocaleString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Funcionalidad del sidebar
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar-filtros');
    const toggleBtn = document.getElementById('toggleSidebarFiltros');

    if (sidebar.classList.contains('active')) {
        sidebar.classList.remove('active');
        toggleBtn.innerHTML = '&lt;';
    } else {
        sidebar.classList.add('active');
        toggleBtn.innerHTML = '&gt;';
    }
}

// Activar/desactivar modo de selección de ubicación en el mapa
function toggleSeleccionUbicacion() {
    const btn = document.getElementById('btnSeleccionarUbicacion');

    if (seleccionandoUbicacion) {
        // Desactivar modo de selección
        seleccionandoUbicacion = false;
        btn.classList.remove('active');
        btn.textContent = 'Seleccionar en mapa';
        map.getContainer().style.cursor = '';

        // Remover listener de click
        map.off('click', onMapClickUbicacion);
    } else {
        // Activar modo de selección
        seleccionandoUbicacion = true;
        btn.classList.add('active');
        btn.textContent = 'Cancelar selección';
        map.getContainer().style.cursor = 'crosshair';

        // Agregar listener de click
        map.on('click', onMapClickUbicacion);
    }
}

// Manejar click en el mapa para seleccionar ubicación
function onMapClickUbicacion(e) {
    const lat = e.latlng.lat.toFixed(6);
    const lng = e.latlng.lng.toFixed(6);

    // Actualizar inputs
    document.getElementById('filtroLatitud').value = lat;
    document.getElementById('filtroLongitud').value = lng;

    // Si no hay radio, poner un valor por defecto
    if (!document.getElementById('filtroRadio').value) {
        document.getElementById('filtroRadio').value = '5';
    }

    // Quitar marcador anterior si existe
    if (marcadorUbicacion) {
        map.removeLayer(marcadorUbicacion);
    }

    // Agregar marcador temporal en la ubicación seleccionada
    marcadorUbicacion = L.marker([lat, lng], {
        icon: L.icon({
            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41]
        })
    }).addTo(map);

    marcadorUbicacion.bindPopup(`
        <b>Ubicación seleccionada</b><br/>
        Lat: ${lat}<br/>
        Lng: ${lng}
    `).openPopup();

    // Desactivar modo de selección
    toggleSeleccionUbicacion();
}

// Aplicar filtros - Recarga la página para que el servidor aplique los filtros
function aplicarFiltros() {
    console.log('[VisualizarHechos] 🎯 Aplicando filtros desde el servidor...');

    const filtros = construirFiltros();
    const coleccion = window.coleccionHandle || 'default';
    const queryString = construirQueryString(coleccion, filtros);
    const newUrl = `/visualizarHechos${queryString}`;

    // Recargar la página con los nuevos filtros
    window.location.href = newUrl;
}

// Actualizar la información de la colección en el sidebar izquierdo
function actualizarInfoColeccion(info) {
    document.getElementById('coleccion-titulo').textContent = info.titulo || 'Sin título';
    document.getElementById('coleccion-descripcion').textContent = info.descripcion || 'Sin descripción';
    document.getElementById('coleccion-handle').textContent = info.handle || '-';
    document.getElementById('coleccion-algoritmo').textContent = info.algoritmoConsenso || '-';
    document.getElementById('coleccion-fuentes').textContent = info.fuentes ? info.fuentes.length : 0;
}

// Construir objeto de filtros desde el formulario
function construirFiltros() {
    return {
        navegacionCurada: document.getElementById('navegacionCurada')?.checked || false,
        categoria: document.getElementById('filtroCategoria')?.value.trim() || '',
        tipoOrigen: document.getElementById('filtroTipoOrigen')?.value.trim() || '',
        fechaCargaInicio: document.getElementById('fechaCargaInicio')?.value || '',
        fechaCargaFin: document.getElementById('fechaCargaFin')?.value || '',
        fechaOcurrenciaInicio: document.getElementById('fechaOcurrenciaInicio')?.value || '',
        fechaOcurrenciaFin: document.getElementById('fechaOcurrenciaFin')?.value || '',
        latitud: document.getElementById('filtroLatitud')?.value || '',
        longitud: document.getElementById('filtroLongitud')?.value || '',
        radio: document.getElementById('filtroRadio')?.value || ''
    };
}

// Construir query string para la URL
function construirQueryString(coleccion, filtros) {
    const params = new URLSearchParams();
    params.append('coleccion', coleccion);

    if (filtros.navegacionCurada) params.append('navegacion_curada', 'true');
    if (filtros.categoria) params.append('categoria', filtros.categoria);
    if (filtros.tipoOrigen) params.append('tipo_origen', filtros.tipoOrigen);
    if (filtros.fechaCargaInicio) params.append('fecha_reporte_desde', filtros.fechaCargaInicio);
    if (filtros.fechaCargaFin) params.append('fecha_reporte_hasta', filtros.fechaCargaFin);
    if (filtros.fechaOcurrenciaInicio) params.append('fecha_acontecimiento_desde', filtros.fechaOcurrenciaInicio);
    if (filtros.fechaOcurrenciaFin) params.append('fecha_acontecimiento_hasta', filtros.fechaOcurrenciaFin);
    if (filtros.latitud) params.append('latitud', filtros.latitud);
    if (filtros.longitud) params.append('longitud', filtros.longitud);
    if (filtros.radio) params.append('radio', filtros.radio);

    return '?' + params.toString();
}

// Limpiar filtros - Recarga la página sin filtros
function limpiarFiltros() {
    const coleccion = window.coleccionHandle || 'default';
    window.location.href = `/visualizarHechos?coleccion=${coleccion}`;
}

// Mostrar hechos en el mapa (todos los hechos ya vienen filtrados del servidor)
function mostrarHechosEnMapa(hechos) {
    console.log('[VisualizarHechos] 🗺️ Mostrando hechos en el mapa:', hechos.length);

    // Limpiar marcadores existentes
    marcadores.forEach(marcador => map.removeLayer(marcador));
    marcadores = [];

    // Verificar si hay hechos
    if (!hechos || hechos.length === 0) {
        mostrarMensajeNoHechos();
        return;
    }

    ocultarMensajeNoHechos();

    // Agregar marcadores para cada hecho
    hechos.forEach(hecho => {
        if (hecho.lat && hecho.lng) {
            // Crear icono azul para todos los hechos (ya están filtrados)
            const icono = L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
                shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                shadowSize: [41, 41]
            });

            const marcador = L.marker([hecho.lat, hecho.lng], { icon: icono }).addTo(map);

            // Verificar si el usuario es admin
            const isAdmin = window.isAdmin === true;

            // Botón de eliminación solo visible para NO admins
            const botonEliminacion = !isAdmin ? `
                        <button onclick="window.solicitarEliminacionDirecta(${hecho.id})" 
                                style="padding: 8px 12px; background: #e74c3c; color: white; border: none; border-radius: 6px; cursor: pointer; width: 100%;">
                            Solicitar Eliminación
                        </button>` : '';

            const popupContent = `
                <div style="min-width: 200px;">
                    <b style="color: #007bff;">${hecho.titulo || 'Sin título'}</b><br/>
                    <b>Categoría:</b> ${hecho.categoria || 'Sin categoría'}<br/>
                    <b>Fecha:</b> ${formatearFecha(hecho.fechaHecho)}<br/>
                    <div style="margin-top: 10px; display: flex; gap: 5px; flex-direction: column;">
                        <button onclick="window.mostrarDetallesCompletos(${hecho.id})" 
                                style="padding: 8px 12px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600; width: 100%;">
                            Ver Detalles Completos
                        </button>
                        ${botonEliminacion}
                    </div>
                </div>
            `;

            marcador.bindPopup(popupContent);

            // Centrar el mapa cuando se hace clic
            marcador.on('click', function() {
                map.panTo([hecho.lat, hecho.lng], {
                    animate: true,
                    duration: 0.5
                });
            });

            marcadores.push(marcador);
        }
    });

    console.log('[VisualizarHechos] ✅ Marcadores agregados:', marcadores.length);

    // Ajustar vista del mapa para mostrar todos los hechos
    const hechosConUbicacion = hechos.filter(h => h.lat && h.lng);
    if (hechosConUbicacion.length > 1) {
        const bounds = L.latLngBounds(hechosConUbicacion.map(h => [h.lat, h.lng]));
        map.fitBounds(bounds, {padding: [50, 50]});
    } else if (hechosConUbicacion.length === 1) {
        map.setView([hechosConUbicacion[0].lat, hechosConUbicacion[0].lng], 13);
    }
}

// Mostrar mensaje de "no hay hechos"
function mostrarMensajeNoHechos() {
    console.log('[VisualizarHechos] 📭 Mostrando mensaje: No hay hechos disponibles');
    const mensajeElement = document.getElementById('mensajeNoHechos');
    if (mensajeElement) {
        mensajeElement.classList.remove('hidden');
    }
    // Centrar en ubicación por defecto
    map.setView([-34.6118, -58.3960], 5);
}

// Ocultar mensaje de "no hay hechos"
function ocultarMensajeNoHechos() {
    const mensajeElement = document.getElementById('mensajeNoHechos');
    if (mensajeElement) {
        mensajeElement.classList.add('hidden');
    }
}

// Event Listeners - Se inicializan cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('[VisualizarHechos] Inicializando event listeners...');

    // Botón aplicar filtros
    const btnAplicar = document.getElementById('btnAplicarFiltros');
    if (btnAplicar) {
        btnAplicar.addEventListener('click', aplicarFiltros);
    }

    // Botón limpiar filtros
    const btnLimpiar = document.getElementById('btnLimpiarFiltros');
    if (btnLimpiar) {
        btnLimpiar.addEventListener('click', limpiarFiltros);
    }

    // Botón para seleccionar ubicación en el mapa
    const btnSeleccionar = document.getElementById('btnSeleccionarUbicacion');
    if (btnSeleccionar) {
        btnSeleccionar.addEventListener('click', toggleSeleccionUbicacion);
    }

    // Toggle sidebar de filtros
    const toggleBtn = document.getElementById('toggleSidebarFiltros');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', toggleSidebar);
    }

    // Cerrar popup de hecho
    const cerrarHecho = document.getElementById('cerrarPopupHecho');
    if (cerrarHecho) {
        cerrarHecho.addEventListener('click', function() {
            const popup = document.getElementById('popupHecho');
            const overlay = document.getElementById('popupOverlay');

            if (popup) {
                popup.classList.add('hidden');
                popup.style.display = 'none';
            }
            if (overlay) {
                overlay.classList.remove('active');
                overlay.style.display = 'none';
            }
        });
    }

    // Cerrar popup de eliminación
    const cerrarEliminar = document.getElementById('cerrarPopupEliminar');
    if (cerrarEliminar) {
        cerrarEliminar.addEventListener('click', function() {
            document.getElementById('popupEliminar').classList.add('hidden');
            document.getElementById('popupOverlay').classList.remove('active');
        });
    }

    // Cerrar popups al hacer click en el overlay
    const overlay = document.getElementById('popupOverlay');
    if (overlay) {
        overlay.addEventListener('click', function() {
            const popupHecho = document.getElementById('popupHecho');
            const popupEliminar = document.getElementById('popupEliminar');

            if (popupHecho) {
                popupHecho.classList.add('hidden');
                popupHecho.style.display = 'none';
            }
            if (popupEliminar) {
                popupEliminar.classList.add('hidden');
                popupEliminar.style.display = 'none';
            }
            overlay.classList.remove('active');
            overlay.style.display = 'none';
        });
    }

    // Botón para solicitar eliminación desde el popup de hecho
    const btnEliminar = document.getElementById('btnEliminarHecho');
    if (btnEliminar) {
        btnEliminar.addEventListener('click', function() {
            if (hechoSeleccionado) {
                solicitarEliminacionDirecta(hechoSeleccionado.id);
            }
        });
    }

    // Contador de caracteres en justificación
    const justificacion = document.getElementById('justificacion');
    const contador = document.getElementById('contadorPalabras');
    const btnConfirmar = document.getElementById('btnConfirmarEliminacion');

    if (justificacion && contador && btnConfirmar) {
        justificacion.addEventListener('input', function() {
            const length = this.value.length;
            contador.textContent = `${length} / 500 caracteres`;

            if (length >= 500) {
                contador.classList.add('valido');
                contador.classList.remove('invalido');
                btnConfirmar.disabled = false;
            } else {
                contador.classList.add('invalido');
                contador.classList.remove('valido');
                btnConfirmar.disabled = true;
            }
        });
    }

    // Confirmar eliminación
    if (btnConfirmar) {
        btnConfirmar.addEventListener('click', async function() {
            if (!hechoSeleccionado) return;

            const justificacionTexto = document.getElementById('justificacion').value;

            try {
                const response = await fetch('/visualizarHechos/api/eliminar', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        idHecho: hechoSeleccionado.id,
                        justificacion: justificacionTexto
                    })
                });

                const data = await response.json();

                if (data.success) {
                    if (window.Toast) {
                        Toast.success('Solicitud de eliminación enviada correctamente');
                    }
                    document.getElementById('popupEliminar').classList.add('hidden');
                    document.getElementById('popupOverlay').classList.remove('active');
                    document.getElementById('justificacion').value = '';
                } else {
                    if (window.Toast) {
                        Toast.error('Error: ' + data.error);
                    }
                }
            } catch (error) {
                console.error('Error al solicitar eliminación:', error);
                if (window.Toast) {
                    Toast.error('Error al procesar la solicitud');
                }
            }
        });
    }

    console.log('[VisualizarHechos] Event listeners inicializados correctamente');
});
