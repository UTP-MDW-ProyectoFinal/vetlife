package mdw_proyectofinal.vetlife.dto;

/** Respuesta del perfil de admin, sin password ni hash. */
public record PerfilAdminResponse(Long id, String usuario, String nombre, String apellido,
        String correo, String cargo) {
}
