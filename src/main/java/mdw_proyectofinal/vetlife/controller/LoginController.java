package mdw_proyectofinal.vetlife.controller;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.service.UserAccountService;
import mdw_proyectofinal.vetlife.service.UserAccountService.CuentaUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserAccountService userAccountService;

    public LoginController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", destinoSeguro(redirect));
        return "users/login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String usuario,
            @RequestParam String contrasena,
            @RequestParam(required = false) String redirect,
            HttpSession session,
            Model model) {
        String destino = destinoSeguro(redirect);
        model.addAttribute("redirect", destino);

        if (("admin".equalsIgnoreCase(usuario.trim()) || "admin@vetlife.com".equalsIgnoreCase(usuario.trim()))
                && "admin123".equals(contrasena)) {
            session.setAttribute("rol", "administrador");
            session.setAttribute("nombre", "Dra. Ramírez");
            session.setAttribute("correo", "admin@vetlife.com");
            model.addAttribute("loginCorrecto", true);
            model.addAttribute("rolUsuario", "administrador");
            model.addAttribute("nombreUsuario", "Dra. Ramírez");
            model.addAttribute("correoUsuario", "admin@vetlife.com");
            model.addAttribute("destino", "/admin/dashboard");
            return "users/login";
        }

        CuentaUsuario cuenta = userAccountService.autenticar(usuario, contrasena);
        if (cuenta == null) {
            boolean identificadorExiste = userAccountService.existeIdentificador(usuario);
            model.addAttribute("mensajeError", identificadorExiste
                    ? "La contraseña no es correcta. Verifica tus datos e inténtalo de nuevo."
                    : "No encontramos una cuenta con ese correo o DNI. Crea una cuenta para continuar.");
            model.addAttribute("sinCuenta", !identificadorExiste);
            return "users/login";
        }

        session.setAttribute("rol", "usuario");
        session.setAttribute("nombre", cuenta.nombre());
        session.setAttribute("correo", cuenta.correo());
        model.addAttribute("loginCorrecto", true);
        model.addAttribute("rolUsuario", "usuario");
        model.addAttribute("nombreUsuario", cuenta.nombre());
        model.addAttribute("apellidoUsuario", cuenta.apellido());
        model.addAttribute("dniUsuario", cuenta.dni());
        model.addAttribute("telefonoUsuario", cuenta.telefono());
        model.addAttribute("correoUsuario", cuenta.correo());
        model.addAttribute("destino", destino);
        return "users/login";
    }

    private String destinoSeguro(String redirect) {
        if ("/citas".equals(redirect) || "citas".equals(redirect)) {
            return "/citas";
        }
        if ("/admin/dashboard".equals(redirect) || "admin".equals(redirect)) {
            return "/admin/dashboard";
        }
        if ("/reservas".equals(redirect)) {
            return "/reservas";
        }
        return "/reservas";
    }
}