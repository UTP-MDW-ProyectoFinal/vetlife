package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mdw_proyectofinal.vetlife.model.api.v1.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);

    default Usuario findByEmail(String email) {
        return findByCorreo(email).orElse(null);
    }

    default Usuario findByNombre(String nombre) {
        return null;
    }
}
