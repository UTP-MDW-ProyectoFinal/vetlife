package mdw_proyectofinal.vetlife.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.model.Usuario;

// Ticket de Brayan: sesion en memoria que reutiliza el login ya existente
@Service
public class SesionService {

    public static final String ATRIBUTO_USUARIO = "usuario";

    public Usuario obtener(HttpSession sesion) {
        Object usuario = sesion.getAttribute(ATRIBUTO_USUARIO);
        if (usuario instanceof Usuario usuarioSesion) {
            return usuarioSesion;
        }
        return recuperarUsuarioDeSesion(sesion);
    }

    public void cerrar(HttpSession sesion) {
        sesion.invalidate();
    }

    // El login ya existente guarda nombre y correo en la sesion, se aprovechan para armar el usuario
    private Usuario recuperarUsuarioDeSesion(HttpSession sesion) {
        Object nombre = sesion.getAttribute("nombre");
        if (nombre == null || nombre.toString().isBlank()) {
            return null;
        }

        Object correo = sesion.getAttribute("correo");
        Usuario usuario = new Usuario("U-SESION", nombre.toString(),
                correo == null ? null : correo.toString());
        sesion.setAttribute(ATRIBUTO_USUARIO, usuario);
        return usuario;
    }
}
// Fin Ticket de Brayan
