package mdw_proyectofinal.vetlife.service.api.v1;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mdw_proyectofinal.vetlife.dto.PerfilAdminResponse;
import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.repository.AdminRepository;
import mdw_proyectofinal.vetlife.utils.EncryptPassword;

/**
 * Servicio del perfil de admin (P6).
 *
 * Autenticacion con headers email/password, siguiendo el patron que ya usa la
 * capa api/v1 (UsuarioController de Brayan), mientras P2 no aporte Basic Auth
 * ni AuthContext.
 *
 * NOTA PARA EL EQUIPO: el Admin del proyecto actual guarda la contrasena en
 * texto plano en memoria (seed "admin123"), y la guia pide hash Argon2.
 * validarContrasena() acepta texto plano (datos legacy) y hash Argon2, de modo
 * que los PATCH que cambian la contrasena ya quedan hasheados y los nuevos
 * admins creados con hash sigan autenticando. Cuando P1 entregue a Adam con
 * password hasheado esto sigue funcionando sin cambios.
 */
@Service
public class AdminPerfilService {

    /** Adam: admin con id 1 (constante de la guia ADAM_ID = 1L). */
    public static final long ADAM_ID = 1L;

    private final AdminRepository adminRepository;

    public AdminPerfilService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /** Autentica por usuario o correo + contrasena. Optional vacio = credenciales invalidas. */
    public Optional<Admin> autenticar(String usuarioOCorreo, String password) {
        if (usuarioOCorreo == null || usuarioOCorreo.isBlank() || password == null) {
            return Optional.empty();
        }
        return adminRepository.buscarPorUsuarioOCorreo(usuarioOCorreo.trim())
                .filter(admin -> validarContrasena(admin.getContrasena(), password));
    }

    public List<Admin> obtenerTodos() {
        return adminRepository.listarTodos();
    }

    public boolean correoEnUsoPorOtroAdmin(String correo, long idActual) {
        return adminRepository.listarTodos().stream()
                .anyMatch(a -> a.getId() != idActual
                        && a.getCorreo() != null
                        && a.getCorreo().equalsIgnoreCase(correo));
    }

    public Admin guardar(Admin admin) {
        return adminRepository.guardar(admin);
    }

    public PerfilAdminResponse aRespuesta(Admin admin) {
        return new PerfilAdminResponse(
                (long) admin.getId(),
                admin.getUsuario(),
                admin.getNombre(),
                admin.getApellido(),
                admin.getCorreo(),
                admin.getCargo());
    }

    private boolean validarContrasena(String contrasenaGuardada, String password) {
        if (contrasenaGuardada == null || password == null) {
            return false;
        }
        if (contrasenaGuardada.startsWith("$argon2")) {
            return Boolean.TRUE.equals(EncryptPassword.test(contrasenaGuardada, password));
        }
        // Datos legacy en memoria sin hashear (admin123 del seed).
        return contrasenaGuardada.equals(password);
    }
}
