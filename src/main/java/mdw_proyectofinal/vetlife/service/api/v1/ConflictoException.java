package mdw_proyectofinal.vetlife.service.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Dominio: conflicto con el estado actual del recurso (duplicados, etc.).
 * Spring la mapea a HTTP 409.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConflictoException(String message) {
        super(message);
    }
}
