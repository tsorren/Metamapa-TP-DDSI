// Mostrar/ocultar contraseña
// 💡 CAMBIO: Usamos querySelectorAll para obtener TODOS los botones con la clase 'toggle-password'
document.querySelectorAll('.toggle-password').forEach(function(toggleButton) {

    // Agregamos el listener a CADA botón encontrado
    toggleButton.addEventListener('click', function() {

        // 'this' ahora es el botón individual que fue clickeado (toggleButton)
        const target = document.getElementById(this.dataset.target);
        const isPassword = target.type === 'password';

        target.type = isPassword ? 'text' : 'password';

        // Cambiamos la imagen del botón que fue clickeado
        this.querySelector('img').src = isPassword ? '/images/eye-off.svg' : '/images/eye.svg';
    });
});

// Validación de contraseña en tiempo real
const passwordInput = document.getElementById("password-register");
if (passwordInput) {
    passwordInput.addEventListener('input', function() {
        validatePasswordStrength(this.value);
    });
}

function validatePasswordStrength(password) {
    // Requisitos
    const tieneMayuscula = /[A-Z]/.test(password);
    const tieneMinuscula = /[a-z]/.test(password);
    const tieneNumero = /[0-9]/.test(password);
    const tieneEspecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);
    const longitudMinima = password.length >= 8;

    // Actualizar indicadores visuales
    updateRequirement('req-mayus', tieneMayuscula);
    updateRequirement('req-minus', tieneMinuscula);
    updateRequirement('req-numero', tieneNumero);
    updateRequirement('req-especial', tieneEspecial);
    updateRequirement('req-longitud', longitudMinima);

    return tieneMayuscula && tieneMinuscula && tieneNumero && tieneEspecial && longitudMinima;
}

function updateRequirement(elementId, isValid) {
    const element = document.getElementById(elementId);
    if (element) {
        if (isValid) {
            element.classList.remove('invalid');
            element.classList.add('valid');
            element.textContent = element.textContent.replace('✗', '✓');
        } else {
            element.classList.remove('valid');
            element.classList.add('invalid');
            element.textContent = element.textContent.replace('✓', '✗');
        }
    }
}

document.getElementById("register-btn")?.addEventListener("click", async () =>{
    const usuario = (document.getElementById("usuario-register")?. value || "").trim();
    const password = document.getElementById("password-register")?.value || "";
    const confirmedPassword = document.getElementById("confirmed-password-register")?.value || "";
    //const mail = document.getElementById("usuario-email-register")?.value || "";
    await registerAdminBackend(usuario, password, confirmedPassword);
});

async function registerAdminBackend(username, password, confirmedPassword) {
    try {
        if (username === "" || password === "" || confirmedPassword === "") {
            throw new Error('Todos los campos deben ser completados');
        }

        /*if (!email.includes("@")) {
            throw new Error('Introduce una dirección de email válida');
        }*/

        if (password !== confirmedPassword) {
            throw new Error('Ambas contraseñas deben coincidir');
        }

        // Validar fortaleza de contraseña
        if (!validatePasswordStrength(password)) {
            throw new Error('La contraseña no cumple con los requisitos de seguridad');
        }

        const resp = await fetch('auth/registerAdmin', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password})
        });

        if (!resp.ok) {
            const errorData = await resp.json();
            throw new Error(errorData.error || "Error desconocido");
        }

        const data = await resp.json();
        Toast.success('Administrador ' + data.name + ' creado correctamente.');
        // Redirigir al panel de control después de registro exitoso
        setTimeout(() => {
            window.location.href = '/panelControl';
        }, 1500);
        return true;
    } catch (err) {
        Toast.error(err.message);
        return false;
    }
}

document.getElementById("back-btn")?.addEventListener("click", async () =>{window.location.href = '/home'})