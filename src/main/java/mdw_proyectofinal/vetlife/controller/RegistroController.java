package mdw_proyectofinal.vetlife.controller;

import mdw_proyectofinal.vetlife.service.UserAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    private final UserAccountService userAccountService;

    public RegistroController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "users/registro";
    }

    @PostMapping("/registro")
    public String registrarCuenta(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String telefono,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String confirmarContrasena,
            RedirectAttributes redirectAttributes) {
        if (nombre.isBlank() || apellido.isBlank() || dni.isBlank() || telefono.isBlank()
                || correo.isBlank() || contrasena.length() < 8) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Completa todos los campos y usa una contraseña de al menos 8 caracteres.");
            return "redirect:/registro";
        }

        if (!contrasena.equals(confirmarContrasena)) {
            redirectAttributes.addFlashAttribute("mensajeError", "Las contraseñas no coinciden.");
            return "redirect:/registro";
        }

        boolean creada = userAccountService.registrar(nombre, apellido, dni, telefono, correo, contrasena);
        if (!creada) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ya existe una cuenta con ese correo o DNI.");
            return "redirect:/registro";
        }

        redirectAttributes.addFlashAttribute("mensajeExito", "Tu cuenta fue creada. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}