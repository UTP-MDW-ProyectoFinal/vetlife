package mdw_proyectofinal.vetlife.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.repository.CitaRepository;
import mdw_proyectofinal.vetlife.repository.ServicioRepository;
import mdw_proyectofinal.vetlife.service.AuthService;

@Controller
public class PageController {

    private final ServicioRepository servicioRepository;
    private final CitaRepository citaRepository;

    public PageController(ServicioRepository servicioRepository, CitaRepository citaRepository) {
        this.servicioRepository = servicioRepository;
        this.citaRepository = citaRepository;
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
    public String citas(Model modelo, HttpSession sesion) {
        if (sesion == null || sesion.getAttribute(AuthService.SESION_ROL) == null) {
            return "users/citas-restringida";
        }
        modelo.addAttribute("servicios", servicioRepository.listarActivos());
        modelo.addAttribute("fechaHoy", LocalDate.now().toString());
        modelo.addAttribute("horaServidor", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        return "users/citas";
    }

    @GetMapping("/reservas")
    public String reservas(Model modelo, HttpSession sesion) {
        Integer usuarioId = (Integer) sesion.getAttribute(AuthService.SESION_USUARIO_ID);
        if (usuarioId != null) {
            modelo.addAttribute("reservas", citaRepository.listarPorUsuario(usuarioId));
        }
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
