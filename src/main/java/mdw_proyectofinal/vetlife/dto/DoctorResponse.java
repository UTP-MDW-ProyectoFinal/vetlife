package mdw_proyectofinal.vetlife.dto;

/**
 * Respuesta de la coleccion de doctores (GET y POST /api/v1/admin/doctores, P6).
 * Equivalente al DoctorResponse de la guia: la especialidad viaja con id y nombre,
 * nunca contrasenas ni hashes (no aplica a doctores, por definicion de la guia).
 */
public record DoctorResponse(Long id, Integer especialidadId, String especialidadNombre,
        String nombre, String apellido, String email, String telefono,
        String numeroLicencia, boolean activo) {
}
