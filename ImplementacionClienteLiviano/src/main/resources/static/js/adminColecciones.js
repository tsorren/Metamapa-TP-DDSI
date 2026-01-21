(function () {
    document.addEventListener("DOMContentLoaded", async () => {

        try {
            const lista = document.getElementById("admin-lista");
            const filtroInput = document.getElementById("filtro");
            const popup = document.getElementById("popup-form");
            const form = document.getElementById("coleccionForm");
            const title = document.getElementById("popup-title");

            const popupConfirm = document.getElementById("popup-confirm");
            const confirmDeleteBtn = document.getElementById("confirm-delete");
            const cancelDeleteBtn = document.getElementById("cancel-delete");

            let editingHandle = null;
            let deleteHandle = null;

            let colecciones = [];
            let filtrosTemporales = []; // Array temporal de filtros
            let filtroIdCounter = 0; // Para generar IDs únicos temporales

            // Elementos del popup de filtros
            const popupFiltros = document.getElementById("popup-filtros");
            const btnGestionarFiltros = document.getElementById("btn-gestionar-filtros");
            const btnAgregarFiltro = document.getElementById("btn-agregar-filtro");
            const filtrosLista = document.getElementById("filtros-lista");
            const confirmarFiltrosBtn = document.getElementById("confirmar-filtros");
            const cancelarFiltrosBtn = document.getElementById("cancelar-filtros");
            const filtrosCount = document.getElementById("filtros-count");

            // Tipos de filtro disponibles (basados en las clases Java del backend)
            const tiposFiltro = [
                { value: 'cargaEntre', label: 'Carga Entre Fechas', params: ['fechaInicio', 'fechaFin'], types: ['datetime-local', 'datetime-local'] },
                { value: 'categoria', label: 'Categoría', params: ['nombreCategoria'], types: ['text'] },
                { value: 'descripcion', label: 'Descripción', params: ['descripcion'], types: ['text'] },
                { value: 'fechaEntre', label: 'Fecha Entre', params: ['fechaInicio', 'fechaFin'], types: ['date', 'date'] },
                { value: 'fechaExacta', label: 'Fecha Exacta', params: ['fecha'], types: ['date'] },
                { value: 'multimedia', label: 'Tipo Multimedia', params: ['tipoContenido'], types: ['select'], 
                  options: [['TEXTO', 'Texto'], ['AUDIO', 'Audio'], ['IMAGEN', 'Imagen'], ['VIDEO', 'Video']] },
                { value: 'tipoOrigen', label: 'Tipo de Origen', params: ['tipoOrigen'], types: ['select'],
                  options: [['MANUAL', 'Manual'], ['DATASET', 'Dataset'], ['CONTRIBUYENTE', 'Contribuyente'], ['EXTERNO', 'Externo']] },
                { value: 'titulo', label: 'Título', params: ['titulo'], types: ['text'] },
                { value: 'ubicacion', label: 'Ubicación', params: ['latitud', 'longitud', 'radio'], types: ['number', 'number', 'number'] }
            ];

            // Mapeo de tipos del backend a tipos del frontend
            // El backend usa PascalCase (CargaEntre, Categoria, etc.) y el frontend usa camelCase
            const mapeoTiposBackendToFrontend = {
                'CargaEntre': 'cargaEntre',
                'Categoria': 'categoria',
                'Descripcion': 'descripcion',
                'FechaEntre': 'fechaEntre',
                'FechaExacta': 'fechaExacta',
                'Multimedia': 'multimedia',
                'TipoOrigen': 'tipoOrigen',
                'Titulo': 'titulo',
                'Ubicacion': 'ubicacion',
                'Curado': 'curado'
            };

            // Cargar colecciones desde el backend
            async function cargarColecciones() {
                try {
                    const response = await fetch('/admin/colecciones/api', {
                        credentials: 'include'
                    });
                    if (response.ok) {
                        colecciones = await response.json();
                    } else {
                        console.error('[adminColecciones] Error al cargar colecciones:', response.status);
                    }
                } catch (error) {
                    console.error('[adminColecciones] Error al conectar con el backend:', error);
                }
            }

            function render(filtro = "") {
                lista.innerHTML = "";
                const coleccionesFiltradas = colecciones.filter(c =>
                    c.titulo.toLowerCase().includes((filtro || "").toLowerCase())
                );

                if (coleccionesFiltradas.length === 0) {
                    lista.innerHTML = '<p style="text-align: center; color: #666;">No hay colecciones disponibles</p>';
                    return;
                }

                coleccionesFiltradas.forEach((col) => {
                    const card = document.createElement("div");
                    card.className = "admin-card";

                    const headerDiv = document.createElement("div");
                    headerDiv.className = "card-header";
                    headerDiv.innerHTML = `
              <h3>${col.titulo || 'Sin título'}</h3>
              <div class="card-buttons">
                <button class="btn-edit" data-handle="${col.handle}" title="Editar">
                  <img src="../images/edit.svg" alt="Editar">
                </button>
                <button class="btn-delete" data-handle="${col.handle}" title="Eliminar">
                  <img src="../images/trash.svg" alt="Eliminar">
                </button>
                <button class="btn-view" data-handle="${col.handle}" title="Ver">
                  <img src="../images/eye-open.svg" alt="Ver">
                </button>
              </div>
            `;
                    card.appendChild(headerDiv);
                    lista.appendChild(card);
                });
            }

            function openForm(handle = null) {
                editingHandle = handle;
                filtrosTemporales = []; // Resetear filtros temporales

                // Resetear todos los checkboxes
                document.querySelectorAll('.fuente-checkbox').forEach(cb => cb.checked = false);

                if (editingHandle) {
                    const col = colecciones.find(c => c.handle === editingHandle);
                    if (col) {
                        title.textContent = "Editar Colección";
                        form.titulo.value = col.titulo || "";
                        form.descripcion.value = col.descripcion || "";
                        form.algoritmoConsenso.value = col.algoritmoConsenso || "";

                        // Marcar las fuentes seleccionadas
                        if (col.fuentes && col.fuentes.length > 0) {
                            const idsFuentesCol = col.fuentes.map(f => f.identificador);
                            idsFuentesCol.forEach(id => {
                                const checkbox = document.querySelector(`.fuente-checkbox[value="${id}"]`);
                                if (checkbox) {
                                    checkbox.checked = true;
                                }
                            });
                        }

                        // Cargar los filtros existentes del criterio de pertenencia
                        // Intentar ambas variantes del nombre del campo (por si el JSON usa otra convención)
                        const criterio = col.CriterioDePertenencia || col.criterioDePertenencia;
                        if (criterio && criterio.filtros && criterio.filtros.length > 0) {
                            criterio.filtros.forEach(filtro => {
                                // Convertir tipo del backend (PascalCase) a frontend (camelCase)
                                const tipoFrontend = mapeoTiposBackendToFrontend[filtro.tipoFiltro] || filtro.tipoFiltro.charAt(0).toLowerCase() + filtro.tipoFiltro.slice(1);

                                // Encontrar la configuración del tipo de filtro para saber los nombres de los parámetros
                                const tipoConfig = tiposFiltro.find(t => t.value === tipoFrontend);
                                const parametrosObj = {};

                                if (tipoConfig && filtro.parametros) {
                                    // Mapear los parámetros del array a un objeto con los nombres correctos
                                    tipoConfig.params.forEach((paramName, idx) => {
                                        let valorParam = filtro.parametros[idx] || '';

                                        // Formatear fechas para inputs de tipo date/datetime-local
                                        if (tipoConfig.types[idx] === 'date' && valorParam) {
                                            // Asegurar formato YYYY-MM-DD
                                            if (valorParam.includes('T')) {
                                                valorParam = valorParam.split('T')[0];
                                            }
                                        } else if (tipoConfig.types[idx] === 'datetime-local' && valorParam) {
                                            // Asegurar formato YYYY-MM-DDTHH:mm
                                            if (!valorParam.includes('T')) {
                                                valorParam = valorParam + 'T00:00';
                                            } else {
                                                // Truncar segundos si existen
                                                valorParam = valorParam.substring(0, 16);
                                            }
                                        }

                                        parametrosObj[paramName] = valorParam;
                                    });
                                }

                                filtrosTemporales.push({
                                    id: `filtro-${filtroIdCounter++}`,
                                    tipoFiltro: tipoFrontend,
                                    parametros: parametrosObj
                                });
                            });
                            console.log('[adminColecciones] Filtros cargados:', filtrosTemporales);
                        }
                    }
                } else {
                    title.textContent = "Nueva Colección";
                    form.reset();
                    document.querySelectorAll('.fuente-checkbox').forEach(cb => cb.checked = false);
                }

                actualizarContadorFiltros();
                actualizarContadorFuentes();
                popup.classList.add("active");
            }

            function actualizarContadorFiltros() {
                const count = filtrosTemporales.length;
                filtrosCount.textContent = `${count} filtro${count !== 1 ? 's' : ''} configurado${count !== 1 ? 's' : ''}`;
            }

            // Abrir popup de gestión de filtros
            btnGestionarFiltros.addEventListener("click", () => {
                renderFiltros();
                popupFiltros.classList.add("active");
            });

            // Agregar nuevo filtro
            btnAgregarFiltro.addEventListener("click", () => {
                const nuevoFiltro = {
                    id: `filtro-${filtroIdCounter++}`,
                    tipoFiltro: '',
                    parametros: {}
                };
                filtrosTemporales.push(nuevoFiltro);
                renderFiltros();
            });

            // Renderizar lista de filtros en el popup
            function renderFiltros() {
                filtrosLista.innerHTML = '';
                
                if (filtrosTemporales.length === 0) {
                    filtrosLista.innerHTML = '<p style="text-align: center; color: #666;">No hay filtros configurados. Haz clic en "+ Agregar Filtro"</p>';
                    return;
                }

                filtrosTemporales.forEach((filtro, index) => {
                    const filtroDiv = document.createElement('div');
                    filtroDiv.className = 'filtro-item';
                    filtroDiv.innerHTML = `
                        <div class="filtro-header">
                            <span>Filtro ${index + 1}</span>
                            <button type="button" class="btn-remove-filtro" data-id="${filtro.id}">✕</button>
                        </div>
                        <div class="filtro-body">
                            <label>Tipo de Filtro</label>
                            <select class="filtro-tipo" data-id="${filtro.id}">
                                <option value="">Seleccione...</option>
                                ${tiposFiltro.map(t => 
                                    `<option value="${t.value}" ${filtro.tipoFiltro === t.value ? 'selected' : ''}>${t.label}</option>`
                                ).join('')}
                            </select>
                            <div class="filtro-parametros" id="params-${filtro.id}">
                                ${renderParametros(filtro)}
                            </div>
                        </div>
                    `;
                    filtrosLista.appendChild(filtroDiv);
                });

                // Event listeners para selects de tipo
                document.querySelectorAll('.filtro-tipo').forEach(select => {
                    select.addEventListener('change', (e) => {
                        const filtroId = e.target.dataset.id;
                        const filtro = filtrosTemporales.find(f => f.id === filtroId);
                        if (filtro) {
                            filtro.tipoFiltro = e.target.value;
                            filtro.parametros = {};
                            const paramsDiv = document.getElementById(`params-${filtroId}`);
                            paramsDiv.innerHTML = renderParametros(filtro);
                            attachParameterListeners();
                        }
                    });
                });

                // Event listeners para botones de eliminar
                document.querySelectorAll('.btn-remove-filtro').forEach(btn => {
                    btn.addEventListener('click', (e) => {
                        const filtroId = e.target.dataset.id;
                        filtrosTemporales = filtrosTemporales.filter(f => f.id !== filtroId);
                        renderFiltros();
                    });
                });

                attachParameterListeners();
            }

            function attachParameterListeners() {
                // Event listeners para inputs de parámetros (input y change para mejor compatibilidad con inputs de fecha)
                document.querySelectorAll('.param-input').forEach(input => {
                    const updateValue = (e) => {
                        const filtroId = e.target.dataset.filtroId;
                        const paramName = e.target.dataset.paramName;
                        const filtro = filtrosTemporales.find(f => f.id === filtroId);
                        if (filtro) {
                            filtro.parametros[paramName] = e.target.value;
                        }
                    };
                    input.addEventListener('input', updateValue);
                    input.addEventListener('change', updateValue); // Para inputs de tipo date/datetime-local
                });

                // Event listeners para selects de parámetros
                document.querySelectorAll('.param-select').forEach(select => {
                    select.addEventListener('change', (e) => {
                        const filtroId = e.target.dataset.filtroId;
                        const paramName = e.target.dataset.paramName;
                        const filtro = filtrosTemporales.find(f => f.id === filtroId);
                        if (filtro) {
                            filtro.parametros[paramName] = e.target.value;
                        }
                    });
                });
            }

            function renderParametros(filtro) {
                if (!filtro.tipoFiltro) return '<p style="color: #999; font-size: 0.9em;">Seleccione un tipo de filtro</p>';
                
                const tipo = tiposFiltro.find(t => t.value === filtro.tipoFiltro);
                if (!tipo) return '';

                return tipo.params.map((param, idx) => {
                    const value = filtro.parametros[param] || '';
                    const inputType = tipo.types[idx];
                    let placeholder = param;
                    let labelText = param;
                    
                    // Placeholders y labels específicos
                    if (param === 'latitud') {
                        placeholder = 'Ej: -34.603722';
                        labelText = 'Latitud';
                    } else if (param === 'longitud') {
                        placeholder = 'Ej: -58.381592';
                        labelText = 'Longitud';
                    } else if (param === 'radio') {
                        placeholder = 'Radio en km (ej: 10)';
                        labelText = 'Radio (km)';
                    } else if (param === 'nombreCategoria') {
                        placeholder = 'Ej: Incendio, Inundación, etc.';
                        labelText = 'Nombre de Categoría';
                    } else if (param === 'descripcion') {
                        placeholder = 'Descripción del hecho';
                        labelText = 'Descripción';
                    } else if (param === 'titulo') {
                        placeholder = 'Título del hecho';
                        labelText = 'Título';
                    } else if (param === 'fechaInicio') {
                        placeholder = 'Seleccione fecha de inicio';
                        labelText = 'Fecha Inicio';
                    } else if (param === 'fechaFin') {
                        placeholder = 'Seleccione fecha de fin';
                        labelText = 'Fecha Fin';
                    } else if (param === 'fecha') {
                        placeholder = 'Seleccione fecha';
                        labelText = 'Fecha';
                    } else if (param === 'tipoContenido') {
                        labelText = 'Tipo de Contenido';
                    } else if (param === 'tipoOrigen') {
                        labelText = 'Tipo de Origen';
                    }
                    
                    if (inputType === 'select' && tipo.options) {
                        return `
                            <div class="param-group">
                                <label>${labelText}</label>
                                <select class="param-select" data-filtro-id="${filtro.id}" data-param-name="${param}">
                                    <option value="">Seleccione...</option>
                                    ${tipo.options.map(opt => 
                                        `<option value="${opt[0]}" ${value === opt[0] ? 'selected' : ''}>${opt[1]}</option>`
                                    ).join('')}
                                </select>
                            </div>
                        `;
                    } else {
                        return `
                            <div class="param-group">
                                <label>${labelText}</label>
                                <input 
                                    type="${inputType}" 
                                    class="param-input" 
                                    data-filtro-id="${filtro.id}" 
                                    data-param-name="${param}" 
                                    value="${value}" 
                                    placeholder="${placeholder}"
                                    ${inputType === 'number' ? 'step="any"' : ''}
                                >
                            </div>
                        `;
                    }
                }).join('');
            }

            // Confirmar filtros y cerrar popup
            confirmarFiltrosBtn.addEventListener("click", () => {
                // Validar que todos los filtros estén completos
                let valid = true;
                for (const filtro of filtrosTemporales) {
                    if (!filtro.tipoFiltro) {
                        if (window.Toast) {
                            Toast.warning('Todos los filtros deben tener un tipo seleccionado');
                        }
                        valid = false;
                        break;
                    }
                    const tipo = tiposFiltro.find(t => t.value === filtro.tipoFiltro);
                    for (const param of tipo.params) {
                        const paramValue = filtro.parametros[param];
                        // Validación más robusta que maneja valores null, undefined, strings vacíos y números
                        if (paramValue === null || paramValue === undefined ||
                            (typeof paramValue === 'string' && paramValue.trim() === '')) {
                            if (window.Toast) {
                                Toast.warning(`Complete todos los parámetros del filtro "${tipo.label}"`);
                            }
                            valid = false;
                            break;
                        }
                    }
                    if (!valid) break;
                }
                
                if (valid) {
                    actualizarContadorFiltros();
                    popupFiltros.classList.remove("active");
                }
            });

            // Cancelar filtros
            cancelarFiltrosBtn.addEventListener("click", () => {
                popupFiltros.classList.remove("active");
            });

            // botón crear
            const btnCrear = document.getElementById("btn-crear");
            if (btnCrear) btnCrear.addEventListener("click", () => openForm());

            // botón cancelar formulario
            const btnCancelForm = document.getElementById("btn-cancel-form");
            if (btnCancelForm) {
                btnCancelForm.addEventListener("click", () => {
                    popup.classList.remove("active");
                    form.reset();
                    filtrosTemporales = [];
                    editingHandle = null;
                });
            }

            // cerrar popups al hacer click fuera del contenido
            document.querySelectorAll(".popup").forEach(p =>
                p.addEventListener("click", e => {
                    if (e.target === p) p.classList.remove("active");
                })
            );

            // guardar coleccion
            form.addEventListener("submit", async (e) => {
                e.preventDefault();

                // Obtener IDs de fuentes desde checkboxes
                const checkboxes = document.querySelectorAll('.fuente-checkbox:checked');
                const idsFuentes = Array.from(checkboxes).map(cb => cb.value);

                // Validaciones
                if (idsFuentes.length === 0) {
                    Toast.warning('Debe seleccionar al menos una fuente de datos');
                    return;
                }

                if (filtrosTemporales.length === 0) {
                    Toast.warning('Debe configurar al menos un filtro para el criterio de pertenencia');
                    return;
                }

                // Convertir parametros de objeto a array en el orden correcto
                const filtrosParaBackend = filtrosTemporales.map(f => {
                    const tipo = tiposFiltro.find(t => t.value === f.tipoFiltro);
                    const parametrosArray = tipo.params.map(param => f.parametros[param]);
                    return {
                        tipoFiltro: f.tipoFiltro,
                        parametros: parametrosArray
                    };
                });

                let datos;
                let endpoint;
                let method;
                let mensajeExito;

                if (editingHandle) {
                    // Editar colección existente (con filtros)
                    datos = {
                        titulo: form.titulo.value.trim(),
                        descripcion: form.descripcion.value.trim(),
                        filtros: filtrosParaBackend,
                        idsFuentes: idsFuentes,
                        algoritmoConsenso: form.algoritmoConsenso.value
                    };
                    endpoint = `/admin/colecciones/api/${editingHandle}/con-filtros`;
                    method = 'PATCH';
                    mensajeExito = 'Colección actualizada exitosamente';
                } else {
                    // Crear nueva colección con filtros

                    datos = {
                        titulo: form.titulo.value.trim(),
                        descripcion: form.descripcion.value.trim(),
                        filtros: filtrosParaBackend,
                        idsFuentes: idsFuentes,
                        algoritmoConsenso: form.algoritmoConsenso.value
                    };
                    endpoint = '/admin/colecciones/api/con-filtros';
                    method = 'POST';
                    mensajeExito = 'Colección creada exitosamente';
                }

                try {
                    const response = await fetch(endpoint, {
                        method: method,
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(datos),
                        credentials: 'include'
                    });

                    if (response.ok) {
                        await cargarColecciones();
                        popup.classList.remove("active");
                        render(filtroInput.value);
                        filtrosTemporales = []; // Limpiar filtros
                        Toast.success(mensajeExito);
                    } else {
                        const error = await response.json();
                        Toast.error('Error al guardar la colección: ' + (error.error || 'Error desconocido'));
                    }
                } catch (error) {
                    Toast.error('Error de conexión con el servidor');
                }
            });

            // delegación de eventos en el contenedor
            lista.addEventListener("click", (e) => {
                const btn = e.target.closest("button");
                if (!btn) return;
                const handle = btn.dataset.handle;

                if (btn.classList.contains("btn-edit")) {
                    openForm(handle);
                } else if (btn.classList.contains("btn-delete")) {
                    deleteHandle = handle;
                    const col = colecciones.find(c => c.handle === handle);
                    if (col) {
                        popupConfirm.querySelector("h2").textContent = `¿Eliminar "${col.titulo}"?`;
                        popupConfirm.classList.add("active");
                    }
                } else if (btn.classList.contains("btn-view")) {
                    window.location.href = `/visualizarHechos?coleccion=${handle}`;
                }
            });

            // confirmar / cancelar borrado
            confirmDeleteBtn.addEventListener("click", async () => {
                if (deleteHandle) {
                    try {
                        const response = await fetch(`/admin/colecciones/api/${deleteHandle}`, {
                            method: 'DELETE',
                            credentials: 'include'
                        });

                        if (response.ok) {
                            await cargarColecciones();
                            render(filtroInput.value);
                            Toast.success('Colección eliminada exitosamente');
                        } else {
                            const error = await response.text();
                            Toast.error('Error al eliminar la colección: ' + error);
                        }
                    } catch (error) {
                        Toast.error('Error de conexión con el servidor');
                    }
                }
                popupConfirm.classList.remove("active");
                deleteHandle = null;
            });

            cancelDeleteBtn.addEventListener("click", () => {
                popupConfirm.classList.remove("active");
                deleteHandle = null;
            });

            // filtro
            filtroInput.addEventListener("input", () => render(filtroInput.value));

            // === FUNCIONALIDAD DE FUENTES CON CHECKBOXES ===

            // Buscador de fuentes
            const fuentesSearchInput = document.getElementById('fuentes-search-input');
            const fuentesList = document.getElementById('fuentes-list');
            const fuentesSelectedCount = document.getElementById('fuentes-selected-count');
            const btnSelectAll = document.getElementById('btn-select-all');
            const btnDeselectAll = document.getElementById('btn-deselect-all');

            if (fuentesSearchInput && fuentesList) {
                fuentesSearchInput.addEventListener('input', (e) => {
                    const searchTerm = e.target.value.toLowerCase();
                    const fuenteItems = fuentesList.querySelectorAll('.fuente-item');

                    fuenteItems.forEach(item => {
                        const label = item.querySelector('.fuente-checkbox-label');
                        const checkbox = item.querySelector('.fuente-checkbox');
                        const nombreFuente = checkbox ? checkbox.dataset.nombre.toLowerCase() : '';

                        if (nombreFuente.includes(searchTerm)) {
                            item.style.display = 'block';
                        } else {
                            item.style.display = 'none';
                        }
                    });
                });
            }

            // Botón seleccionar todas las fuentes visibles
            if (btnSelectAll) {
                btnSelectAll.addEventListener('click', () => {
                    const checkboxes = fuentesList.querySelectorAll('.fuente-checkbox');
                    checkboxes.forEach(cb => {
                        const item = cb.closest('.fuente-item');
                        if (item && item.style.display !== 'none') {
                            cb.checked = true;
                        }
                    });
                    actualizarContadorFuentes();
                });
            }

            // Botón deseleccionar todas las fuentes
            if (btnDeselectAll) {
                btnDeselectAll.addEventListener('click', () => {
                    const checkboxes = fuentesList.querySelectorAll('.fuente-checkbox');
                    checkboxes.forEach(cb => {
                        cb.checked = false;
                    });
                    actualizarContadorFuentes();
                });
            }

            // Actualizar contador cuando cambie la selección
            function actualizarContadorFuentes() {
                const checkboxesSeleccionados = document.querySelectorAll('.fuente-checkbox:checked');
                const count = checkboxesSeleccionados.length;

                if (fuentesSelectedCount) {
                    const spanCount = fuentesSelectedCount.querySelector('span');
                    if (spanCount) {
                        spanCount.textContent = `${count} fuente${count !== 1 ? 's' : ''} seleccionada${count !== 1 ? 's' : ''}`;
                    }
                }
            }

            // Event listeners para todos los checkboxes
            if (fuentesList) {
                fuentesList.addEventListener('change', (e) => {
                    if (e.target.classList.contains('fuente-checkbox')) {
                        actualizarContadorFuentes();
                    }
                });

                // Inicializar contador
                actualizarContadorFuentes();
            }

            // Cargar colecciones al iniciar
            await cargarColecciones();
            render();
        } catch (err) {
            console.error("[adminColecciones] error en init:", err);
        }
    });
})();
