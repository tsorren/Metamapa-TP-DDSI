// Variables globales para el carrusel
let currentCollectionIndex = 0;
let carouselMap = null;
let collections = [];

// Crear todos los mapas dinámicamente según los div con class "map"
document.addEventListener("DOMContentLoaded", () => {
    // Cargar colecciones desde la variable global
    collections = window.coleccionesData || [];

    // Inicializar carrusel si hay colecciones
    if (collections && collections.length > 0) {
        initCarousel();
    }

    // Inicializar mapas dinámicos (si existen otros mapas en la página)
    document.querySelectorAll(".map").forEach(div => {
        // Saltar el mapa del carrusel
        if (div.id === 'carousel-map') return;

        const coords = JSON.parse(div.dataset.coords || "[0,0]");
        const markers = JSON.parse(div.dataset.markers || "[]");

        if (!div._leaflet_id) {
            const map = L.map(div.id).setView(coords, 13);

            // Tiles MapTiler
            L.tileLayer(
                `https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=lsLLHrc2xX9ejIqZplai`,
                {
                    attribution:
                        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://www.maptiler.com/">MapTiler</a>',
                    maxZoom: 20,
                    noWrap: true
                }
            ).addTo(map);

            // Agregar marcadores dinámicos
            markers.forEach(m => {
                L.marker(m.coords).addTo(map).bindPopup(m.popup);
            });

            // Botón centrar
            addCenterButton(map, coords);
        }
    });
});

// Inicializar carrusel de colecciones
function initCarousel() {
    if (!collections || collections.length === 0) {
        // console.log('No hay colecciones disponibles');
        return;
    }

    // Crear indicadores
    createIndicators();

    // Configurar botones
    const prevBtn = document.getElementById('carousel-prev');
    const nextBtn = document.getElementById('carousel-next');

    if (prevBtn) {
        prevBtn.addEventListener('click', () => navigateCarousel(-1));
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => navigateCarousel(1));
    }

    // Mostrar primera colección
    showCollection(0);
}

// Crear indicadores del carrusel
function createIndicators() {
    const indicatorsContainer = document.getElementById('carousel-indicators');
    if (!indicatorsContainer) return;

    indicatorsContainer.innerHTML = '';

    collections.forEach((_, index) => {
        const indicator = document.createElement('button');
        indicator.className = 'carousel-indicator';
        indicator.setAttribute('aria-label', `Ir a colección ${index + 1}`);
        indicator.addEventListener('click', () => showCollection(index));
        indicatorsContainer.appendChild(indicator);
    });
}

// Navegar en el carrusel
function navigateCarousel(direction) {
    const newIndex = currentCollectionIndex + direction;

    if (newIndex >= 0 && newIndex < collections.length) {
        showCollection(newIndex);
    }
}

// Mostrar colección específica
function showCollection(index) {
    if (index < 0 || index >= collections.length) return;

    currentCollectionIndex = index;
    const collection = collections[index];

    // Actualizar contenido de la tarjeta
    updateCollectionCard(collection);

    // Actualizar mapa
    updateCarouselMap(collection);

    // Actualizar indicadores
    updateIndicators();

    // Actualizar estado de botones
    updateNavigationButtons();
}

// Actualizar contenido de la tarjeta de colección
function updateCollectionCard(collection) {
    // Título
    const titleElement = document.getElementById('collection-title');
    if (titleElement) {
        titleElement.textContent = collection.titulo || 'Sin título';
    }

    // Descripción
    const descElement = document.getElementById('collection-description');
    if (descElement) {
        descElement.textContent = collection.descripcion || 'Sin descripción';
    }

    // Contador de hechos
    const countElement = document.getElementById('hechos-count');
    if (countElement) {
        const hechosCount = collection.hechos ? collection.hechos.length : 0;
        countElement.textContent = hechosCount;
    }
}

