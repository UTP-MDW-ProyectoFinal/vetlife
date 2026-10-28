package mdw_proyectofinal.vetlife.service.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Dominio: el recurso solicitado no existe (o no pertenece al usuario).
 * Spring la mapea a HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
