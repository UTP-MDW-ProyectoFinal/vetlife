package mdw_proyectofinal.vetlife.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.model.Cita;
import mdw_proyectofinal.vetlife.model.Mascota;
import mdw_proyectofinal.vetlife.model.Servicio;
import mdw_proyectofinal.vetlife.model.Usuario;
import mdw_proyectofinal.vetlife.repository.CitaRepository;
import mdw_proyectofinal.vetlife.repository.MascotaRepository;
import mdw_proyectofinal.vetlife.repository.ServicioRepository;
import mdw_proyectofinal.vetlife.service.CatalogoService;
import mdw_proyectofinal.vetlife.service.SesionService;

@Controller
public class UserController {

    private final ServicioRepository servicioRepository;
    private final MascotaRepository mascotaRepository;
    private final CitaRepository citaRepository;
    private final SesionService sesionService;
    private final CatalogoService catalogoService;

    public UserController(ServicioRepository servicioRepository, MascotaRepository mascotaRepository,
            CitaRepository citaRepository, SesionService sesionService, CatalogoService catalogoService) {
        this.servicioRepository = servicioRepository;
        this.mascotaRepository = mascotaRepository;
        this.citaRepository = citaRepository;
        this.sesionService = sesionService;
        this.catalogoService = catalogoService;
    }

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

    // Controladores Citas / Servicios (Ticket de Brayan): inicio

    @GetMapping("/citas")
    public String citas(Model model, HttpSession sesion) {
        model.addAttribute("usuario", sesionService.obtener(sesion));
        model.addAttribute("servicios", servicioRepository.findAllActivos());
        model.addAttribute("mascotas", mascotaRepository.findAll());
        model.addAttribute("fechaHoy", LocalDate.now().toString());
        model.addAttribute("horarios", catalogoService.generarHorarios());
        return "users/citas";
    }

    @PostMapping("/citas/acceder")
    public String accederACitas(@RequestParam String usuario, @RequestParam String contrasena,
            HttpSession sesion, RedirectAttributes redireccion) {
        if (!sesionService.autenticar(usuario, contrasena, sesion)) {
            redireccion.addFlashAttribute("mensajeError", "Las credenciales de la demo no son correctas.");
        }
        return "redirect:/citas";
    }

    @PostMapping("/citas")
    public String reservarCita(@RequestParam String servicioId, @RequestParam String mascotaId,
            @RequestParam String fecha, @RequestParam String hora,
            @RequestParam(required = false) String notas, HttpSession sesion) {
        Usuario usuario = sesionService.obtener(sesion);

        Servicio servicio = servicioRepository.findById(servicioId).orElse(null);
        Mascota mascota = mascotaRepository.findById(mascotaId).orElse(null);

        Cita cita = new Cita();
        cita.setServicio(servicio != null ? servicio.getNombre() : servicioId);
        cita.setMascotaId(mascota != null ? mascota.getId() : mascotaId);
        cita.setMascotaNombre(mascota != null ? mascota.getNombre() : "");
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setNotas(notas);
        cita.setUsuarioCorreo(usuario != null ? usuario.getCorreo() : null);
        cita.setEstado("pendiente");
        citaRepository.save(cita);

        return "redirect:/reservas";
    }

    @GetMapping("/servicios")
    public String servicios(Model model, HttpSession sesion) {
        model.addAttribute("usuario", sesionService.obtener(sesion));
        model.addAttribute("categorias", catalogoService.agruparPorCategoria(servicioRepository.findAllActivos()));
        return "users/servicios";
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesionService.cerrar(sesion);
        return "redirect:/";
    }

    // Fin Controladores Citas / Servicios (Ticket de Brayan)
}
