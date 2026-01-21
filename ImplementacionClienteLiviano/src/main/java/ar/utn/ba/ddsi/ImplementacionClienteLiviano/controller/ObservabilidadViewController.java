package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para servir la página HTML del dashboard de observabilidad
 * Solo accesible para usuarios con rol ADMIN
 */
@Controller
@RequestMapping("/api/observabilidad")
public class ObservabilidadViewController {

    /**
     * Sirve la página HTML del dashboard de observabilidad
     * Solo accesible para administradores
     */
    @GetMapping("/dashboard-page")
    @PreAuthorize("hasRole('ADMIN')")
    public String getDashboardPage() {
        return "observabilidad";  // Retorna el template observabilidad.html
    }
}

