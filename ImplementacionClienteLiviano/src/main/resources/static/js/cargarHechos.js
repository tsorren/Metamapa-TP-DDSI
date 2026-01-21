// cargarHechos.js
let map, addingHecho = false, tempMarker = null;
let multimediaId = null; // ID del contenido multimedia subido
let myHechosMarkers = []; // Array para guardar los marcadores de mis hechos
let myHechosLayer = null; // Layer group para los hechos del usuario
let showMyHechos = false; // Estado por defecto: oculto (solo marcador temporal)
let isUserAuthenticated = false; // Flag para saber si el usuario está autenticado

function initMap() {
    map = L.map("map").setView([-34.61, -58.38], 4);
    L.tileLayer(
        `https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=lsLLHrc2xX9ejIqZplai`,
        {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
            maxZoom: 20,
            noWrap: true
        }
    ).addTo(map);

    map.on("click", (e) => {
        if (!addingHecho) return;

        // Limpiar el marcador temporal anterior
        if (tempMarker) {
            map.removeLayer(tempMarker);
            tempMarker = null;
        }

        // Crear el nuevo marcador temporal con un icono distintivo
        const tempIcon = L.divIcon({
            className: 'temp-marker-icon',
            html: `<div style="background-color: #ff6b6b; width: 30px; height: 30px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3);"></div>`,
            iconSize: [30, 30],
            iconAnchor: [15, 15]
        });

        tempMarker = L.marker(e.latlng, { icon: tempIcon }).addTo(map);

        const coordenadasInput = document.getElementById("coordenadas");
        if (coordenadasInput) {
            coordenadasInput.value = `${e.latlng.lat},${e.latlng.lng}`;
        }

        const formWrap = document.getElementById("hechoForm");
        if (formWrap) {
            formWrap.classList.remove("hidden");
            formWrap.classList.add("active");
            console.log("[Cargar Hecho] Formulario mostrado");
        } else {
            console.error("[Cargar Hecho] No se encontró el elemento hechoForm");
        }
    });
}

