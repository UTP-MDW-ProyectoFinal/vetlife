package mdw_proyectofinal.vetlife.dto;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        Long mascotaId,
        String mascotaNombre,
        Long doctorId,
        String doctorNombre,
        LocalDateTime fechaHora,
        String motivo,
        String estado) {
}
