/**
 * Hechos Creados - Gestión completa con backend integration
 */

(function () {
    if (window.__HECHOS_CREADOS_INIT__) return;
    window.__HECHOS_CREADOS_INIT__ = true;

    // Suprimir warnings de autoplay de audio/video
    // Estos son comportamientos esperados del navegador, no errores reales
    const originalError = window.console.error;
    window.console.error = function(...args) {
        const errorMsg = args[0]?.toString() || '';
        // Ignorar errores conocidos de autoplay que no son críticos
        if (errorMsg.includes('play()') && errorMsg.includes('interrupted')) {
            return; // Silenciar este warning específico
        }
        originalError.apply(console, args);
    };

    // TODO: Obtener ID del contribuyente de la sesión actual

    let hechosActuales = [];

    document.addEventListener('DOMContentLoaded', function () {
        // console.log("[hechosCreados] Inicializando...");
        cargarHechos();
        inicializarEventos();

        // Agregar listener global para prevenir autoplay accidental
        document.addEventListener('play', function(e) {
            if (e.target.tagName === 'VIDEO' || e.target.tagName === 'AUDIO') {
                // Permitir solo si el usuario hizo clic directamente
                if (!e.isTrusted) {
                    e.preventDefault();
                    e.stopPropagation();
                }
            }
        }, true);
    });

    /**
     * Cargar hechos del contribuyente desde el backend
     */
    async function cargarHechos() {
        try {
            mostrarCargando(true);

            const response = await fetch('/hechos-creados/api/hechos', {
                credentials: 'include'
            });
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }

            const data = await response.json();

            if (data.success) {
                hechosActuales = data.hechos;
                renderizarHechos(data.hechos);
            } else {
                throw new Error(data.error || 'Error desconocido');
            }

            mostrarCargando(false);
        } catch (error) {
            console.error('Error al cargar hechos:', error);
            mostrarError('Error al cargar los hechos. Por favor, intenta de nuevo.');
            mostrarCargando(false);
        }
    }

    /**
     * Renderizar hechos en el DOM
     */
    function renderizarHechos(hechos) {
        const main = document.querySelector('main');
        main.innerHTML = '';

        if (hechos.length === 0) {
            main.innerHTML = `
                <div class="no-hechos">
                    <p>No has creado ningún hecho aún.</p>
                    <a class="btn-crear" href="/cargarHecho">Crear mi primer hecho</a>
                </div>
            `;
            return;
        }

        hechos.forEach(hecho => {
            const hechoCard = crearCardHecho(hecho);
            main.appendChild(hechoCard);
        });
    }

    /**
     * Crear card HTML para un hecho
     */
    function crearCardHecho(hecho) {
        const container = document.createElement('div');
        container.className = 'hecho-card';
        container.dataset.id = hecho.identificador;

        const puedeModificar = verificarPuedeModificar(hecho);
        const diasRestantes = calcularDiasRestantes(hecho.fechaDeCarga);

        container.innerHTML = `
            <div class="hecho-info">
                <h3>${hecho.titulo || 'Sin título'}</h3>
                <p><strong>Categoría:</strong> ${hecho.categoria?.nombre || 'Sin categoría'}</p>
                <p><strong>Descripción:</strong></p>
                <p class="descripcion">${hecho.descripcion || 'Sin descripción'}</p>
                <p><strong>Ubicación:</strong> Lat: ${hecho.ubicacion?.latitud || '-'}, Lng: ${hecho.ubicacion?.longitud || '-'}</p>
                <p><strong>Fecha del hecho:</strong> ${formatearFecha(hecho.fechaDelHecho)}</p>
                <p><strong>Fecha de carga:</strong> ${formatearFechaHora(hecho.fechaDeCarga)}</p>
                ${hecho.fechaUltimaModificacion ? `<p><strong>Última modificación:</strong> ${formatearFechaHora(hecho.fechaUltimaModificacion)}</p>` : ''}
                <p><strong>Visible:</strong> ${hecho.visible ? 'Sí' : 'No'}</p>
                ${puedeModificar ? `<p class="plazo-modificacion">⏰ Puedes modificar este hecho durante ${diasRestantes} día(s) más</p>` : ''}
                ${!puedeModificar && diasRestantes <= 0 ? `<p class="plazo-vencido">⚠️ Plazo de modificación vencido</p>` : ''}
                ${hecho.contenido ? renderizarMultimedia(hecho.contenido) : ''}
                ${hecho.etiquetas && hecho.etiquetas.length > 0 ? renderizarEtiquetas(hecho.etiquetas) : ''}
            </div>
            <div class="acciones">
                <button class="btn-edit ${!puedeModificar ? 'disabled' : ''}" 
                        data-id="${hecho.identificador}" 
                        title="${puedeModificar ? 'Solicitar modificación' : 'Plazo de modificación vencido'}"
                        ${!puedeModificar ? 'disabled' : ''}>
                    <img src="/images/edit.svg" alt="Editar">
                    <span>Modificar</span>
                </button>
                <button class="btn-delete" data-id="${hecho.identificador}" title="Solicitar eliminación">
                    <img src="/images/trash.svg" alt="Eliminar">
                    <span>Eliminar</span>
                </button>
            </div>
        `;

        return container;
    }

    /**
     * Detectar tipo de contenido por extensión de archivo
     */
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

    /**
     * Renderizar multimedia
     */
    function renderizarMultimedia(contenido) {
        if (!contenido || !contenido.url) {
            return '<div class="multimedia-empty" style="color: #999; font-style: italic; margin-top: 10px;">Sin contenido multimedia</div>';
        }

        // El backend puede enviar 'tipo' o 'tipoContenido'
        let tipoRaw = contenido.tipo || contenido.tipoContenido || '';
        let tipo = tipoRaw.toString().toLowerCase();

        // Si no hay tipo, detectar por extensión de archivo
        if (!tipo || tipo === '') {
            tipo = detectarTipoPorExtension(contenido.url) || '';
            tipoRaw = tipo.toUpperCase();
        }

        // console.log('[hechosCreados] Renderizando multimedia:', { tipo, tipoRaw, url: contenido.url });

        // Imagen
        if (tipo === 'imagen' || tipo === 'image') {
            return `<div class="multimedia multimedia-image" style="margin-top: 10px;">
                <img src="${contenido.url}" alt="${contenido.descripcion || 'Imagen del hecho'}" 
                     style="max-width: 100%; max-height: 300px; border-radius: 8px; display: block; margin: 0 auto;"
                     onerror="this.onerror=null; this.src='data:image/svg+xml,%3Csvg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'200\\' height=\\'200\\'%3E%3Crect fill=\\'%23ddd\\' width=\\'200\\' height=\\'200\\'/%3E%3Ctext x=\\'50%25\\' y=\\'50%25\\' dominant-baseline=\\'middle\\' text-anchor=\\'middle\\' font-family=\\'sans-serif\\' font-size=\\'14\\' fill=\\'%23999\\'%3EImagen no disponible%3C/text%3E%3C/svg%3E';" />
            </div>`;
        }

        // Video
        if (tipo === 'video') {
            return `<div class="multimedia multimedia-video" style="margin-top: 10px;">
                <video controls preload="metadata" style="max-width: 100%; max-height: 300px; border-radius: 8px; display: block; margin: 0 auto;">
                    <source src="${contenido.url}" type="video/mp4">
                    <source src="${contenido.url}" type="video/webm">
                    Tu navegador no soporta la reproducción de video.
                </video>
            </div>`;
        }

        // Audio
        if (tipo === 'audio') {
            return `<div class="multimedia multimedia-audio" style="margin-top: 10px;">
                <div style="background: #f5f5f5; padding: 15px; border-radius: 8px; text-align: center;">
                    <div style="margin-bottom: 10px; color: #666;">🎵 Contenido de Audio</div>
                    <audio controls preload="metadata" style="width: 100%; max-width: 400px;">
                        <source src="${contenido.url}" type="audio/mpeg">
                        <source src="${contenido.url}" type="audio/ogg">
                        <source src="${contenido.url}" type="audio/wav">
                        Tu navegador no soporta la reproducción de audio.
                    </audio>
                </div>
            </div>`;
        }

        // Texto - Cargar y mostrar inline
        if (tipo === 'texto' || tipo === 'text') {
            // Crear un contenedor con un botón para cargar el texto
            const contenedorId = 'texto-' + Math.random().toString(36).substr(2, 9);
            setTimeout(() => cargarTextoInline(contenedorId, contenido.url), 100);

            return `<div class="multimedia multimedia-text" style="margin-top: 10px;">
                <div style="background: #f9f9f9; padding: 15px; border-radius: 8px; border: 1px solid #e0e0e0;">
                    <div style="margin-bottom: 10px; color: #666; font-weight: bold;">📄 Contenido de Texto</div>
                    <button onclick="cargarTextoInline('${contenedorId}', '${contenido.url}')" 
                            style="padding: 8px 16px; background: #3498db; color: white; border: none; border-radius: 4px; cursor: pointer;">
                        Ver contenido
                    </button>
                    <div id="${contenedorId}" style="margin-top: 10px; display: none; max-height: 300px; overflow-y: auto; text-align: left; padding: 10px; background: white; border-radius: 4px;"></div>
                </div>
            </div>`;
        }

        // Tipo desconocido
        return `<div class="multimedia multimedia-unknown" style="margin-top: 10px;">
            <div style="background: #f0f0f0; padding: 15px; border-radius: 8px; text-align: center;">
                <div style="margin-bottom: 10px; color: #666;">Tipo: ${tipoRaw || 'Desconocido'}</div>
                <div style="margin-bottom: 10px; font-size: 12px; color: #999;">URL: ${contenido.url}</div>
                <a href="${contenido.url}" target="_blank" class="btn-ver-multimedia" 
                   style="display: inline-block; padding: 8px 16px; background: #3498db; color: white; text-decoration: none; border-radius: 4px;">
                    Ver contenido
                </a>
            </div>
        </div>`;
    }

    /**
     * Cargar texto inline sin redireccionar
     */
    window.cargarTextoInline = async function(contenedorId, url) {
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

    /**
     * Renderizar etiquetas
     */
    function renderizarEtiquetas(etiquetas) {
        const etiquetasHTML = etiquetas.map(e => `<span class="etiqueta">${e.nombre}</span>`).join('');
        return `<div class="etiquetas"><strong>Etiquetas:</strong> ${etiquetasHTML}</div>`;
    }

    /**
     * Verificar si puede modificar (7 días desde la carga)
     */
    function verificarPuedeModificar(hecho) {
        if (!hecho.fechaDeCarga) return false;

        const fechaCarga = new Date(hecho.fechaDeCarga);
        const fechaLimite = new Date(fechaCarga);
        fechaLimite.setDate(fechaLimite.getDate() + 7);

        return new Date() < fechaLimite;
    }

    /**
     * Calcular días restantes para modificar
     */
    function calcularDiasRestantes(fechaDeCarga) {
        if (!fechaDeCarga) return 0;

        const fechaCarga = new Date(fechaDeCarga);
        const fechaLimite = new Date(fechaCarga);
        fechaLimite.setDate(fechaLimite.getDate() + 7);

        const diff = fechaLimite - new Date();
        return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
    }

    /**
     * Función para detectar tipo de contenido desde un archivo
     */
    function detectarTipoContenido(file) {
        const tipo = file.type.toLowerCase();
        if (tipo.startsWith('image/')) return 'IMAGEN';
        if (tipo.startsWith('video/')) return 'VIDEO';
        if (tipo.startsWith('audio/')) return 'AUDIO';
        if (tipo.includes('text/')) return 'TEXTO';
        return 'IMAGEN'; // Default
    }

    /**
     * Inicializar eventos
     */
    function inicializarEventos() {
        document.addEventListener('click', async function(e) {
            // Editar (solicitar modificación)
            if (e.target.closest('.btn-edit') && !e.target.closest('.btn-edit').disabled) {
                const btn = e.target.closest('.btn-edit');
                const idHecho = btn.dataset.id;
                await abrirModalModificar(idHecho);
            }

            // Eliminar (solicitar eliminación)
            if (e.target.closest('.btn-delete')) {
                const btn = e.target.closest('.btn-delete');
                const idHecho = btn.dataset.id;
                abrirModalEliminar(idHecho);
            }
        });

        // Cerrar modales con ESC
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                cerrarModales();
            }
        });
    }


    /**
     * Abrir modal para modificar hecho
     */
    async function abrirModalModificar(idHecho) {
        const hecho = hechosActuales.find(h => h.identificador == idHecho);
        if (!hecho) return;

        const multimediaActual = hecho.contenido ? `
            <div class="multimedia-actual">
                <p><strong>Contenido actual:</strong> ${hecho.contenido.tipo}</p>
                ${renderizarMultimedia(hecho.contenido)}
            </div>
        ` : '<p>Sin contenido multimedia</p>';

        // Crear modal dinámicamente
        const modal = document.createElement('div');
        modal.className = 'popup show';
        modal.id = 'popup-edit';
        modal.innerHTML = `
            <div class="popup-content" style="max-height: 90vh; overflow-y: auto;">
                <h2>Solicitar Modificación de Hecho</h2>
                <p class="warning">⚠️ Solo puedes modificar hechos durante los primeros 7 días desde su creación.</p>
                <form id="form-modificar">
                    <label for="edit-titulo">Título *</label>
                    <input type="text" id="edit-titulo" value="${hecho.titulo || ''}" required>

                    <label for="edit-descripcion">Descripción *</label>
                    <textarea id="edit-descripcion" rows="4" required>${hecho.descripcion || ''}</textarea>

                    <label for="edit-fecha">Fecha del hecho *</label>
                    <input type="date" id="edit-fecha" value="${hecho.fechaDelHecho || ''}" required max="${new Date().toISOString().split('T')[0]}">

                    <label for="edit-latitud">Latitud</label>
                    <input type="number" step="0.000001" id="edit-latitud" value="${hecho.ubicacion?.latitud || ''}">

                    <label for="edit-longitud">Longitud</label>
                    <input type="number" step="0.000001" id="edit-longitud" value="${hecho.ubicacion?.longitud || ''}">

                    <hr style="margin: 20px 0; border: 1px solid #ddd;">
                    
                    <h3>Contenido Multimedia</h3>
                    ${multimediaActual}
                    
                    <label>
                        <input type="checkbox" id="cambiar-multimedia"> Cambiar contenido multimedia
                    </label>

                    <div id="nueva-multimedia" class="hidden" style="margin-top: 15px;">
                        <label for="edit-tipo-contenido">Tipo de Contenido</label>
                        <select id="edit-tipo-contenido">
                            <option value="">Sin contenido multimedia</option>
                            <option value="TEXTO">Texto</option>
                            <option value="IMAGEN">Imagen</option>
                            <option value="VIDEO">Video</option>
                            <option value="AUDIO">Audio</option>
                        </select>

                        <div id="edit-contenedor-archivo" class="hidden" style="margin-top: 10px;">
                            <label for="edit-archivo">Seleccionar archivo</label>
                            <input type="file" id="edit-archivo" accept="*/*">
                            <div id="edit-file-info" class="file-info hidden">
                                <div class="file-details">
                                    <span id="edit-file-name"></span>
                                    <button type="button" id="edit-remove-file" class="remove-file">×</button>
                                </div>
                                <div class="progress-container">
                                    <div id="edit-progress-bar" class="progress-bar"></div>
                                </div>
                            </div>
                        </div>

                        <div id="edit-contenedor-texto" class="hidden" style="margin-top: 10px;">
                            <label for="edit-texto-contenido">Contenido de Texto</label>
                            <textarea id="edit-texto-contenido" rows="4" placeholder="Ingrese el texto aquí..."></textarea>
                        </div>
                    </div>

                    <div class="popup-actions" style="margin-top: 20px;">
                        <button type="submit" class="btn-save">Solicitar Modificación</button>
                        <button type="button" class="btn-cancel">Cancelar</button>
                    </div>
                </form>
            </div>
        `;

        document.body.appendChild(modal);

        // Variable para guardar el ID del nuevo multimedia
        let nuevoMultimediaId = null;

        // Manejar checkbox de cambiar multimedia
        const checkboxCambiar = modal.querySelector('#cambiar-multimedia');
        const divNuevaMultimedia = modal.querySelector('#nueva-multimedia');

        checkboxCambiar.addEventListener('change', function() {
            if (this.checked) {
                divNuevaMultimedia.classList.remove('hidden');
            } else {
                divNuevaMultimedia.classList.add('hidden');
                nuevoMultimediaId = null;
            }
        });

        // Manejar cambio de tipo de contenido
        const tipoContenido = modal.querySelector('#edit-tipo-contenido');
        const contenedorArchivo = modal.querySelector('#edit-contenedor-archivo');
        const contenedorTexto = modal.querySelector('#edit-contenedor-texto');
        const archivoInput = modal.querySelector('#edit-archivo');

        tipoContenido.addEventListener('change', function() {
            const tipo = this.value;

            if (tipo === 'TEXTO') {
                contenedorArchivo.classList.add('hidden');
                contenedorTexto.classList.remove('hidden');
            } else if (tipo !== '') {
                contenedorArchivo.classList.remove('hidden');
                contenedorTexto.classList.add('hidden');

                // Actualizar accept según tipo
                let accept = '*/*';
                switch(tipo) {
                    case 'IMAGEN':
                        accept = 'image/*';
                        break;
                    case 'VIDEO':
                        accept = 'video/*';
                        break;
                    case 'AUDIO':
                        accept = 'audio/*';
                        break;
                }
                archivoInput.setAttribute('accept', accept);
            } else {
                contenedorArchivo.classList.add('hidden');
                contenedorTexto.classList.add('hidden');
            }

            nuevoMultimediaId = null;
        });

        // Manejar subida de archivo
        archivoInput.addEventListener('change', async function() {
            if (this.files.length > 0) {
                const file = this.files[0];
                const fileInfo = modal.querySelector('#edit-file-info');
                const fileName = modal.querySelector('#edit-file-name');
                const progressBar = modal.querySelector('#edit-progress-bar');

                fileName.textContent = file.name;
                fileInfo.classList.remove('hidden');
                progressBar.style.width = '0%';

                try {
                    const formData = new FormData();
                    formData.append('archivo', file);
                    formData.append('tipoContenido', detectarTipoContenido(file));

                    // Simular progreso
                    let progress = 0;
                    const interval = setInterval(() => {
                        if (progress >= 90) {
                            clearInterval(interval);
                        } else {
                            progress += 10;
                            progressBar.style.width = progress + '%';
                        }
                    }, 100);

                    const response = await fetch('/multimedia/upload', {
                        method: 'POST',
                        body: formData
                    });

                    if (response.ok) {
                        const multimedia = await response.json();
                        nuevoMultimediaId = multimedia.id;
                        progressBar.style.width = '100%';
                        clearInterval(interval);
                        // console.log('Archivo subido con ID:', nuevoMultimediaId);
                    } else {
                        if (window.Toast) {
                            Toast.error('Error al subir archivo');
                        }
                        fileInfo.classList.add('hidden');
                        this.value = '';
                    }
                } catch (error) {
                    console.error('Error:', error);
                    if (window.Toast) {
                        Toast.error('Error de conexión al subir archivo');
                    }
                    fileInfo.classList.add('hidden');
                    this.value = '';
                }
            }
        });

        // Botón para eliminar archivo
        modal.querySelector('#edit-remove-file')?.addEventListener('click', function() {
            archivoInput.value = '';
            modal.querySelector('#edit-file-info').classList.add('hidden');
            nuevoMultimediaId = null;
        });

        // Event listeners del formulario
        modal.querySelector('#form-modificar').addEventListener('submit', async function(e) {
            e.preventDefault();
            await procesarModificacion(idHecho, hecho, nuevoMultimediaId);
        });

        modal.querySelector('.btn-cancel').addEventListener('click', () => {
            modal.remove();
        });

        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.remove();
            }
        });
    }

    /**
     * Procesar modificación
     */
    async function procesarModificacion(idHecho, hechoOriginal, nuevoMultimediaId) {
        const titulo = document.getElementById('edit-titulo').value.trim();
        const descripcion = document.getElementById('edit-descripcion').value.trim();
        const fechaDelHecho = document.getElementById('edit-fecha').value;
        const latitud = document.getElementById('edit-latitud').value;
        const longitud = document.getElementById('edit-longitud').value;

        if (!titulo || !descripcion || !fechaDelHecho) {
            mostrarError('Todos los campos obligatorios deben estar completos');
            return;
        }

        try {
            mostrarCargando(true);

            // Verificar si se está cambiando multimedia
            const cambiarMultimedia = document.getElementById('cambiar-multimedia').checked;
            let idContenidoNuevo = hechoOriginal.contenido?.id || null;

            if (cambiarMultimedia) {
                const tipoContenido = document.getElementById('edit-tipo-contenido').value;

                if (tipoContenido === 'TEXTO') {
                    const textoContenido = document.getElementById('edit-texto-contenido').value.trim();
                    if (textoContenido) {
                        const response = await fetch('/multimedia/texto', {
                            method: 'POST',
                            headers: { 'Content-Type': 'text/plain' },
                            body: textoContenido
                        });

                        if (response.ok) {
                            const multimedia = await response.json();
                            idContenidoNuevo = multimedia.id;
                        } else {
                            mostrarError('Error al guardar texto');
                            mostrarCargando(false);
                            return;
                        }
                    }
                } else if (tipoContenido !== '') {
                    // El archivo ya fue subido, usar el ID recibido como parámetro
                    if (nuevoMultimediaId) {
                        idContenidoNuevo = nuevoMultimediaId;
                    }
                } else {
                    // Sin multimedia
                    idContenidoNuevo = null;
                }
            }

            // Enviar TODOS los datos: originales + modificados
            const payload = {
                idHecho: idHecho,

                // Datos originales
                tituloOriginal: hechoOriginal.titulo,
                descripcionOriginal: hechoOriginal.descripcion,
                categoriaOriginal: hechoOriginal.categoria,
                ubicacionOriginal: hechoOriginal.ubicacion,
                fechaDelHechoOriginal: hechoOriginal.fechaDelHecho,
                contenidoOriginal: hechoOriginal.contenido,

                // Datos modificados del formulario
                titulo: titulo,
                descripcion: descripcion,
                categoria: hechoOriginal.categoria,
                ubicacion: {
                    latitud: latitud ? parseFloat(latitud) : hechoOriginal.ubicacion?.latitud,
                    longitud: longitud ? parseFloat(longitud) : hechoOriginal.ubicacion?.longitud
                },
                fechaDelHecho: fechaDelHecho,
                idContenido: idContenidoNuevo
            };

            const response = await fetch('/hechos-creados/api/modificar', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (data.success) {
                mostrarExito('Solicitud de modificación enviada correctamente');
                cerrarModales();
                await cargarHechos();
            } else {
                throw new Error(data.error || 'Error desconocido');
            }

            mostrarCargando(false);
        } catch (error) {
            console.error('Error al modificar:', error);
            mostrarError('Error al solicitar modificación: ' + error.message);
            mostrarCargando(false);
        }
    }

    /**
     * Abrir modal para eliminar hecho
     */
    function abrirModalEliminar(idHecho) {
        const hecho = hechosActuales.find(h => h.identificador == idHecho);
        if (!hecho) return;

        const modal = document.createElement('div');
        modal.className = 'popup show';
        modal.id = 'popup-delete';
        modal.innerHTML = `
            <div class="popup-content">
                <h2>Solicitar Eliminación de Hecho</h2>
                <p><strong>Hecho:</strong> ${hecho.titulo}</p>
                <p class="warning">⚠️ Esta solicitud será revisada por un administrador. Debes proporcionar una justificación detallada.</p>
                
                <form id="form-eliminar">
                    <label for="justificacion">Justificación (mínimo 500 caracteres) *</label>
                    <textarea id="justificacion" rows="8" minlength="500" maxlength="2000" 
                              placeholder="Explica detalladamente por qué solicitas la eliminación de este hecho..." 
                              required></textarea>
                    <div class="char-counter">
                        <span id="char-count">0</span> / 500 mínimo (máximo 2000)
                    </div>

                    <div class="popup-actions">
                        <button type="submit" class="btn-delete-confirm">Solicitar Eliminación</button>
                        <button type="button" class="btn-cancel">Cancelar</button>
                    </div>
                </form>
            </div>
        `;

        document.body.appendChild(modal);

        // Contador de caracteres
        const textarea = modal.querySelector('#justificacion');
        const charCount = modal.querySelector('#char-count');
        const charCounter = modal.querySelector('.char-counter');

        textarea.addEventListener('input', function() {
            const length = this.value.length;
            charCount.textContent = length;

            charCounter.classList.remove('invalid', 'warning', 'valid');
            if (length >= 500) {
                charCounter.classList.add('valid');
            } else if (length >= 300) {
                charCounter.classList.add('warning');
            } else {
                charCounter.classList.add('invalid');
            }
        });

        // Event listeners
        modal.querySelector('#form-eliminar').addEventListener('submit', async function(e) {
            e.preventDefault();
            await procesarEliminacion(idHecho);
        });

        modal.querySelector('.btn-cancel').addEventListener('click', () => {
            modal.remove();
        });

        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.remove();
            }
        });
    }

    /**
     * Procesar eliminación
     */
    async function procesarEliminacion(idHecho) {
        const justificacion = document.getElementById('justificacion').value.trim();

        if (justificacion.length < 500) {
            mostrarError('La justificación debe tener al menos 500 caracteres');
            return;
        }

        const confirmacion = confirm('¿Estás seguro de que deseas solicitar la eliminación de este hecho?');
        if (!confirmacion) return;

        try {
            mostrarCargando(true);

            const payload = {
                idHecho: idHecho,
                justificacion: justificacion
            };

            const response = await fetch('/hechos-creados/api/eliminar', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (data.success) {
                mostrarExito('Solicitud de eliminación enviada correctamente. Será revisada por un administrador.');
                cerrarModales();
                await cargarHechos();
            } else {
                throw new Error(data.error || 'Error desconocido');
            }

            mostrarCargando(false);
        } catch (error) {
            console.error('Error al solicitar eliminación:', error);
            mostrarError('Error al solicitar eliminación: ' + error.message);
            mostrarCargando(false);
        }
    }

    /**
     * Utilidades
     */
    function cerrarModales() {
        document.querySelectorAll('.popup').forEach(modal => modal.remove());
    }

    function formatearFecha(fecha) {
        if (!fecha) return '-';
        const d = new Date(fecha);
        return d.toLocaleDateString('es-AR');
    }

    function formatearFechaHora(fecha) {
        if (!fecha) return '-';
        const d = new Date(fecha);
        return d.toLocaleString('es-AR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
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
        alert('❌ ' + mensaje);
    }

    function mostrarExito(mensaje) {
        alert('✅ ' + mensaje);
    }

})();