// Actualizar mapa del carrusel
function updateCarouselMap(collection) {
    const mapElement = document.getElementById('carousel-map');
    if (!mapElement) return;

    // Destruir mapa anterior si existe
    if (carouselMap) {
        carouselMap.remove();
        carouselMap = null;
    }

    // Determinar coordenadas iniciales
    let coords = [-34.6037, -58.3816]; // Buenos Aires por defecto
    let zoom = 5;

    if (collection.hechos && collection.hechos.length > 0) {
        const firstHecho = collection.hechos[0];
        if (firstHecho.lat && firstHecho.lng) {
            coords = [firstHecho.lat, firstHecho.lng];
            zoom = 12;
        }
    }

    // Crear nuevo mapa
    carouselMap = L.map('carousel-map').setView(coords, zoom);

    // Agregar tiles
    L.tileLayer(
        `https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=lsLLHrc2xX9ejIqZplai`,
        {
            attribution:
                '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://www.maptiler.com/">MapTiler</a>',
            maxZoom: 20,
            noWrap: true
        }
    ).addTo(carouselMap);

    // Agregar marcadores
    if (collection.hechos && collection.hechos.length > 0) {
        const bounds = [];

        collection.hechos.forEach((hecho, index) => {
            if (hecho.lat && hecho.lng) {
                const marker = L.marker([hecho.lat, hecho.lng]).addTo(carouselMap);

                // Crear popup mejorado con botón para ver detalles
                const popupContent = `
                    <div style="min-width: 200px;">
                        <b style="font-size: 16px; color: #333;">${hecho.titulo || 'Sin título'}</b><br/>
                        <p style="margin: 8px 0; color: #666; font-size: 14px;">${(hecho.descripcion || '').substring(0, 100)}${(hecho.descripcion || '').length > 100 ? '...' : ''}</p>
                        <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
                            <p style="margin: 5px 0; font-size: 13px;"><strong>Categoría:</strong> ${hecho.categoria?.nombre || 'Sin categoría'}</p>
                            <p style="margin: 5px 0; font-size: 13px;"><strong>Fecha:</strong> ${formatearFecha(hecho.fechaDelHecho)}</p>
                        </div>
                        <button onclick="mostrarDetalleHechoPorIndice(${currentCollectionIndex}, ${index})" 
                                style="margin-top: 10px; width: 100%; padding: 8px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600; transition: transform 0.2s;">
                            Ver Detalles Completos
                        </button>
                    </div>
                `;

                marker.bindPopup(popupContent);
                bounds.push([hecho.lat, hecho.lng]);
            }
        });

        // Ajustar vista para mostrar todos los marcadores
        if (bounds.length > 1) {
            carouselMap.fitBounds(bounds, { padding: [50, 50] });
        }
    }

    // Botón centrar
    addCenterButton(carouselMap, coords);
}

// Actualizar indicadores activos
function updateIndicators() {
    const indicators = document.querySelectorAll('.carousel-indicator');
    indicators.forEach((indicator, index) => {
        if (index === currentCollectionIndex) {
            indicator.classList.add('active');
        } else {
            indicator.classList.remove('active');
        }
    });
}

// Actualizar estado de botones de navegación
function updateNavigationButtons() {
    const prevBtn = document.getElementById('carousel-prev');
    const nextBtn = document.getElementById('carousel-next');

    if (prevBtn) {
        prevBtn.disabled = currentCollectionIndex === 0;
    }

    if (nextBtn) {
        nextBtn.disabled = currentCollectionIndex === collections.length - 1;
    }
}

// Función auxiliar para agregar botón de centrar
function addCenterButton(map, coords) {
    const btn = L.control({position: "topright"});
    btn.onAdd = () => {
        const b = L.DomUtil.create("button", "center-btn");
        b.innerHTML = "⦿";
        b.title = "Centrar mapa";
        b.onclick = () => map.setView(coords, map.getZoom());
        return b;
    };
    btn.addTo(map);
}

// Función para navegar a la página principal
function startNavigation() {
    window.location.href = '/homePage';
}

// Soporte para navegación con teclado en el carrusel
document.addEventListener('keydown', (e) => {
    if (collections.length === 0) return;

    if (e.key === 'ArrowLeft') {
        navigateCarousel(-1);
    } else if (e.key === 'ArrowRight') {
        navigateCarousel(1);
    }
});

// ====== FUNCIONES PARA MODAL DE DETALLES DEL HECHO ======

