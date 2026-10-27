package mdw_proyectofinal.vetlife.dto;

/** GET /api/v1/admin/doctores/{idDoctor}: ficha del doctor con conteo de citas (P5). */
public record DoctorDetalleResponse(Long id, String nombre, String apellido, String especialidad,
        String email, String telefono, String numeroLicencia, boolean activo,
        long totalCitas, long citasPendientes) {
}
