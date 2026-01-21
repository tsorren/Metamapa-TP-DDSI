/**
 * Funcionalidad del Panel de Control
 * Gestión de fuentes y colecciones
 */

document.addEventListener('DOMContentLoaded', function () {
    initializePanelControl();
});

function initializePanelControl() {
    // Botones eliminados: configurar fuentes y ver colecciones

    // Botón importar CSV
    const btnImportarCsv = document.getElementById('btn-importar-csv');
    if (btnImportarCsv) {
        btnImportarCsv.addEventListener('click', () => {
            window.location.href = '/admin/importar';;
        });
    }

    // Selector de tipo de fuente
    const tipoFuenteSelect = document.getElementById('tipo-fuente');
    if (tipoFuenteSelect) {
        tipoFuenteSelect.addEventListener('change', cambiarConfiguracionFuente);
    }

    // Selector de tipo de proxy
    const tipoProxySelect = document.getElementById('tipo-proxy');
    if (tipoProxySelect) {
        tipoProxySelect.addEventListener('change', cambiarConfiguracionProxy);
    }

    // Formulario de configuración
    const formConfigurar = document.getElementById('form-configurar-fuente');
    if (formConfigurar) {
        formConfigurar.addEventListener('submit', enviarConfiguracionFuente);
    }

    // Formularios de proxy
    const formProxyRecursiva = document.getElementById('form-proxy-recursiva');
    if (formProxyRecursiva) {
        formProxyRecursiva.addEventListener('submit', enviarProxyRecursiva);
    }

    const formProxyExterna = document.getElementById('form-proxy-externa');
    if (formProxyExterna) {
        formProxyExterna.addEventListener('submit', enviarProxyExterna);
    }

    // Cerrar modal con ESC
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            cerrarModal();
            cerrarModalProxyRecursiva();
            cerrarModalProxyExterna();
        }
    });
}

/**
 * Carga las colecciones disponibles en el dropdown
 */
