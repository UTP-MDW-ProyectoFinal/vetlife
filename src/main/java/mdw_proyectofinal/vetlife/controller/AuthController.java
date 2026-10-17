package mdw_proyectofinal.vetlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.service.AuthService;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String usuario,
            @RequestParam String contrasena,
            HttpSession sesion,
            Model modelo) {
        if (authService.login(usuario, contrasena, sesion)) {
            Object rol = sesion.getAttribute(AuthService.SESION_ROL);
            return "admin".equals(rol) ? "redirect:/admin" : "redirect:/perfil";
        }
        modelo.addAttribute("error", "Credenciales incorrectas. Verifica tu usuario y contraseña.");
        return "users/login";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String telefono,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String confirmarContrasena,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes redirectAttributes) {
        String error = authService.registrar(nombre, apellido, dni, telefono, correo, contrasena,
                confirmarContrasena, sesion);
        if (error != null) {
            modelo.addAttribute("error", error);
            modelo.addAttribute("nombre", nombre);
            modelo.addAttribute("apellido", apellido);
            modelo.addAttribute("dni", dni);
            modelo.addAttribute("telefono", telefono);
            modelo.addAttribute("correo", correo);
            return "users/registro";
        }
        redirectAttributes.addFlashAttribute("mensaje", "Cuenta creada. Ahora registra a tu mascota.");
        return "redirect:/mascota?from=registro";
    }

    @GetMapping("/logout")
    public String logout(HttpSession sesion) {
        authService.logout(sesion);
        return "redirect:/";
    }
}
