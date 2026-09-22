package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/perfil")
    public String verPerfil(Model model) {
        model.addAttribute("nombreUsuario", "Grisel");
        return "perfil";
    }

    @GetMapping("/mascota")
    public String verMascota(Model model) {
        model.addAttribute("nombreMascota", "Fido");
        return "mascota";
    }
}