function wireHechoForm() {
    const addBtn = document.getElementById("addHechoBtn");
    const formWrap = document.getElementById("hechoForm");
    const form = document.getElementById("hechoData");
    const cancel1 = document.getElementById("cancelBtn");
    const cancel2 = document.getElementById("cancelBtn2");

    // Elementos multimedia
    const tipoContenido = document.getElementById("tipoContenido");
    const contenedorArchivo = document.getElementById("contenedorArchivo");
    const contenedorTexto = document.getElementById("contenedorTexto");
    const archivoInput = document.getElementById("archivoMultimedia");
    const textoInput = document.getElementById("textoContenido");
    const fileInfo = document.getElementById("file-info-hecho");
    const fileName = document.getElementById("file-name-hecho");
    const removeFile = document.getElementById("remove-file-hecho");
    const progressBar = document.getElementById("progress-bar-hecho");

    if (!addBtn || !formWrap || !form) return;

    addBtn.addEventListener("click", () => {
        addingHecho = true;
        if (window.Toast) {
            Toast.info("Hacé clic en el mapa para seleccionar la ubicación del nuevo hecho.");
        }
    });

    // Cambiar tipo de contenido
    tipoContenido?.addEventListener("change", (e) => {
        const tipo = e.target.value;

        if (tipo === "TEXTO") {
            contenedorArchivo.classList.add("hidden");
            contenedorTexto.classList.remove("hidden");
        } else if (tipo !== "") {
            contenedorArchivo.classList.remove("hidden");
            contenedorTexto.classList.add("hidden");

            // Actualizar accept según tipo
            let accept = "*/*";
            switch(tipo) {
                case "IMAGEN":
                    accept = "image/*";
                    break;
                case "VIDEO":
                    accept = "video/*";
                    break;
                case "AUDIO":
                    accept = "audio/*";
                    break;
            }
            archivoInput.setAttribute("accept", accept);
        } else {
            contenedorArchivo.classList.add("hidden");
            contenedorTexto.classList.add("hidden");
        }

        resetMultimedia();
    });

    // Función para detectar tipo de contenido
    function detectarTipoContenido(file) {
        const tipo = file.type.toLowerCase();
        if (tipo.startsWith('image/')) return 'IMAGEN';
        if (tipo.startsWith('video/')) return 'VIDEO';
        if (tipo.startsWith('audio/')) return 'AUDIO';
        if (tipo.includes('text/')) return 'TEXTO';
        return 'IMAGEN'; // Default
    }

    // Subir archivo
    archivoInput?.addEventListener("change", async () => {
        if (archivoInput.files.length > 0) {
            const file = archivoInput.files[0];
            fileName.textContent = file.name;
            fileInfo.classList.remove("hidden");

            // Simular progreso
            progressBar.style.width = "0%";
            let progress = 0;

            const interval = setInterval(() => {
                if (progress >= 90) {
                    clearInterval(interval);
                } else {
                    progress += 10;
                    progressBar.style.width = progress + "%";
                }
            }, 100);

            try {
                const formData = new FormData();
                formData.append("archivo", file);
                formData.append("tipoContenido", detectarTipoContenido(file));

                const response = await fetch('/multimedia/upload', {
                    method: 'POST',
                    body: formData,
                    credentials: 'include'
                });

                if (response.ok) {
                    const multimedia = await response.json();
                    multimediaId = multimedia.id;
                    progressBar.style.width = "100%";
                    clearInterval(interval);
                    console.log('Archivo subido con ID:', multimediaId);
                } else {
                    if (window.Toast) {
                        Toast.error('Error al subir archivo');
                    }
                    resetMultimedia();
                }
            } catch (error) {
                console.error('Error:', error);
                if (window.Toast) {
                    Toast.error('Error de conexión al subir archivo');
                }
                resetMultimedia();
            }
        }
    });

    // Eliminar archivo
    removeFile?.addEventListener("click", resetMultimedia);

    function resetMultimedia() {
        archivoInput.value = "";
        textoInput.value = "";
        fileInfo.classList.add("hidden");
        progressBar.style.width = "0%";
        multimediaId = null;
    }

    function cancelar() {
        form.reset();
        formWrap.classList.remove("active");
        formWrap.classList.add("hidden");
        resetMultimedia();
        if (tempMarker) {
            map.removeLayer(tempMarker);
            tempMarker = null;
        }
        addingHecho = false;
        console.log("[Cargar Hecho] Formulario cerrado");
    }

    if (cancel1) cancel1.addEventListener("click", cancelar);
    if (cancel2) cancel2.addEventListener("click", cancelar);

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        if (!tempMarker) {
            if (window.Toast) {
                Toast.warning('Por favor, selecciona una ubicación en el mapa primero.');
            }
            return;
        }

        const formData = new FormData(form);
        const latlng = tempMarker.getLatLng();

        // Procesar contenido multimedia
        let idContenido = multimediaId;

        if (tipoContenido.value === "TEXTO" && textoInput.value.trim()) {
            try {
                const response = await fetch('/multimedia/texto', {
                    method: 'POST',
                    headers: { 'Content-Type': 'text/plain' },
                    body: textoInput.value
                });

                if (response.ok) {
                    const multimedia = await response.json();
                    idContenido = multimedia.id;
                } else {
                    if (window.Toast) {
                        Toast.error('Error al guardar texto');
                    }
                    return;
                }
            } catch (error) {
                console.error('Error:', error);
                if (window.Toast) {
                    Toast.error('Error al guardar texto');
                }
                return;
            }
        }

        const body = {
             titulo: formData.get('titulo'),
             descripcion: formData.get('descripcion'),
             categoria: formData.get('categoria'),
             latitud: latlng.lat,
             longitud: latlng.lng,
             fechaDelHecho: formData.get('fechaDelHecho'),
             idContenido: idContenido
         };

         try {
             const response = await fetch('/cargarHecho', {
                 method: 'POST',
                 headers: { 'Content-Type': 'application/json' },
                 body: JSON.stringify(body),
                 credentials: 'include'
             });

             if (response.ok) {
                const hechoCreado = await response.json();

                if (window.Toast) {
                    Toast.success('Hecho creado correctamente');
                }

                // Recargar los hechos del usuario para mostrar el nuevo
                setTimeout(() => {
                    loadMyHechos();
                }, 500);

                cancelar();
            } else {
                const errorText = await response.text();
                if (window.Toast) {
                    Toast.error('Error al crear el hecho: ' + errorText);
                }
            }
         } catch (error) {
             console.error('Error:', error);
             if (window.Toast) {
                 Toast.error('Error de conexión con el servidor: ' + error.message);
             }
         }
    });
}

// ========== FUNCIONES PARA MOSTRAR HECHOS DEL USUARIO ==========

