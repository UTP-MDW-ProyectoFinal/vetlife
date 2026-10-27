package mdw_proyectofinal.vetlife.dto;

import java.time.LocalDateTime;

/**
 * Paciente de un doctor: mascota con citas no canceladas con ese doctor (P5).
 * ultimaVisita es null si no tiene citas COMPLETADA.
 */
public record PacienteResponse(Long mascotaId, String nombre, String especie, String raza,
        Duenio duenio, long totalCitas, LocalDateTime ultimaVisita) {

    public record Duenio(Long id, String nombre, String correo) {
    }
}