function cargarColecciones() {
    fetch('/panel/colecciones', {
        credentials: 'include'
    })
        .then(response => response.json())
        .then(colecciones => {
            const select = document.getElementById('coleccion-id');
            select.innerHTML = '<option value="">Selecciona una colección</option>';

            colecciones.forEach(coleccion => {
                const option = document.createElement('option');
                option.value = coleccion.id;
                option.textContent = coleccion.nombre;
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error al cargar colecciones:', error);
        });
}

// Llamar cuando se abre el modal
function abrirModalConfiguracion() {
    document.getElementById('modal-configurar-fuentes').style.display = 'flex';
    cargarColecciones(); // Cargar colecciones al abrir el modal
}

function cerrarModal() {
    const modal = document.getElementById('modal-configurar-fuentes');
    if (modal) {
        modal.style.display = 'none';
    }
}

function cambiarConfiguracionFuente() {
    const tipoFuente = document.getElementById('tipo-fuente').value;

    // Ocultar todas las secciones de configuración
    document.getElementById('config-estatica').style.display = 'none';
    document.getElementById('config-dinamica').style.display = 'none';
    document.getElementById('config-proxy').style.display = 'none';

    // Mostrar la sección correspondiente
    switch (tipoFuente) {
        case 'ESTATICA':
            document.getElementById('config-estatica').style.display = 'block';
            break;
        case 'DINAMICA':
            document.getElementById('config-dinamica').style.display = 'block';
            break;
        case 'PROXY':
            document.getElementById('config-proxy').style.display = 'block';
            // Resetear el selector de tipo de proxy
            document.getElementById('tipo-proxy').value = '';
            document.getElementById('config-proxy-recursiva').style.display = 'none';
            document.getElementById('config-proxy-externa').style.display = 'none';
            break;
    }
}

function cambiarConfiguracionProxy() {
    const tipoProxy = document.getElementById('tipo-proxy').value;

    // Ocultar todas las subsecciones de proxy
    document.getElementById('config-proxy-recursiva').style.display = 'none';
    document.getElementById('config-proxy-externa').style.display = 'none';

    // Mostrar la subsección correspondiente
    switch (tipoProxy) {
        case 'RECURSIVA':
            document.getElementById('config-proxy-recursiva').style.display = 'block';
            break;
        case 'EXTERNA':
            document.getElementById('config-proxy-externa').style.display = 'block';
            break;
    }
}

function enviarConfiguracionFuente(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const tipoFuente = formData.get('tipoFuente');

    // Mostrar loading
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Guardando...';
    submitBtn.disabled = true;

    // Determinar la URL según el tipo de fuente
    let url = form.action;

    if (tipoFuente === 'PROXY') {
        const tipoProxy = formData.get('tipoProxy');

        if (!tipoProxy) {
            Toast.error('Por favor selecciona el tipo de Proxy');
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
            return;
        }

        // Configurar URL según el tipo de proxy
        if (tipoProxy === 'RECURSIVA') {
            url = '/panel/proxy/configurar/recursiva';
        } else if (tipoProxy === 'EXTERNA') {
            url = '/panel/proxy/configurar/externa';
        }
    }

    fetch(url, {
        method: 'POST',
        body: formData,
        credentials: 'include'
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            return response.json().then(err => {
                throw new Error(err.message || 'Error al configurar la fuente');
            });
        })
        .then(data => {
            // Éxito
            Toast.success(data.message || 'Fuente configurada correctamente');
            cerrarModal();
            // Recargar la página para mostrar la nueva fuente
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        })
        .catch(error => {
            // Error
            console.error('Error:', error);
            Toast.error('Error al configurar la fuente: ' + error.message);
        })
        .finally(() => {
            // Restaurar botón
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        });
}

function editarFuente(fuenteId) {
    // TODO: Implementar edición de fuente
    console.log('Editando fuente:', fuenteId);
    alert('Funcionalidad de edición en desarrollo');
}

function eliminarFuente(fuenteId) {
    if (confirm('¿Estás seguro de que quieres eliminar esta fuente?')) {
        fetch(`/panel/fuentes/${fuenteId}`, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    if (window.Toast) {
                        Toast.success('Fuente eliminada correctamente');
                    }
                    window.location.reload();
                } else {
                    throw new Error('Error al eliminar la fuente');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                if (window.Toast) {
                    Toast.error('Error al eliminar la fuente');
                }
            });
    }
}

/**
 * Gestión de Microservicios - Solo verificación de estado
 */
function verificarEstado(servicio) {
    fetch('/panel/microservicios/status', {
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            let estado;
            if (servicio === 'fuenteestatica') {
                estado = data.fuenteEstaticaRunning ? 'CORRIENDO' : 'DETENIDO';
            } else if (servicio === 'fuentedinamica') {
                estado = data.fuenteDinamicaRunning ? 'CORRIENDO' : 'DETENIDO';
            } else if (servicio === 'fuenteproxy') {
                estado = data.fuenteProxyRunning ? 'CORRIENDO' : 'DETENIDO';
            } else if (servicio === 'agregador') {
                estado = data.agregadorRunning ? 'CORRIENDO' : 'DETENIDO';
            }
            const tipo = estado === 'CORRIENDO' ? 'success' : 'warning';
            Toast[tipo](`Estado de ${servicio}: ${estado}`);
        })
        .catch(error => {
            console.error('Error:', error);
            Toast.error('Error al verificar estado');
        });
}

/**
 * Funciones para levantar fuentes en el Agregador
 */
function levantarFuenteEstatica() {
    const btn = event.target.closest('button');
    btn.disabled = true;
    btn.innerHTML = '<span class="btn-icon">⏳</span> Levantando...';

    fetch('/panel/agregador/fuentes/levantar/estatica', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message || 'Error al levantar fuente'); });
            }
            return response.json();
        })
        .then(data => {
            Toast.success('Fuente Estática levantada correctamente en el Agregador');
            cargarFuentesRegistradas();
        })
        .catch(error => {
            console.error('Error:', error);
            Toast.error('Error al levantar Fuente Estática: ' + error.message);
        })
        .finally(() => {
            btn.disabled = false;
            btn.innerHTML = '<span class="btn-icon">📄</span> Levantar Fuente Estática';
        });
}

function levantarFuenteDinamica() {
    const btn = event.target.closest('button');
    btn.disabled = true;
    btn.innerHTML = '<span class="btn-icon">⏳</span> Levantando...';

    fetch('/panel/agregador/fuentes/levantar/dinamica', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message || 'Error al levantar fuente'); });
            }
            return response.json();
        })
        .then(data => {
            Toast.success('Fuente Dinámica levantada correctamente en el Agregador');
            cargarFuentesRegistradas();
        })
        .catch(error => {
            console.error('Error:', error);
            Toast.error('Error al levantar Fuente Dinámica: ' + error.message);
        })
        .finally(() => {
            btn.disabled = false;
            btn.innerHTML = '<span class="btn-icon">🔄</span> Levantar Fuente Dinámica';
        });
}

