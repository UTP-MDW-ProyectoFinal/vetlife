package mdw_proyectofinal.vetlife.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNoEncontradaException extends RuntimeException {
    public RecursoNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
