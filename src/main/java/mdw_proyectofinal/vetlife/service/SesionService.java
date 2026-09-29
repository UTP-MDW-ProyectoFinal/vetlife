package mdw_proyectofinal.vetlife.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.model.Usuario;

// Ticket de Brayan: sesion en memoria con auto-login demo
@Service
public class SesionService {

    public static final String ATRIBUTO_USUARIO = "usuario";

    private static final Usuario USUARIO_DEMO = new Usuario("U-001", "Grisel Torres", "grisel@vetlife.com");

    public Usuario obtener(HttpSession sesion) {
        Object usuario = sesion.getAttribute(ATRIBUTO_USUARIO);
        return usuario instanceof Usuario ? (Usuario) usuario : null;
    }

    public Usuario obtenerOCrear(HttpSession sesion) {
        Usuario usuario = obtener(sesion);
        if (usuario == null) {
            usuario = USUARIO_DEMO;
            sesion.setAttribute(ATRIBUTO_USUARIO, usuario);
        }
        return usuario;
    }

    public void cerrar(HttpSession sesion) {
        sesion.invalidate();
    }
}
// Fin Ticket de Brayan
