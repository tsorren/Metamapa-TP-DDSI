/**
 * Funcionalidad del menú desplegable de usuario
 * Script común para todas las páginas que tengan el menú de usuario
 */

document.addEventListener('DOMContentLoaded', function () {
    const profileContainer = document.querySelector('.profile');
    const profileTrigger = document.querySelector('.profile-trigger');
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.querySelector('.profile .dropdown');

    if (!profileContainer) {
        console.warn('Elementos del menú de usuario no encontrados');
        return;
    }

    // Si existe profile-trigger (menú completo con iconos)
    if (profileTrigger && userMenu) {
        // Toggle del menú al hacer click en el trigger
        profileTrigger.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            profileContainer.classList.toggle('show');
        });

        // Evitar que el menú se cierre al hacer click dentro de él
        userMenu.addEventListener('click', function (e) {
            e.stopPropagation();
        });
    }
    // Si existe dropdown simple (menú sin iconos)
    else if (dropdown) {
        // Toggle del menú al hacer click en el perfil
        profileContainer.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            profileContainer.classList.toggle('show');
        });

        // Evitar que el menú se cierre al hacer click dentro de él
        dropdown.addEventListener('click', function (e) {
            e.stopPropagation();
        });
    }

    // Cerrar menú al hacer click fuera de él
    document.addEventListener('click', function (e) {
        if (!profileContainer.contains(e.target)) {
            profileContainer.classList.remove('show');
        }
    });


    // Cerrar menú con tecla Escape
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            profileContainer.classList.remove('show');
        }
    });


    // Funcionalidad de logout
    const logoutItem = document.getElementById('logout');
    if (logoutItem) {
        logoutItem.addEventListener('click', function (e) {
            e.preventDefault();
            // Cerrar sesión y redirigir a landing page
            // console.log('Cerrando sesión...');
            window.location.href = '/';
        });
    }
});