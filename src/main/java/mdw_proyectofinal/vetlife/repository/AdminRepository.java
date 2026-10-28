package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Admin;

@Repository
public class AdminRepository {

    private final List<Admin> admins = new ArrayList<>();
    private int siguienteId = 1;

    public AdminRepository() {
        guardar(new Admin(0, "Dra.", "Ramírez", "admin", "admin@vetlife.com", "admin123", "Administrador"));
    }

    public Admin guardar(Admin admin) {
        if (admin.getId() == 0) {
            admin.setId(siguienteId++);
        }
        // remove if exists with same id
        admins.removeIf(a -> a.getId() == admin.getId());
        admins.add(admin);
        return admin;
    }

    public Admin save(Admin admin) {
        return guardar(admin);
    }

    public List<Admin> listarTodos() {
        return List.copyOf(admins);
    }

    public long count() {
        return admins.size();
    }

    public Optional<Admin> buscarPorId(int id) {
        return admins.stream().filter(a -> a.getId() == id).findFirst();
    }

    public Admin findById(Long id) {
        return admins.stream().filter(a -> a.getId() == id.intValue()).findFirst().orElse(null);
    }

    public Admin findByUsuario(String usuario) {
        return admins.stream().filter(a -> a.getUsuario() != null && a.getUsuario().equalsIgnoreCase(usuario))
                .findFirst().orElse(null);
    }

    public boolean existsByUsername(String username) {
        return admins.stream().anyMatch(a -> a.getUsuario() != null && a.getUsuario().equalsIgnoreCase(username));
    }

    public boolean existsByEmail(String email) {
        return admins.stream().anyMatch(a -> a.getCorreo() != null && a.getCorreo().equalsIgnoreCase(email));
    }

    public Optional<Admin> buscarPorUsuarioOCorreo(String usuarioOCorreo) {
        return admins.stream()
                .filter(a -> a.getUsuario().equalsIgnoreCase(usuarioOCorreo)
                        || a.getCorreo().equalsIgnoreCase(usuarioOCorreo))
                .findFirst();
    }
}
