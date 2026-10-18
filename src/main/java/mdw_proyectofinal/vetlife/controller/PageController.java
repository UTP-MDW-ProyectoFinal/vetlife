package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import mdw_proyectofinal.vetlife.repository.ServicioRepository;

@Controller
public class PageController {

    private final ServicioRepository servicioRepository;

    public PageController(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @GetMapping("/")
    public String inicio(Model modelo) {
        modelo.addAttribute("serviciosInicio", servicioRepository.listarActivos().stream().limit(6).toList());
        return "index";
    }

    @GetMapping("/servicios")
    public String servicios(Model modelo) {
        modelo.addAttribute("categorias", servicioRepository.listarPorCategoria());
        return "users/servicios";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "users/contacto";
    }

    @GetMapping("/citas")
    public String citas(Model modelo) {
        modelo.addAttribute("servicios", servicioRepository.listarActivos());
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
