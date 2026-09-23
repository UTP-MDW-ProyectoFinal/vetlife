package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/reservas")
    public String reservas() {
        return "users/reservas";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        model.addAttribute("nombreUsuario", "Grisel");
        return "users/perfil";
    }

    @GetMapping("/mascota")
    public String verMascota(Model model) {
        model.addAttribute("nombreMascota", "Fido");
        return "users/mascota";
    }
}
