package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.model.Usuario;
import mdw_proyectofinal.vetlife.repository.UsuarioRepository;
import mdw_proyectofinal.vetlife.service.AuthService;
import mdw_proyectofinal.vetlife.service.MascotaService;

@Controller
public class UserController {

    private final UsuarioRepository usuarioRepository;
    private final MascotaService mascotaService;

    public UserController(UsuarioRepository usuarioRepository, MascotaService mascotaService) {
        this.usuarioRepository = usuarioRepository;
        this.mascotaService = mascotaService;
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession sesion, Model modelo) {
        Integer usuarioId = (Integer) sesion.getAttribute(AuthService.SESION_USUARIO_ID);
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId).orElse(null);
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("mascotas", mascotaService.listarPorDuenio(usuarioId));
        return "users/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String telefono,
            @RequestParam String correo,
            HttpSession sesion,
            RedirectAttributes redirectAttributes) {
        Integer usuarioId = (Integer) sesion.getAttribute(AuthService.SESION_USUARIO_ID);
        usuarioRepository.actualizar(usuarioId, nombre.trim(), apellido.trim(), dni.trim(), telefono.trim(),
                correo.trim());
        sesion.setAttribute(AuthService.SESION_NOMBRE, nombre.trim());
        sesion.setAttribute(AuthService.SESION_CORREO, correo.trim());
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");
        return "redirect:/perfil";
    }

    @GetMapping("/mascota")
    public String mascota(@RequestParam(required = false) String from,
            @RequestParam(required = false) String error,
            Model modelo) {
        modelo.addAttribute("error", error);
        return "users/mascota";
    }

    @PostMapping("/mascota")
    public String registrarMascota(@RequestParam String nombre,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) String edad,
            @RequestParam(required = false) String sexo,
            HttpSession sesion,
            RedirectAttributes redirectAttributes) {
        Integer usuarioId = (Integer) sesion.getAttribute(AuthService.SESION_USUARIO_ID);
        String error = mascotaService.registrar(nombre, especie, raza, edad, sexo, usuarioId);
        if (error != null) {
            redirectAttributes.addAttribute("error", error);
            return "redirect:/mascota";
        }
        redirectAttributes.addFlashAttribute("mensaje", "Mascota registrada correctamente.");
        return "redirect:/perfil";
    }
}