function levantarFuenteProxy() {
    const btn = event.target.closest('button');
    const originalHTML = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<span class="btn-icon">⏳</span> Registrando...';

    fetch('/panel/agregador/fuentes/levantar/proxy', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message || 'Error al registrar módulo'); });
            }
            return response.json();
        })
        .then(data => {
            Toast.success('Módulo Proxy registrado correctamente en el Agregador');
            cargarFuentesRegistradas();
        })
        .catch(error => {
            console.error('Error:', error);
            Toast.error('Error al registrar Módulo Proxy: ' + error.message);
        })
        .finally(() => {
            btn.disabled = false;
            btn.innerHTML = originalHTML;
        });
}

/**
 * Carga y muestra los módulos registrados en el agregador
 */
function cargarFuentesRegistradas() {
    console.log('🔄 Cargando módulos registrados...');

    // Cargar módulos en lugar de fuentes
    fetch('/panel/agregador/modulos', {
        credentials: 'include'
    })
        .then(response => {
            console.log('📡 Respuesta recibida:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(modulos => {
            console.log('📦 Módulos recibidos:', modulos);
            console.log('📦 Total de módulos:', modulos.length);

            const container = document.getElementById('fuentes-registradas-list');

            if (!modulos || modulos.length === 0) {
                container.innerHTML = '<p class="no-fuentes">No hay módulos registrados en el agregador</p>';
                console.log('⚠️ No hay módulos para mostrar');
                return;
            }

            // Cargar también las fuentes para mostrar el conteo
            fetch('/panel/agregador/fuentes/registradas', { credentials: 'include' })
                .then(r => r.json())
                .then(fuentes => {
                    console.log('📦 Fuentes recibidas:', fuentes);

                    // Contar fuentes por módulo
                    const fuentesPorModulo = new Map();
                    fuentes.forEach(fuente => {
                        const moduloId = fuente.moduloFuente.identificador;
                        if (!fuentesPorModulo.has(moduloId)) {
                            fuentesPorModulo.set(moduloId, []);
                        }
                        fuentesPorModulo.get(moduloId).push(fuente);
                    });

                    console.log('📊 Fuentes por módulo:', fuentesPorModulo);

                    // Generar HTML para cada módulo
                    const htmlModulos = modulos.map(modulo => {
                        const tipo = modulo.tipo || 'DESCONOCIDO';
                        const tipoClass = tipo.toLowerCase();
                        const url = modulo.url || 'URL no disponible';
                        const fuentesDelModulo = fuentesPorModulo.get(modulo.identificador) || [];
                        const cantidadFuentes = fuentesDelModulo.length;

                        console.log(`✨ Renderizando módulo ${tipo} (${tipoClass}) ID: ${modulo.identificador} con ${cantidadFuentes} fuente(s)`);

                        return `
                            <div class="modulo-registrado-item">
                                <div class="modulo-header">
                                    <span class="fuente-reg-tipo fuente-tipo-${tipoClass}">${tipo}</span>
                                    <span class="modulo-id">Módulo ID: ${modulo.identificador}</span>
                                </div>
                                <div class="modulo-info">
                                    <div class="modulo-url">
                                        <strong>URL:</strong> 
                                        <span style="word-break: break-all; color: #666;">${url}</span>
                                    </div>
                                    <div class="modulo-fuentes-count">
                                        <strong>${cantidadFuentes}</strong> fuente${cantidadFuentes !== 1 ? 's' : ''} registrada${cantidadFuentes !== 1 ? 's' : ''}
                                    </div>
                                </div>
                                ${cantidadFuentes > 0 ? `
                                    <div class="fuentes-detalle">
                                        ${fuentesDelModulo.map(fuente => `
                                            <div class="fuente-mini-item">
                                                <span class="fuente-mini-id">ID: ${fuente.identificador}</span>
                                                <span class="fuente-mini-externo">Externo: ${fuente.identificadorExterno}</span>
                                            </div>
                                        `).join('')}
                                    </div>
                                ` : '<div class="fuentes-detalle"><p class="no-fuentes-modulo">Sin fuentes cargadas aún</p></div>'}
                            </div>
                        `;
                    }).join('');

                    container.innerHTML = htmlModulos;

                    // Actualizar contador total
                    const totalFuentes = fuentes.length;
                    console.log(`✅ RENDERIZADO COMPLETO: ${modulos.length} módulo(s) con ${totalFuentes} fuente(s) total`);

                    // Verificar que se renderizó correctamente
                    const elementosRenderizados = container.querySelectorAll('.modulo-registrado-item');
                    console.log(`✅ Elementos en DOM: ${elementosRenderizados.length}`);
                })
                .catch(error => {
                    console.error('⚠️ Error al cargar fuentes, mostrando solo módulos:', error);

                    // Mostrar módulos sin información de fuentes
                    const htmlModulos = modulos.map(modulo => {
                        const tipo = modulo.tipo || 'DESCONOCIDO';
                        const tipoClass = tipo.toLowerCase();
                        const url = modulo.url || 'URL no disponible';

                        return `
                            <div class="modulo-registrado-item">
                                <div class="modulo-header">
                                    <span class="fuente-reg-tipo fuente-tipo-${tipoClass}">${tipo}</span>
                                    <span class="modulo-id">Módulo ID: ${modulo.identificador}</span>
                                </div>
                                <div class="modulo-info">
                                    <div class="modulo-url">
                                        <strong>URL:</strong> 
                                        <span style="word-break: break-all; color: #666;">${url}</span>
                                    </div>
                                    <div class="modulo-fuentes-count">
                                        <strong>0</strong> fuentes registradas
                                    </div>
                                </div>
                            </div>
                        `;
                    }).join('');

                    container.innerHTML = htmlModulos;
                });
        })
        .catch(error => {
            console.error('❌ Error al cargar módulos registrados:', error);
            const container = document.getElementById('fuentes-registradas-list');
            container.innerHTML = `<p class="no-fuentes">Error al cargar módulos: ${error.message}</p>`;
        });
}

// Cargar fuentes registradas al inicializar
document.addEventListener('DOMContentLoaded', function() {
    cargarFuentesRegistradas();
    actualizarEstadoBotonesLevantar();
});

/**
 * Actualiza el estado de los botones de levantar fuentes según el estado de los microservicios
 */
function actualizarEstadoBotonesLevantar() {
    fetch('/panel/microservicios/status', {
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const btnEstatica = document.getElementById('btn-levantar-estatica');
            const btnDinamica = document.getElementById('btn-levantar-dinamica');
            const btnProxy = document.getElementById('btn-levantar-proxy');

            // Habilitar/deshabilitar según el estado del agregador y de cada fuente
            if (btnEstatica) {
                btnEstatica.disabled = !data.agregadorRunning || !data.fuenteEstaticaRunning;
                btnEstatica.title = (!data.agregadorRunning || !data.fuenteEstaticaRunning)
                    ? 'Requiere que Agregador y Fuente Estática estén corriendo'
                    : 'Registrar fuente estática en el agregador';
            }

            if (btnDinamica) {
                btnDinamica.disabled = !data.agregadorRunning || !data.fuenteDinamicaRunning;
                btnDinamica.title = (!data.agregadorRunning || !data.fuenteDinamicaRunning)
                    ? 'Requiere que Agregador y Fuente Dinámica estén corriendo'
                    : 'Registrar fuente dinámica en el agregador';
            }

            if (btnProxy) {
                btnProxy.disabled = !data.agregadorRunning || !data.fuenteProxyRunning;
                btnProxy.title = (!data.agregadorRunning || !data.fuenteProxyRunning)
                    ? 'Requiere que Agregador y Fuente Proxy estén corriendo'
                    : 'Registrar fuente proxy en el agregador';
            }
        })
        .catch(error => {
            console.error('Error al actualizar estado de botones:', error);
        });
}

// Auto-actualizar estado cada 30 segundos
setInterval(function () {
    if (document.querySelector('.microservices-section')) {
        fetch('/panel/microservicios/status', {
            credentials: 'include'
        })
            .then(response => response.json())
            .then(data => {
                // Actualizar FuenteEstatica
                const fuenteEstaticaStatus = document.querySelector('.microservice-item:nth-child(1) .microservice-status');
                if (fuenteEstaticaStatus) {
                    fuenteEstaticaStatus.textContent = data.fuenteEstaticaRunning ? 'CORRIENDO' : 'DETENIDO';
                    fuenteEstaticaStatus.className = 'microservice-status ' + (data.fuenteEstaticaRunning ? 'status-running' : 'status-stopped');
                }

                // Actualizar FuenteDinamica
                const fuenteDinamicaStatus = document.querySelector('.microservice-item:nth-child(2) .microservice-status');
                if (fuenteDinamicaStatus) {
                    fuenteDinamicaStatus.textContent = data.fuenteDinamicaRunning ? 'CORRIENDO' : 'DETENIDO';
                    fuenteDinamicaStatus.className = 'microservice-status ' + (data.fuenteDinamicaRunning ? 'status-running' : 'status-stopped');
                }

                // Actualizar FuenteProxy
                const fuenteProxyStatus = document.querySelector('.microservice-item:nth-child(3) .microservice-status');
                if (fuenteProxyStatus) {
                    fuenteProxyStatus.textContent = data.fuenteProxyRunning ? 'CORRIENDO' : 'DETENIDO';
                    fuenteProxyStatus.className = 'microservice-status ' + (data.fuenteProxyRunning ? 'status-running' : 'status-stopped');
                }

                // Actualizar Agregador
                const agregadorStatus = document.querySelector('.microservice-item:nth-child(4) .microservice-status');
                if (agregadorStatus) {
                    agregadorStatus.textContent = data.agregadorRunning ? 'CORRIENDO' : 'DETENIDO';
                    agregadorStatus.className = 'microservice-status ' + (data.agregadorRunning ? 'status-running' : 'status-stopped');
                }

                // Actualizar estado de los botones de levantar fuentes
                actualizarEstadoBotonesLevantar();
            })
            .catch(error => console.error('Error actualizando estado:', error));
    }
}, 30000);

// Cerrar modal al hacer click fuera
window.onclick = function (event) {
    const modal = document.getElementById('modal-configurar-fuentes');
    if (event.target === modal) {
        cerrarModal();
    }

    const modalProxyRecursiva = document.getElementById('modal-proxy-recursiva');
    if (event.target === modalProxyRecursiva) {
        cerrarModalProxyRecursiva();
    }

    const modalProxyExterna = document.getElementById('modal-proxy-externa');
    if (event.target === modalProxyExterna) {
        cerrarModalProxyExterna();
    }
}

/**
 * Funciones para Proxy Recursiva
 */
function abrirModalProxyRecursiva() {
    document.getElementById('modal-proxy-recursiva').style.display = 'flex';
}

function cerrarModalProxyRecursiva() {
    const modal = document.getElementById('modal-proxy-recursiva');
    if (modal) {
        modal.style.display = 'none';
        document.getElementById('form-proxy-recursiva').reset();
    }
}

function enviarProxyRecursiva(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;

    submitBtn.textContent = 'Creando...';
    submitBtn.disabled = true;

    fetch('/panel/proxy/configurar/recursiva', {
        method: 'POST',
        body: formData,
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            Toast.success(data.message || 'Proxy Recursiva creada correctamente');
            cerrarModalProxyRecursiva();
            setTimeout(() => window.location.reload(), 1500);
        } else {
            throw new Error(data.message || 'Error al crear proxy recursiva');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        Toast.error('Error: ' + error.message);
    })
    .finally(() => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

/**
 * Funciones para Proxy Externa
 */
function abrirModalProxyExterna() {
    document.getElementById('modal-proxy-externa').style.display = 'flex';
}

function cerrarModalProxyExterna() {
    const modal = document.getElementById('modal-proxy-externa');
    if (modal) {
        modal.style.display = 'none';
        document.getElementById('form-proxy-externa').reset();
    }
}

function enviarProxyExterna(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;

    submitBtn.textContent = 'Creando...';
    submitBtn.disabled = true;

    fetch('/panel/proxy/configurar/externa', {
        method: 'POST',
        body: formData,
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            Toast.success(data.message || 'Proxy Externa creada correctamente');
            cerrarModalProxyExterna();
            setTimeout(() => window.location.reload(), 1500);
        } else {
            throw new Error(data.message || 'Error al crear proxy externa');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        Toast.error('Error: ' + error.message);
    })
    .finally(() => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

/**
 * Navega a la página de observabilidad
 * Redirige en la misma pestaña
 */
function abrirObservabilidad() {
    console.log('[Panel Control] Redirigiendo a observabilidad...');
    window.location.href = '/api/observabilidad/dashboard-page';
}
