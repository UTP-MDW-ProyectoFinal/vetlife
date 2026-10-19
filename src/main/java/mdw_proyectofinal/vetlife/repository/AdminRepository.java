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
        admins.add(admin);
        return admin;
    }

    public List<Admin> listarTodos() {
        return List.copyOf(admins);
    }

    public Optional<Admin> buscarPorId(int id) {
        return admins.stream().filter(a -> a.getId() == id).findFirst();
    }

    public Optional<Admin> buscarPorUsuarioOCorreo(String usuarioOCorreo) {
        return admins.stream()
                .filter(a -> a.getUsuario().equalsIgnoreCase(usuarioOCorreo)
                        || a.getCorreo().equalsIgnoreCase(usuarioOCorreo))
                .findFirst();
    }
}
