package mdw_proyectofinal.vetlife.dto;

/**
 * Respuesta del perfil de admin (GET y PATCH /api/v1/admin/perfil, P6).
 * Es el equivalente al AdminResponse de la guia: nunca expone password ni hash.
 * Incluye apellido y cargo porque el Admin del proyecto actual los tiene.
 */
public record PerfilAdminResponse(Long id, String usuario, String nombre, String apellido,
        String correo, String cargo) {
}
