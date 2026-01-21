// Evita inicializar dos veces si se incluye en varias páginas
(function () {
    if (window.__AUTH_INIT__) return;
    window.__AUTH_INIT__ = true;

    // ===== Inyecta estilos =====
    function injectAuthStyles() {
        if (document.getElementById("auth-styles")) return;
        const css = `
      .popup{display:none;position:fixed;top:0;left:0;width:100%;height:100%;
             background:rgba(0,0,0,0.6);backdrop-filter:blur(4px);
             justify-content:center;align-items:center;z-index:1000}
      .popup-content{background:#fff;padding:30px;border-radius:12px;min-width:320px;max-width:500px;width:90%;
                     box-shadow:0 6px 18px rgba(0,0,0,0.3);text-align:left;position:relative}
      .popup-content h2{margin-bottom:16px;font-size:22px}
      .popup-content label{display:block;margin-top:10px;margin-bottom:6px;font-weight:bold}
      .popup-content input{width:100%;padding:10px;margin-bottom:12px;border:1px solid #ccc;border-radius:6px}
      .popup-content button{margin-top:8px;padding:10px 16px;background:#333;color:#fff;border:none;border-radius:6px;cursor:pointer}
      .popup-content .close{position:absolute;top:8px;right:12px;font-size:20px;font-weight:bold;cursor:pointer}
      .popup-content a{display:block;margin-top:15px;text-align:left;color:#666;text-decoration:none;font-size:14px}
      .popup-content a:hover{color:#333;text-decoration:underline}
      .profile{position:relative;display:flex;align-items:center;gap:8px;cursor:pointer}
      .profile .dropdown{display:none;position:absolute;top:50px;right:0;background:#fff;border:1px solid #ccc;border-radius:8px;
                         box-shadow:0 4px 12px rgba(0,0,0,0.2);min-width:180px;z-index:100}
      .profile.show .dropdown{display:block}
      .profile .dropdown a{display:block;padding:10px;text-decoration:none;color:#000}
      .profile .dropdown a:hover{background:#eee}
      .avatar{width:40px;height:40px;border-radius:50%;border:2px solid #333;object-fit:cover}
      .hidden{display:none !important}
      .password-container{position:relative;width:100%;display:flex;align-items:center}
      .password-container input{width:100%;padding:10px;padding-right:40px;margin-bottom:12px;border:1px solid #ccc;border-radius:6px}
      .toggle-password{position:absolute;right:10px;top:10px;background:none;border:none;cursor:pointer;padding:5px;
                      display:flex;align-items:center;justify-content:center;z-index:10}
      .toggle-password img{width:20px;height:20px;opacity:0.6;transition:opacity 0.2s}
      .toggle-password:hover img{opacity:1}
    `;
        const style = document.createElement("style");
        style.id = "auth-styles";
        style.textContent = css;
        document.head.appendChild(style);
    }

    // ===== Inyecta popups si no existen =====
    function ensureAuthPopups() {
        if (document.getElementById("popup-login")) return;

        const wrap = document.createElement("div");
        wrap.innerHTML = `
      <div id="popup-login" class="popup">
        <div class="popup-content">
          <span class="close">&times;</span>
          <h2>Iniciar Sesión</h2>
          <label for="usuario-login">Nombre de usuario:</label>
          <input type="text" id="usuario-login" placeholder="Escribe tu nombre de usuario">
          <label for="password-login">Contraseña:</label>
          <div class="password-container">
            <input type="password" id="password-login" placeholder="Ingresa tu contraseña">
            <button type="button" class="toggle-password" data-target="password-login">
              <img src="/images/eye.svg" alt="Mostrar contraseña">
            </button>
          </div>
          <button id="login-btn">Ingresar</button>
          <a href="#" id="forgot-pass">¿Olvidaste tu contraseña?</a>
        </div>
      </div>

      <div id="popup-register" class="popup">
        <div class="popup-content">
          <span class="close">&times;</span>
          <h2>Crear una cuenta</h2>
          <label for="usuario-register">Nombre de usuario:</label>
          <input type="text" id="usuario-register" placeholder="Escribe un nombre de usuario">
          <label for="email-register">Correo electrónico:</label>
          <input type="email" id="email-register" placeholder="Ingresa tu correo electrónico">
          <label for="password-register">Contraseña:</label>
          <div class="password-container">
            <input type="password" id="password-register" placeholder="Crea una contraseña">
            <button type="button" class="toggle-password" data-target="password-register">
              <img src="/images/eye.svg" alt="Mostrar contraseña">
            </button>
          </div>
          <label for="password-repeat">Repite tu contraseña:</label>
          <div class="password-container">
            <input type="password" id="password-repeat" placeholder="Repite la contraseña">
            <button type="button" class="toggle-password" data-target="password-repeat">
              <img src="/images/eye.svg" alt="Mostrar contraseña">
            </button>
          </div>
          <button id="register-btn">Registrarse</button>
        </div>
      </div>

      <div id="popup-forgot" class="popup">
        <div class="popup-content">
          <span class="close">&times;</span>
          <h2>Recuperar Contraseña</h2>
          <label for="email-forgot">Nombre de usuario:</label>
          <input type="text" id="email-forgot" placeholder="Ingresa tu nombre de usuario">
          <button id="reset-btn">Confirmar</button>
          <a href="#" id="back-to-login" style="display:block;margin-top:10px;">← Volver al login</a>
        </div>
      </div>
    `;
        document.body.appendChild(wrap);
    }

    // ===== Utilidades =====
    function closeAllPopups() {
        document.querySelectorAll(".popup").forEach(p => p.style.display = "none");
    }

    function openPopup(id) {
        closeAllPopups();
        const el = document.getElementById(id);
        if (el) el.style.display = "flex";
    }

    // ===== Avatar dinámico =====
    function generarAvatar(usuario, imgEl) {
        if (!imgEl) return;
        const letra = usuario.charAt(0).toUpperCase();
        const colores = ["#FF5733", "#33A1FF", "#28A745", "#FFC300", "#8E44AD", "#E67E22", "#16A085"];
        const color = colores[usuario.charCodeAt(0) % colores.length];
        const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" width="80" height="80">
        <circle cx="40" cy="40" r="38" fill="${color}" />
        <text x="50%" y="55%" text-anchor="middle" font-size="36" fill="white" font-family="Arial" dy=".3em">${letra}</text>
      </svg>`;
        imgEl.src = "data:image/svg+xml;base64," + btoa(svg);
    }

    function mostrarPerfil(usuario) {
        const authButtons = document.getElementById("auth-buttons");
        const userProfile = document.getElementById("user-profile");
        const userName = document.getElementById("user-name");
        const avatarImg = document.querySelector(".avatar");
        if (authButtons) authButtons.classList.add("hidden");
        if (userProfile) userProfile.classList.remove("hidden");
        if (userName) userName.textContent = usuario;
        generarAvatar(usuario, avatarImg);
    }

    function ocultarPerfil() {
        const authButtons = document.getElementById("auth-buttons");
        const userProfile = document.getElementById("user-profile");
        const userName = document.getElementById("user-name");
        const avatarImg = document.querySelector(".avatar");
        if (userProfile) userProfile.classList.add("hidden");
        if (authButtons) authButtons.classList.remove("hidden");
        if (userName) userName.textContent = "";
        if (avatarImg) avatarImg.src = "iconos/avatar.png";
    }

    // ===== Login con backend =====
    async function loginUserBackend(username, password) {
        try {
            const resp = await fetch('auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, password}),
                credentials: 'include'
            });
            if (!resp.ok) throw new Error('Credenciales inválidas');
            const data = await resp.json();

            // Guardar nombre Y rol en localStorage
            localStorage.setItem('user', JSON.stringify({
                name: data.name,
                role: data.role
            }));

            mostrarPerfil(data.name);
            closeAllPopups();

            // Recargar la página para actualizar los botones según el rol
            window.location.reload();

            return true;
        } catch (err) {
            if (window.Toast) {
                Toast.error(err.message);
            }
            return false;
        }
    }

    async function registerUserBackend(username,email, password, confirmedPassword) {
        try {
            if (password !== confirmedPassword) throw new Error('Ambas contraseñas deben coincidir');
            if (username === "" || email === "" || password === "" || confirmedPassword === "") throw new Error('todos los campos deben ser completados')
            if (!email.includes("@")) throw new Error('Introdusca un direccion de email valida')
            const resp = await fetch('auth/register', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, email, password})
            });
            if (!resp.ok) {
                const errorData = await resp.json(); // ← aquí obtenés { error: "El usuario ya existe" }
                throw new Error(errorData.error || "Error desconocido");
            }
            const data = await resp.json();
            //localStorage.setItem('user', JSON.stringify(data));
            if (window.Toast) {
                Toast.success('Perfil ' + data.name + ' creado correctamente');
            }
            closeAllPopups();
            return true;
        } catch (err) {
            if (window.Toast) {
                Toast.error(err.message);
            }
            return false;
        }
    }

    // ===== Iniciar navegación =====
    function startNavigation() {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (userData && (userData.role === 'ROLE_ADMIN' || userData.role === 'ROLE_administrador')) {
            window.location.href = '/adminHome';
        } else {
            window.location.href = '/home';
        }
    }

    // ===== Listeners =====
    function wireHeaderAuth() {
        const logoutBtn = document.getElementById("logout");

        document.getElementById("btn-login")?.addEventListener("click", () => window.location.href = '/login');
        document.getElementById("btn-register")?.addEventListener("click", () => window.location.href = '/register');

        document.getElementById("forgot-pass")?.addEventListener("click", (e) => {
            e.preventDefault();
            openPopup("popup-forgot");
        });

       /* document.getElementById("login-btn")?.addEventListener("click", async () => {
            const usuario = (document.getElementById("usuario-login")?.value || "").trim();
            const password = document.getElementById("password-login")?.value || "";
            await loginUserBackend(usuario, password);
        });

        document.getElementById("register-btn")?.addEventListener("click", async () =>{
            const usuario = (document.getElementById("usuario-register")?. value || "").trim();
            const password = document.getElementById("password-register")?.value || "";
            const confirmedPassword = document.getElementById("password-repeat")?.value || "";
            const mail = document.getElementById("email-register")?.value || "";
            await registerUserBackend(usuario, mail, password, confirmedPassword);
        });*/

        /*document.getElementById("register-btn")?.addEventListener("click", async () => {
            const usuario = (document.getElementById("usuario-register")?.value || "").trim();
            const pass = document.getElementById("password-register")?.value || "";
            const passRepeat = document.getElementById("password-repeat")?.value || "";
            if (usuario && pass && pass === passRepeat) {
                const resp = await fetch('/auth/register', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({username: usuario, password: pass})
                });
                if (resp.ok) loginUserBackend(usuario, pass);
                else alert("Error al registrar usuario");
            } else {
                alert("Complete los campos y asegúrese de que las contraseñas coincidan.");
            }
        });*/

        document.getElementById("reset-btn")?.addEventListener("click", async () => {
            const email = (document.getElementById("email-forgot")?.value || "").trim();
            if (email) {
                try {
                    const resp = await fetch('/auth/recuperar', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({email: email})
                    });
                    if (resp.ok) {
                        if (window.Toast) {
                            Toast.success("Se ha enviado un enlace de recuperación a tu correo electrónico.");
                        }
                        closeAllPopups();
                    } else {
                        if (window.Toast) {
                            Toast.error("Correo no encontrado.");
                        }
                    }
                } catch (error) {
                    if (window.Toast) {
                        Toast.error("Error al procesar la solicitud. Intenta nuevamente.");
                    }
                }
            } else {
                if (window.Toast) {
                    Toast.warning("Por favor, ingresa tu correo electrónico.");
                }
            }
        });

        document.getElementById("back-to-login")?.addEventListener("click", (e) => {
            e.preventDefault();
            openPopup("popup-login");
        });

        if (logoutBtn) logoutBtn.onclick = (e) => {
            e.preventDefault();
            localStorage.removeItem("user");
            ocultarPerfil();
        };
    }

    // ===== Cerrar popups con la cruz o fondo =====
    function wirePopupCloseButtons() {
        // Clic en la cruz
        document.querySelectorAll(".popup .close").forEach(btn => {
            btn.addEventListener("click", closeAllPopups);
        });

        // Clic fuera del contenido
        document.querySelectorAll(".popup").forEach(popup => {
            popup.addEventListener("click", (e) => {
                if (e.target === popup) closeAllPopups();
            });
        });
    }

    // ===== Init =====
    document.addEventListener("DOMContentLoaded", () => {
        injectAuthStyles();
        ensureAuthPopups();
        wireHeaderAuth();
        wirePopupCloseButtons(); // <-- aquí activamos las cruces y clic afuera

        // Toggle password visibility
        document.addEventListener('mousedown', (e) => {
            if (e.target.closest('.toggle-password')) {
                e.preventDefault();
                const btn = e.target.closest('.toggle-password');
                const targetId = btn.getAttribute('data-target');
                const input = document.getElementById(targetId);
                if (input) input.type = 'text';
            }
        });

        document.addEventListener('mouseup', (e) => {
            if (e.target.closest('.toggle-password')) {
                e.preventDefault();
                const btn = e.target.closest('.toggle-password');
                const targetId = btn.getAttribute('data-target');
                const input = document.getElementById(targetId);
                if (input) input.type = 'password';
            }
        });

        document.addEventListener('mouseleave', (e) => {
            // Verificar que e.target es un elemento válido antes de usar closest
            if (e.target && e.target.closest && e.target.closest('.toggle-password')) {
                const btn = e.target.closest('.toggle-password');
                const targetId = btn.getAttribute('data-target');
                const input = document.getElementById(targetId);
                if (input) input.type = 'password';
            }
        }, true); // Usar captura para eventos de salida

        const userData = JSON.parse(localStorage.getItem("user") || "null");
        if (userData) mostrarPerfil(userData.name);
    });

    window.startNavigation = startNavigation;

})();
