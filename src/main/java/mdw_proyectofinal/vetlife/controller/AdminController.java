package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller 

public class AdminController {

    @GetMapping("/admin/pacientes")
    public String pacientes() {
        return "admin/pacientes";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
    

// Issue #18
    @GetMapping("/admin/gestion-citas")
    public String gestionCitas() {
        return "admin/gestion-citas";
    }

    // Issue #19
    @GetMapping("/admin/gestion-servicios")
    public String gestionServicios() {
        return "admin/gestion-servicios";
    }

}
