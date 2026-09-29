package mdw_proyectofinal.vetlife.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.model.Cita;
import mdw_proyectofinal.vetlife.model.Mascota;
import mdw_proyectofinal.vetlife.model.Servicio;
import mdw_proyectofinal.vetlife.model.Usuario;
import mdw_proyectofinal.vetlife.repository.CitaRepository;
import mdw_proyectofinal.vetlife.repository.MascotaRepository;
import mdw_proyectofinal.vetlife.repository.ServicioRepository;
import mdw_proyectofinal.vetlife.service.SesionService;

@Controller
public class UserController {

    private final ServicioRepository servicioRepository;
    private final MascotaRepository mascotaRepository;
    private final CitaRepository citaRepository;
    private final SesionService sesionService;

    public UserController(ServicioRepository servicioRepository, MascotaRepository mascotaRepository,
            CitaRepository citaRepository, SesionService sesionService) {
        this.servicioRepository = servicioRepository;
        this.mascotaRepository = mascotaRepository;
        this.citaRepository = citaRepository;
        this.sesionService = sesionService;
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
        model.addAttribute("usuario", sesionService.obtenerOCrear(sesion));
        model.addAttribute("servicios", servicioRepository.findAllActivos());
        model.addAttribute("mascotas", mascotaRepository.findAll());
        model.addAttribute("fechaHoy", LocalDate.now().toString());
        model.addAttribute("horarios", generarHorarios());
        return "users/citas";
    }

    @PostMapping("/citas")
    public String reservarCita(@RequestParam String servicioId, @RequestParam String mascotaId,
            @RequestParam String fecha, @RequestParam String hora,
            @RequestParam(required = false) String notas, HttpSession sesion) {
        Usuario usuario = sesionService.obtenerOCrear(sesion);

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
        model.addAttribute("usuario", sesionService.obtenerOCrear(sesion));
        model.addAttribute("categorias", agruparPorCategoria(servicioRepository.findAllActivos()));
        return "users/servicios";
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesionService.cerrar(sesion);
        return "redirect:/";
    }

    private List<HorarioDisponible> generarHorarios() {
        List<HorarioDisponible> horarios = new ArrayList<>();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(17, 30);
        DateTimeFormatter formatoValor = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT);
        DateTimeFormatter formatoEtiqueta = DateTimeFormatter.ofPattern("hh:mm a", Locale.ROOT);

        for (LocalTime hora = inicio; !hora.isAfter(fin); hora = hora.plusMinutes(30)) {
            horarios.add(new HorarioDisponible(hora.format(formatoValor), hora.format(formatoEtiqueta)));
        }
        return horarios;
    }

    private List<CategoriaServicio> agruparPorCategoria(List<Servicio> servicios) {
        Map<String, List<Servicio>> porCategoria = new LinkedHashMap<>();
        for (Servicio servicio : servicios) {
            porCategoria.computeIfAbsent(servicio.getCategoria(), clave -> new ArrayList<>()).add(servicio);
        }

        List<CategoriaServicio> categorias = new ArrayList<>();
        porCategoria.forEach((nombre, lista) -> categorias.add(new CategoriaServicio(nombre, lista)));
        return categorias;
    }

    public record HorarioDisponible(String valor, String etiqueta) {
    }

    public record CategoriaServicio(String nombre, List<Servicio> servicios) {
    }

    // Fin Controladores Citas / Servicios (Ticket de Brayan)
}
