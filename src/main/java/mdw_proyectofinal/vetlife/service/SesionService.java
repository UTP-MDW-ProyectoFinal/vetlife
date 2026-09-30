package mdw_proyectofinal.vetlife.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.model.Usuario;

// Ticket de Brayan: sesion en memoria con login simple de la cuenta demo
@Service
public class SesionService {

    public static final String ATRIBUTO_USUARIO = "usuario";

    private final String usuarioDemo;
    private final String contrasenaDemo;
    private final String nombreDemo;
    private final String correoDemo;

    public SesionService(
            @Value("${vetlife.demo.usuario:demo@vetlife.com}") String usuarioDemo,
            @Value("${vetlife.demo.contrasena:vetlife123}") String contrasenaDemo,
            @Value("${vetlife.demo.nombre:Grisel Torres}") String nombreDemo,
            @Value("${vetlife.demo.correo:grisel@vetlife.com}") String correoDemo) {
        this.usuarioDemo = usuarioDemo;
        this.contrasenaDemo = contrasenaDemo;
        this.nombreDemo = nombreDemo;
        this.correoDemo = correoDemo;
    }

    public Usuario obtener(HttpSession sesion) {
        Object usuario = sesion.getAttribute(ATRIBUTO_USUARIO);
        return usuario instanceof Usuario usuarioSesion ? usuarioSesion : null;
    }

    public boolean estaLogueado(HttpSession sesion) {
        return obtener(sesion) != null;
    }

    public boolean autenticar(String usuario, String contrasena, HttpSession sesion) {
        if (!usuarioDemo.equalsIgnoreCase(usuario.trim()) || !contrasenaDemo.equals(contrasena)) {
            return false;
        }
        sesion.setAttribute(ATRIBUTO_USUARIO, new Usuario("U-DEMO", nombreDemo, correoDemo));
        return true;
    }

    public void cerrar(HttpSession sesion) {
        sesion.invalidate();
    }
}
// Fin Ticket de Brayan