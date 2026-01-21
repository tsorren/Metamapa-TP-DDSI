// roleChecker.js - Verificación de roles en el frontend
(function() {
    'use strict';

    // Función para obtener los datos del usuario desde localStorage
    function getUserData() {
        try {
            return JSON.parse(localStorage.getItem('user'));
        } catch (e) {
            return null;
        }
    }

    // Función para verificar si el usuario es admin
    function isAdmin() {
        const userData = getUserData();
        return userData && (userData.role === 'ROLE_ADMIN' || userData.role === 'ROLE_administrador');
    }

    // Función para verificar si el usuario está autenticado
    function isAuthenticated() {
        const userData = getUserData();
        return userData && userData.name;
    }

    // Protección de rutas de admin
    /*function protectAdminRoutes() {
        const adminPaths = ['/adminHome', '/admin/colecciones', '/admin/solicitudes', '/panelControl'];
        const currentPath = window.location.pathname;

        if (adminPaths.some(path => currentPath.includes(path))) {
            if (!isAdmin()) {
                console.warn('Acceso denegado: se requiere rol de administrador');
                window.location.href = '/home';
            }
        }
    }*/

    // Protección de rutas autenticadas (solo cargar hechos requiere autenticación)
    /*function protectAuthenticatedRoutes() {
        const authenticatedPaths = ['/hechos-creados', '/cargar-hecho'];
        const currentPath = window.location.pathname;

        if (authenticatedPaths.some(path => currentPath.includes(path))) {
            if (!isAuthenticated()) {
                console.warn('Acceso denegado: se requiere autenticación');
                Toast.warning('Debes iniciar sesión para acceder a esta página');
                window.location.href = '/landing';
            }
        }
    }*/

    // Mostrar/ocultar elementos según el rol
    function toggleAdminElements() {
        const adminElements = document.querySelectorAll('[data-admin-only]');
        const userElements = document.querySelectorAll('[data-user-only]');
        const guestElements = document.querySelectorAll('[data-guest-only]');

        if (isAdmin()) {
            adminElements.forEach(el => el.style.display = '');
            userElements.forEach(el => el.style.display = '');
            guestElements.forEach(el => el.style.display = 'none');
        } else if (isAuthenticated()) {
            adminElements.forEach(el => el.style.display = 'none');
            userElements.forEach(el => el.style.display = '');
            guestElements.forEach(el => el.style.display = 'none');
        } else {
            adminElements.forEach(el => el.style.display = 'none');
            userElements.forEach(el => el.style.display = 'none');
            guestElements.forEach(el => el.style.display = '');
        }
    }

    // Ejecutar al cargar la página
    /*document.addEventListener('DOMContentLoaded', () => {
        protectAdminRoutes();
        protectAuthenticatedRoutes();
        toggleAdminElements();
    });*/

    // Exportar funciones globalmente para uso en otros scripts
    /*window.roleChecker = {
        isAdmin: isAdmin,
        isAuthenticated: isAuthenticated,
        getUserData: getUserData
    };*/
})();

