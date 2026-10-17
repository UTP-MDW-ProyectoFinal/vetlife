package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/servicios")
    public String servicios() {
        return "users/servicios";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "users/contacto";
    }

    @GetMapping("/citas")
    public String citas() {
        return "users/citas";
    }

    @GetMapping("/reservas")
    public String reservas() {
        return "users/reservas";
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "users/registro";
    }
}