let hechoSeleccionado = null;

// Mostrar detalles completos del hecho en modal
function mostrarDetalleHecho(hecho) {
    // console.log('[Landing] Mostrando detalles del hecho:', hecho);

    hechoSeleccionado = hecho;

    // Actualizar título
    document.getElementById('hechoTitulo').textContent = hecho.titulo || 'Sin título';

    // Actualizar descripción
    document.getElementById('hechoDescripcion').textContent = hecho.descripcion || 'Sin descripción';

    // Actualizar categoría
    document.getElementById('hechoCategoria').textContent = hecho.categoria?.nombre || 'Sin categoría';

    // Actualizar ubicación
    const ubicacionText = hecho.ubicacion
        ? `${hecho.ubicacion.latitud.toFixed(4)}, ${hecho.ubicacion.longitud.toFixed(4)}`
        : 'Sin ubicación';
    document.getElementById('hechoUbicacion').textContent = ubicacionText;

    // Actualizar fecha del hecho
    document.getElementById('hechoFechaHecho').textContent = formatearFecha(hecho.fechaDelHecho);

    // Actualizar fecha de carga
    document.getElementById('hechoFechaCarga').textContent = formatearFechaHora(hecho.fechaDeCarga);

    // Actualizar fuente
    document.getElementById('hechoFuente').textContent = hecho.fuente?.moduloFuente?.nombre || 'Sin fuente';

    // Actualizar origen
    document.getElementById('hechoOrigen').textContent = hecho.origen || 'DESCONOCIDO';

    // Actualizar etiquetas
    const etiquetasText = hecho.etiquetas && hecho.etiquetas.length > 0
        ? hecho.etiquetas.map(e => e.nombre).join(', ')
        : 'Sin etiquetas';
    document.getElementById('hechoEtiquetas').textContent = etiquetasText;

    // Actualizar multimedia
    const multimediaContainer = document.getElementById('hechoMultimedia');
    if (hecho.contenido) {
        multimediaContainer.innerHTML = renderizarMultimedia(hecho.contenido);
    } else {
        multimediaContainer.innerHTML = '<p style="color: #999; font-style: italic;">Sin contenido multimedia</p>';
    }

    // Mostrar modal
    document.getElementById('hechoPopup').classList.remove('hidden');
    document.getElementById('hechoPopupOverlay').classList.add('active');
}

// Función auxiliar para mostrar hecho desde popup del mapa (usando índices)
// Esta función es llamada desde el HTML generado en los popups
window.mostrarDetalleHechoPorIndice = function(coleccionIndex, hechoIndex) {
    if (collections[coleccionIndex] && collections[coleccionIndex].hechos[hechoIndex]) {
        mostrarDetalleHecho(collections[coleccionIndex].hechos[hechoIndex]);
    }
};

// Cerrar modal de detalles del hecho
function cerrarDetalleHecho() {
    document.getElementById('hechoPopup').classList.add('hidden');
    document.getElementById('hechoPopupOverlay').classList.remove('active');
    hechoSeleccionado = null;
}

