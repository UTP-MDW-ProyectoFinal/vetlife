package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Usuario;

@Repository("usuarioRepositoryMemoria")
public class UsuarioRepository {

    private final List<Usuario> usuarios = new ArrayList<>();
    private int siguienteId = 1;

    public UsuarioRepository() {
        guardar(new Usuario(0, "María", "González", "12345678", "+51 900 111 223",
                "maria.gonzalez@vetlife.com", "123456"));
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == 0) {
            usuario.setId(siguienteId++);
        }
        usuarios.add(usuario);
        return usuario;
    }

    public List<Usuario> listarTodos() {
        return List.copyOf(usuarios);
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarios.stream().filter(u -> u.getId() == id).findFirst();
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    public Optional<Usuario> buscarPorDni(String dni) {
        return usuarios.stream()
                .filter(u -> u.getDni().equals(dni))
                .findFirst();
    }

    public boolean existeCorreo(String correo) {
        return buscarPorCorreo(correo).isPresent();
    }

    public boolean existeDni(String dni) {
        return buscarPorDni(dni).isPresent();
    }

    public Optional<Usuario> actualizar(int id, String nombre, String apellido, String dni, String telefono,
            String correo) {
        return buscarPorId(id).map(usuario -> {
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDni(dni);
            usuario.setTelefono(telefono);
            usuario.setCorreo(correo);
            return usuario;
        });
    }
}
