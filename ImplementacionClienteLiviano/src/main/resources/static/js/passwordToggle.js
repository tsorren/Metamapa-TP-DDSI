/**
 * Script para manejar la funcionalidad de mostrar/ocultar contraseñas
 * Funcionalidad: Click en ojo → Alterna entre mostrar/ocultar en loop infinito
 */

(function() {
    'use strict';

    function initPasswordToggle() {
        // Seleccionar todos los botones de toggle de contraseña
        const toggleButtons = document.querySelectorAll('.toggle-password');

        // console.log('[Password Toggle] Inicializado. Botones encontrados:', toggleButtons.length);

        toggleButtons.forEach(function(button) {
            // Remover listeners previos para evitar duplicados
            button.removeEventListener('click', handleToggleClick);
            button.addEventListener('click', handleToggleClick);
        });
    }

    function handleToggleClick(e) {
        e.preventDefault();
        e.stopPropagation();

        const button = e.currentTarget;
        const targetId = button.getAttribute('data-target');
        const passwordInput = document.getElementById(targetId);
        const icon = button.querySelector('img');

        if (!passwordInput || !icon) {
            console.error('[Password Toggle] No se encontró el input o ícono para:', targetId);
            return;
        }

        // TOGGLE: Alternar entre password (oculto) y text (visible)
        const isPasswordHidden = passwordInput.type === 'password';

        if (isPasswordHidden) {
            // Estado actual: OCULTO → Cambiar a VISIBLE
            passwordInput.type = 'text';
            icon.src = icon.src.replace('eye-closed.svg', 'eye-open.svg');
            icon.alt = 'Ocultar contraseña';
            // console.log('[Password Toggle] ✅ VISIBLE:', targetId);
        } else {
            // Estado actual: VISIBLE → Cambiar a OCULTO
            passwordInput.type = 'password';
            icon.src = icon.src.replace('eye-open.svg', 'eye-closed.svg');
            icon.alt = 'Mostrar contraseña';
            // console.log('[Password Toggle] 🔒 OCULTO:', targetId);
        }
    }

    // Inicializar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initPasswordToggle);
    } else {
        initPasswordToggle();
    }

    // Re-inicializar si se cargan popups dinámicamente
    window.addEventListener('load', initPasswordToggle);

})();

