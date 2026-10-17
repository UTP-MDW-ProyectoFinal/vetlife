package mdw_proyectofinal.vetlife.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.model.Usuario;
import mdw_proyectofinal.vetlife.repository.AdminRepository;
import mdw_proyectofinal.vetlife.repository.UsuarioRepository;

@Service
public class AuthService {

    public static final String SESION_ROL = "rol";
    public static final String SESION_USUARIO_ID = "usuarioId";
    public static final String SESION_ADMIN_ID = "adminId";
    public static final String SESION_NOMBRE = "nombre";
    public static final String SESION_CORREO = "correo";

    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;

    public AuthService(AdminRepository adminRepository, UsuarioRepository usuarioRepository) {
        this.adminRepository = adminRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public boolean login(String usuario, String contrasena, HttpSession sesion) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            return false;
        }

        Admin admin = adminRepository.buscarPorUsuarioOCorreo(usuario.trim())
                .filter(a -> a.getContrasena().equals(contrasena))
                .orElse(null);
        if (admin != null) {
            sesion.setAttribute(SESION_ROL, "admin");
            sesion.setAttribute(SESION_ADMIN_ID, admin.getId());
            sesion.setAttribute(SESION_NOMBRE, admin.getNombreCompleto());
            sesion.setAttribute(SESION_CORREO, admin.getCorreo());
            return true;
        }

        Usuario usuarioBuscado = usuarioRepository.buscarPorCorreo(usuario.trim())
                .filter(u -> u.getContrasena().equals(contrasena))
                .orElse(null);
        if (usuarioBuscado != null) {
            guardarUsuarioEnSesion(sesion, usuarioBuscado);
            return true;
        }

        return false;
    }

    public String registrar(String nombre, String apellido, String dni, String telefono, String correo,
            String contrasena, String confirmarContrasena, HttpSession sesion) {
        if (esVacio(nombre) || esVacio(apellido) || esVacio(dni) || esVacio(telefono)
                || esVacio(correo) || esVacio(contrasena)) {
            return "Todos los campos son obligatorios.";
        }
        if (contrasena.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres.";
        }
        if (!contrasena.equals(confirmarContrasena)) {
            return "Las contraseñas no coinciden.";
        }
        if (usuarioRepository.existeCorreo(correo.trim())) {
            return "Ya existe una cuenta con ese correo.";
        }
        if (usuarioRepository.existeDni(dni.trim())) {
            return "Ya existe una cuenta con ese DNI.";
        }

        Usuario usuario = usuarioRepository.guardar(
                new Usuario(0, nombre.trim(), apellido.trim(), dni.trim(), telefono.trim(), correo.trim(), contrasena));
        guardarUsuarioEnSesion(sesion, usuario);
        return null;
    }

    public void logout(HttpSession sesion) {
        sesion.invalidate();
    }

    private void guardarUsuarioEnSesion(HttpSession sesion, Usuario usuario) {
        sesion.setAttribute(SESION_ROL, "usuario");
        sesion.setAttribute(SESION_USUARIO_ID, usuario.getId());
        sesion.setAttribute(SESION_NOMBRE, usuario.getNombre());
        sesion.setAttribute(SESION_CORREO, usuario.getCorreo());
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
