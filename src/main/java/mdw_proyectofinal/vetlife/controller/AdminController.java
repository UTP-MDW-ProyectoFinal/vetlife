package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mdw_proyectofinal.vetlife.model.Servicio;
import mdw_proyectofinal.vetlife.model.Usuario;
import mdw_proyectofinal.vetlife.repository.MascotaRepository;
import mdw_proyectofinal.vetlife.repository.ServicioRepository;
import mdw_proyectofinal.vetlife.repository.UsuarioRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    public AdminController(MascotaRepository mascotaRepository, UsuarioRepository usuarioRepository,
            ServicioRepository servicioRepository) {
        this.mascotaRepository = mascotaRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
    }

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/citas")
    public String gestionCitas() {
        return "admin/gestion-citas";
    }

    @GetMapping("/pacientes")
    public String pacientes(Model modelo) {
        modelo.addAttribute("mascotas", mascotaRepository.listarTodas());
        modelo.addAttribute("duenios", mascotaRepository.listarTodas().stream()
                .collect(java.util.stream.Collectors.toMap(
                        m -> m.getId(),
                        m -> usuarioRepository.buscarPorId(m.getDuenioId())
                                .map(Usuario::getNombreCompleto)
                                .orElse("Sin propietario"),
                        (a, b) -> a)));
        return "admin/pacientes";
    }

    @GetMapping("/servicios")
    public String gestionServicios(Model modelo) {
        java.util.List<Servicio> servicios = servicioRepository.listarTodas();
        long activos = servicios.stream().filter(Servicio::isActivo).count();
        double precioPromedio = servicios.isEmpty() ? 0
                : servicios.stream().mapToDouble(Servicio::getPrecio).average().orElse(0);

        modelo.addAttribute("servicios", servicios);
        modelo.addAttribute("totalServicios", servicios.size());
        modelo.addAttribute("activosServicios", activos);
        modelo.addAttribute("inactivosServicios", servicios.size() - activos);
        modelo.addAttribute("precioPromedio", precioPromedio);
        return "admin/gestion-servicios";
    }
}
