package mdw_proyectofinal.vetlife.service.api.v1;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mdw_proyectofinal.vetlife.dto.PerfilAdminResponse;
import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.repository.AdminRepository;
import mdw_proyectofinal.vetlife.utils.EncryptPassword;

/** Autenticacion y perfil de los admins (P6). */
@Service
public class AdminPerfilService {

    public static final long ADAM_ID = 1L;

    private final AdminRepository adminRepository;

    public AdminPerfilService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

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
        return contrasenaGuardada.equals(password);
    }
}
