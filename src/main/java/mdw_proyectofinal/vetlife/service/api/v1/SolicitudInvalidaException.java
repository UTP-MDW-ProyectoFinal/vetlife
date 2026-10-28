package mdw_proyectofinal.vetlife.service.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Dominio: la solicitud no cumple la regla de negocio (ej. no envio ningun campo).
 * Spring la mapea a HTTP 400.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SolicitudInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SolicitudInvalidaException(String message) {
        super(message);
    }
}
