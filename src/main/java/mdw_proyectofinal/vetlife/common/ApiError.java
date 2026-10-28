package mdw_proyectofinal.vetlife.common;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        List<CampoError> detalles
) {
    public ApiError(int status, String error, String mensaje, String path, List<CampoError> detalles) {
        this(Instant.now(), status, error, mensaje, path, detalles == null ? List.of() : detalles);
    }

    public ApiError(int status, String error, String mensaje, String path) {
        this(Instant.now(), status, error, mensaje, path, List.of());
    }
}
