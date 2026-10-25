package mdw_proyectofinal.vetlife.dto;

/** Respuesta de un doctor con su especialidad (id y nombre). */
public record DoctorResponse(Long id, Integer especialidadId, String especialidadNombre,
        String nombre, String apellido, String email, String telefono,
        String numeroLicencia, boolean activo) {
}