async function loadMyHechos() {
    try {
        const response = await fetch('/hechos-creados/api/hechos', {
            credentials: 'include'
        });

        if (response.ok) {
            const data = await response.json();
            const hechos = data.hechos || [];
            console.log("[Mis Hechos] Hechos cargados:", hechos.length);
            isUserAuthenticated = true;
            displayMyHechos(hechos);

            // Mostrar el botón solo si el usuario está autenticado
            const toggleBtn = document.getElementById('toggleMyHechosBtn');
            if (toggleBtn) {
                toggleBtn.style.display = 'flex';
            }
        } else if (response.status === 401 || response.status === 403) {
            console.log("[Mis Hechos] Usuario no autenticado");
            isUserAuthenticated = false;
            hideToggleButton();
        } else if (response.status === 404) {
            console.log("[Mis Hechos] Endpoint no disponible (404)");
            isUserAuthenticated = false;
            hideToggleButton();
        } else {
            console.log("[Mis Hechos] Error al cargar:", response.status);
            isUserAuthenticated = false;
            hideToggleButton();
        }
    } catch (error) {
        console.log("[Mis Hechos] Error de conexión:", error.message);
        isUserAuthenticated = false;
        hideToggleButton();
    }
}

function hideToggleButton() {
    const toggleBtn = document.getElementById('toggleMyHechosBtn');
    if (toggleBtn) {
        toggleBtn.style.display = 'none';
    }
}

function displayMyHechos(hechos) {
    console.log("[Mis Hechos] === Iniciando displayMyHechos ===");
    console.log("[Mis Hechos] Cantidad de hechos recibidos:", hechos.length);

    // Log del primer hecho para ver su estructura
    if (hechos.length > 0) {
        console.log("[Mis Hechos] Estructura del primer hecho:", JSON.stringify(hechos[0], null, 2));
    }

    // Limpiar marcadores anteriores
    clearMyHechos();

    // Crear layer group si no existe
    if (!myHechosLayer) {
        myHechosLayer = L.layerGroup();
    }

    // Agregar marcadores para cada hecho
    hechos.forEach(hecho => {
        // Verificar si tiene ubicación
        if (!hecho.ubicacion) {
            console.warn("[Mis Hechos] Hecho sin ubicación:", hecho.identificador);
            return;
        }

        let lat, lng;

        // Caso 1: ubicacion tiene latitud y longitud como propiedades separadas
        if (hecho.ubicacion.latitud !== undefined && hecho.ubicacion.longitud !== undefined) {
            lat = hecho.ubicacion.latitud;
            lng = hecho.ubicacion.longitud;
            console.log(`[Mis Hechos] Hecho ${hecho.identificador} - Lat: ${lat}, Lng: ${lng}`);
        }
        // Caso 2: ubicacion tiene coordenadas como string "lat,lng"
        else if (hecho.ubicacion.coordenadas && typeof hecho.ubicacion.coordenadas === 'string') {
            [lat, lng] = hecho.ubicacion.coordenadas.split(',').map(Number);
            console.log(`[Mis Hechos] Hecho ${hecho.identificador} - Coordenadas string: ${lat}, ${lng}`);
        }
        // Caso 3: ubicacion.coordenadas es un objeto con latitud/longitud
        else if (hecho.ubicacion.coordenadas && hecho.ubicacion.coordenadas.latitud !== undefined) {
            lat = hecho.ubicacion.coordenadas.latitud;
            lng = hecho.ubicacion.coordenadas.longitud;
            console.log(`[Mis Hechos] Hecho ${hecho.identificador} - Coordenadas objeto: ${lat}, ${lng}`);
        }
        else {
            console.warn("[Mis Hechos] Formato de ubicación no reconocido:", hecho.ubicacion);
            return;
        }

        // Validar coordenadas
        if (isNaN(lat) || isNaN(lng) || lat === null || lng === null) {
            console.warn("[Mis Hechos] Coordenadas inválidas para hecho:", hecho.identificador, "lat:", lat, "lng:", lng);
            return;
        }

        // Crear icono personalizado para hechos del usuario
        const customIcon = L.divIcon({
            className: 'custom-marker-icon',
            html: `<div style="background-color: #48bb78; width: 30px; height: 30px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3);"></div>`,
            iconSize: [30, 30],
            iconAnchor: [15, 15]
        });

        const marker = L.marker([lat, lng], { icon: customIcon });

        // Crear popup con información del hecho
        const popupContent = createHechoPopup(hecho);
        marker.bindPopup(popupContent);

        myHechosMarkers.push(marker);
        marker.addTo(myHechosLayer);
        console.log(`[Mis Hechos] Marcador creado exitosamente en [${lat}, ${lng}]`);
    });

    console.log("[Mis Hechos] Marcadores creados:", myHechosMarkers.length);

    // NO mostrar automáticamente, solo si showMyHechos es true
    if (showMyHechos && myHechosLayer) {
        myHechosLayer.addTo(map);
        console.log("[Mis Hechos] Layer agregado al mapa");
    }
}

