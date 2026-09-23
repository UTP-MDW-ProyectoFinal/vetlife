package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "users/contacto";
    }
}