// Renderizar contenido multimedia
function renderizarMultimedia(contenido) {
    if (!contenido || !contenido.url) {
        return '<p style="color: #999; font-style: italic;">Sin contenido multimedia</p>';
    }

    const tipo = (contenido.tipo || contenido.tipoContenido || '').toLowerCase();
    const url = contenido.url;

    // Imagen
    if (tipo === 'imagen' || tipo === 'image') {
        return `
            <div style="margin-top: 15px;">
                <img src="${url}" alt="Imagen del hecho" 
                     style="max-width: 100%; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"
                     onerror="this.onerror=null; this.src='data:image/svg+xml,%3Csvg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'200\\' height=\\'200\\'%3E%3Crect fill=\\'%23ddd\\' width=\\'200\\' height=\\'200\\'/%3E%3Ctext x=\\'50%25\\' y=\\'50%25\\' dominant-baseline=\\'middle\\' text-anchor=\\'middle\\' font-family=\\'sans-serif\\' font-size=\\'14\\' fill=\\'%23999\\'%3EImagen no disponible%3C/text%3E%3C/svg%3E';" />
            </div>
        `;
    }

    // Video
    if (tipo === 'video') {
        return `
            <div style="margin-top: 15px;">
                <video controls preload="metadata" style="max-width: 100%; border-radius: 8px;">
                    <source src="${url}" type="video/mp4">
                    <source src="${url}" type="video/webm">
                    Tu navegador no soporta la reproducción de video.
                </video>
            </div>
        `;
    }

    // Audio
    if (tipo === 'audio') {
        return `
            <div style="margin-top: 15px; background: #f5f5f5; padding: 15px; border-radius: 8px;">
                <div style="margin-bottom: 10px; color: #666; font-weight: 600;">🎵 Audio</div>
                <audio controls preload="metadata" style="width: 100%;">
                    <source src="${url}" type="audio/mpeg">
                    <source src="${url}" type="audio/ogg">
                    <source src="${url}" type="audio/wav">
                    Tu navegador no soporta la reproducción de audio.
                </audio>
            </div>
        `;
    }

    // Texto
    if (tipo === 'texto' || tipo === 'text') {
        return `
            <div style="margin-top: 15px;">
                <a href="${url}" target="_blank" 
                   style="display: inline-block; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 6px; transition: background 0.2s;">
                    📄 Ver contenido de texto
                </a>
            </div>
        `;
    }

    // Tipo desconocido
    return `
        <div style="margin-top: 15px; background: #f0f0f0; padding: 15px; border-radius: 8px;">
            <div style="margin-bottom: 10px; color: #666;">Tipo: ${tipo || 'Desconocido'}</div>
            <a href="${url}" target="_blank" 
               style="display: inline-block; padding: 8px 16px; background: white; color: #007bff; text-decoration: none; border: 1px solid #007bff; border-radius: 4px;">
                Ver contenido
            </a>
        </div>
    `;
}

// Formatear fecha (solo fecha)
function formatearFecha(fecha) {
    if (!fecha) return 'Sin fecha';
    const date = new Date(fecha);
    if (isNaN(date.getTime())) return 'Fecha inválida';
    return date.toLocaleDateString('es-AR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// Formatear fecha y hora
function formatearFechaHora(fecha) {
    if (!fecha) return 'Sin fecha';
    const date = new Date(fecha);
    if (isNaN(date.getTime())) return 'Fecha inválida';
    return date.toLocaleString('es-AR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Ver hecho en el mapa (redirigir a visualizarHechos)
function verHechoEnMapa() {
    if (!hechoSeleccionado) {
        console.warn('No hay hecho seleccionado');
        return;
    }

    // Obtener la colección actual del carrusel
    const coleccionActual = collections[currentCollectionIndex];

    if (!coleccionActual) {
        console.warn('No se pudo obtener la colección actual');
        return;
    }

    // Obtener el handle de la colección (identificador único)
    const coleccionHandle = coleccionActual.handle;

    if (!coleccionHandle) {
        console.warn('La colección no tiene handle disponible:', coleccionActual);
        return;
    }

    // console.log('✅ Redirigiendo a visualizar hechos de colección:', coleccionActual.titulo, 'Handle:', coleccionHandle);

    // Redirigir a visualizarHechos con el handle de la colección
    window.location.href = `/visualizarHechos?coleccion=${coleccionHandle}`;
}

// Inicializar event listeners para el modal
document.addEventListener('DOMContentLoaded', () => {
    // Botón cerrar modal
    const btnCerrar = document.getElementById('cerrarHechoPopup');
    if (btnCerrar) {
        btnCerrar.addEventListener('click', cerrarDetalleHecho);
    }

    // Overlay - cerrar al hacer clic fuera
    const overlay = document.getElementById('hechoPopupOverlay');
    if (overlay) {
        overlay.addEventListener('click', cerrarDetalleHecho);
    }

    // Botón "Ver en Mapa"
    const btnVerEnMapa = document.getElementById('btnVerEnMapa');
    if (btnVerEnMapa) {
        btnVerEnMapa.addEventListener('click', verHechoEnMapa);
    }

    // Cerrar con tecla ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            cerrarDetalleHecho();
        }
    });
});
