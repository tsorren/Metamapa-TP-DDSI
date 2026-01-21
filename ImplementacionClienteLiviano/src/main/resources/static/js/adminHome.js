(function () {
    if (window.__HOME_ADMIN_INIT__) return;
    window.__HOME_ADMIN_INIT__ = true;

    document.addEventListener("DOMContentLoaded", () => {
        // --- Botones principales ---
        document.getElementById("btn-navegar")?.addEventListener("click", () => {
            window.location.href = "/colecciones";
        });
        document.getElementById("btn-cargar")?.addEventListener("click", () => {
            window.location.href = "/cargarHecho";
        });
        document.getElementById("btn-colecciones")?.addEventListener("click", () => {
            window.location.href = "/admin/colecciones";
        });
        document.getElementById("btn-solicitudes")?.addEventListener("click", () => {
            window.location.href = "/admin/solicitudes";
        });
        document.getElementById("btn-historial-solicitudes")?.addEventListener("click", () => {
            window.location.href = "/historial-solicitudes";
        });
        document.getElementById("btn-panel_de_control")?.addEventListener("click", () => {
            window.location.href = "/panel-control";
        });
        document.getElementById("btn-register_admin")?.addEventListener("click", () => {
            window.location.href = "/register-admin";
        });

        // --- Popup Importar hechos ---
        const btnImportar = document.getElementById("btn-importar");
        const popup = document.getElementById("popup-importar");
        const closeBtn = popup?.querySelector(".close-popup");
        const confirmBtn = document.getElementById("confirm-import");
        const fileInput = document.getElementById("import-file");

        // Elementos para el preview de archivo + progreso
        const fileInfo = document.getElementById("file-info");
        const fileNameSpan = document.getElementById("file-name");
        const removeFileBtn = document.getElementById("remove-file");
        const progressBar = document.getElementById("progress-bar");

        function openImportPopup() {
            popup?.classList.remove("hidden");
            popup.style.display = "flex";
        }

        function closeImportPopup() {
            popup?.classList.add("hidden");
            popup.style.display = "none";
            resetFilePreview();
        }

        btnImportar?.addEventListener("click", openImportPopup);
        closeBtn?.addEventListener("click", closeImportPopup);
        popup?.addEventListener("click", e => {
            if (e.target === popup) closeImportPopup();
        });

        // --- Mostrar archivo y simular progreso ---
        fileInput?.addEventListener("change", () => {
            if (fileInput.files.length > 0) {
                const file = fileInput.files[0];
                fileNameSpan.textContent = file.name;
                fileInfo.classList.remove("hidden");

                // Reset progreso
                progressBar.style.width = "0%";
                let progress = 0;

                // Simulación de carga
                const interval = setInterval(() => {
                    if (progress >= 100) {
                        clearInterval(interval);
                    } else {
                        progress += 10;
                        progressBar.style.width = progress + "%";
                    }
                }, 200);
            }
        });

        const importForm = document.getElementById("import-form");
        const loader = document.getElementById("loader");

        importForm?.addEventListener("submit", (e) => {
            if (!fileInput.files.length) {
                e.preventDefault();
                if (window.Toast) {
                    Toast.warning("Por favor seleccioná un archivo para importar.");
                }
                return;
            }

            // Validar tipo de archivo
            const file = fileInput.files[0];
            const validTypes = ['.csv', '.json'];
            const fileExtension = '.' + file.name.split('.').pop().toLowerCase();

            if (!validTypes.includes(fileExtension)) {
                e.preventDefault();
                if (window.Toast) {
                    Toast.warning("Por favor seleccioná un archivo CSV o JSON válido.");
                }
                return;
            }

            // Mostrar cargando
            if (loader) {
                loader.removeAttribute("hidden");
            }

            // El formulario se enviará normalmente por POST a /fuentes/importar
            console.log("Enviando archivo:", file.name, "al servidor...");
        });

        // Eliminar archivo cargado
        removeFileBtn?.addEventListener("click", resetFilePreview);

        function resetFilePreview() {
            fileInput.value = "";
            fileInfo.classList.add("hidden");
            progressBar.style.width = "0%";
        }

        // --- Notificaciones ---
        const bellBtn = document.getElementById("btn-bell");
        const notifDropdown = document.getElementById("notif-dropdown");

        bellBtn?.addEventListener("click", () => {
            notifDropdown?.classList.toggle("hidden");
        });

        // Cierra el dropdown si clickeás afuera
        document.addEventListener("click", (e) => {
            if (
                !bellBtn?.contains(e.target) &&
                !notifDropdown?.contains(e.target)
            ) {
                notifDropdown?.classList.add("hidden");
            }
        });
    });
})();
