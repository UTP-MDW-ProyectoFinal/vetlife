package mdw_proyectofinal.vetlife.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<CampoError> detalles = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            detalles.add(new CampoError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Datos inválidos",
                request.getRequestURI(), detalles);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex,
            HttpServletRequest request) {
        List<CampoError> detalles = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String campo = violation.getPropertyPath().toString();
            detalles.add(new CampoError(campo, violation.getMessage()));
        }
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Datos inválidos",
                request.getRequestURI(), detalles);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Datos inválidos",
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Datos inválidos",
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ApiError> handleSolicitudInvalidaException(SolicitudInvalidaException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<ApiError> handleNoAutenticadoException(NoAutenticadoException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ApiError> handleAccesoDenegadoException(AccesoDenegadoException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RecursoNoEncontradaException.class)
    public ResponseEntity<ApiError> handleRecursoNoEncontradaException(RecursoNoEncontradaException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ApiError> handleConflictoException(ConflictoException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> handleReglaNegocioException(ReglaNegocioException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unprocessable Entity", ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT.value(), "Conflict",
                "Conflicto de integridad de datos", request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "Error interno del servidor", request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
