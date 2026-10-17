package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/citas")
    public String gestionCitas() {
        return "admin/gestion-citas";
    }

    @GetMapping("/pacientes")
    public String pacientes() {
        return "admin/pacientes";
    }

    @GetMapping("/servicios")
    public String gestionServicios() {
        return "admin/gestion-servicios";
    }
}