function createHechoPopup(hecho) {
    let multimedia = '';
    if (hecho.contenido) {
        const contenido = hecho.contenido;
        switch(contenido.tipo) {
            case 'IMAGEN':
                multimedia = `<img src="${contenido.url}" alt="Imagen" style="max-width: 100%; max-height: 200px; border-radius: 8px; margin-top: 10px;">`;
                break;
            case 'VIDEO':
                multimedia = `<video controls style="max-width: 100%; max-height: 200px; border-radius: 8px; margin-top: 10px;">
                    <source src="${contenido.url}" type="video/mp4">
                </video>`;
                break;
            case 'AUDIO':
                multimedia = `<audio controls style="width: 100%; margin-top: 10px;">
                    <source src="${contenido.url}" type="audio/mpeg">
                </audio>`;
                break;
            case 'TEXTO':
                multimedia = `<p style="margin-top: 10px; font-style: italic; color: #555;">${contenido.texto || ''}</p>`;
                break;
        }
    }

    const categoriaNombre = hecho.categoria && hecho.categoria.nombre ? hecho.categoria.nombre : '';

    return `
        <div style="min-width: 200px;">
            <h3 style="margin: 0 0 10px 0; color: #48bb78; font-size: 16px;">${hecho.titulo || 'Sin título'}</h3>
            ${categoriaNombre ? `<p style="margin: 5px 0;"><strong>Categoría:</strong> ${categoriaNombre}</p>` : ''}
            ${hecho.descripcion ? `<p style="margin: 5px 0;"><strong>Descripción:</strong> ${hecho.descripcion}</p>` : ''}
            ${hecho.fechaDelHecho ? `<p style="margin: 5px 0;"><strong>Fecha:</strong> ${new Date(hecho.fechaDelHecho).toLocaleDateString()}</p>` : ''}
            ${multimedia}
            <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #ddd;">
                <small style="color: #666;">Mis hechos</small>
            </div>
        </div>
    `;
}

function clearMyHechos() {
    if (myHechosLayer) {
        myHechosLayer.clearLayers();
    }
    myHechosMarkers = [];
}

function toggleMyHechos() {
    showMyHechos = !showMyHechos;

    const toggleBtn = document.getElementById('toggleMyHechosBtn');

    if (showMyHechos) {
        // Mostrar todos los hechos del usuario
        if (myHechosLayer) {
            myHechosLayer.addTo(map);
            console.log("[Mis Hechos] Mostrando hechos en el mapa");
        }
        if (toggleBtn) {
            toggleBtn.classList.add('active');
            toggleBtn.setAttribute('title', 'Ocultar mis hechos');
        }
        if (window.Toast) {
            Toast.info('Mostrando todos tus hechos en el mapa');
        }
    } else {
        // Ocultar todos los hechos del usuario
        if (myHechosLayer && map.hasLayer(myHechosLayer)) {
            map.removeLayer(myHechosLayer);
            console.log("[Mis Hechos] Ocultando hechos del mapa");
        }
        if (toggleBtn) {
            toggleBtn.classList.remove('active');
            toggleBtn.setAttribute('title', 'Ver mis hechos');
        }
        if (window.Toast) {
            Toast.info('Ocultando tus hechos del mapa');
        }
    }
}

function setupToggleMyHechosButton() {
    const toggleBtn = document.getElementById('toggleMyHechosBtn');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', toggleMyHechos);
    }
}

// Modificar la función de envío del formulario para recargar los hechos después de crear uno nuevo
function refreshMyHechosAfterCreate() {
    loadMyHechos();
}

document.addEventListener("DOMContentLoaded", () => {
    initMap();
    wireHechoForm();
    setupToggleMyHechosButton();

    // Cargar hechos del usuario después de que el mapa esté listo
    setTimeout(() => {
        loadMyHechos();
    }, 500);
});
