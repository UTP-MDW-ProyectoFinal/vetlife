package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/perfil")
    public String perfil() {
        return "users/perfil";
    }
}
