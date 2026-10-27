package mdw_proyectofinal.vetlife.controller.api.v1;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Positive;
import mdw_proyectofinal.vetlife.service.api.v1.AdminDoctorConsultaService;
import mdw_proyectofinal.vetlife.service.api.v1.AdminPerfilService;

/**
 * Consultas de solo lectura de admin sobre un doctor (P5):
 * detalle, citas, una cita y pacientes. Cualquier admin autenticado puede usarlas
 * (ninguna es solo-Adam). Un cliente o una peticion sin credenciales recibe 401.
 */
@RestController
@Validated
@RequestMapping(ApiVersion.BASE_PATH + "/admin/doctores")
public class AdminDoctorConsultaController {

    private static final String MSG_NO_AUTENTICADO = "Credenciales ausentes o incorrectas";
    private static final String MSG_DOCTOR_NO_ENCONTRADO = "Doctor no encontrado";
    private static final String MSG_CITA_NO_ENCONTRADA = "Cita no encontrada para el doctor indicado";

    private final AdminDoctorConsultaService consultaService;
    private final AdminPerfilService adminPerfilService;

    public AdminDoctorConsultaController(AdminDoctorConsultaService consultaService,
            AdminPerfilService adminPerfilService) {
        this.consultaService = consultaService;
        this.adminPerfilService = adminPerfilService;
    }

    /** GET /api/v1/admin/doctores/{idDoctor} */
    @GetMapping("/{idDoctor}")
    public ResponseEntity<?> getDoctor(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable @Positive(message = "El id del doctor debe ser un número positivo") Long idDoctor) {

        if (!autenticado(email, password)) {
            return error(HttpStatus.UNAUTHORIZED, MSG_NO_AUTENTICADO);
        }
        return consultaService.obtenerDetalle(idDoctor)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, MSG_DOCTOR_NO_ENCONTRADO));
    }

    /** GET /api/v1/admin/doctores/{idDoctor}/citas?estado=&fecha=2026-11-10 */
    @GetMapping("/{idDoctor}/citas")
    public ResponseEntity<?> getCitas(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable @Positive(message = "El id del doctor debe ser un número positivo") Long idDoctor,
            @RequestParam(value = "estado", required = false) String estadoParam,
            @RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        if (!autenticado(email, password)) {
            return error(HttpStatus.UNAUTHORIZED, MSG_NO_AUTENTICADO);
        }
        if (!consultaService.doctorExiste(idDoctor)) {
            return error(HttpStatus.NOT_FOUND, MSG_DOCTOR_NO_ENCONTRADO);
        }

        String estado = null;
        if (estadoParam != null && !estadoParam.isBlank()) {
            estado = estadoParam.trim().toUpperCase();
            if (!consultaService.estadoExiste(estado)) {
                return error(HttpStatus.BAD_REQUEST, "El estado '" + estadoParam.trim() + "' no existe");
            }
        }

        return ResponseEntity.ok(consultaService.listarCitas(idDoctor, estado, fecha));
    }

    /** GET /api/v1/admin/doctores/{idDoctor}/citas/{idCita} */
    @GetMapping("/{idDoctor}/citas/{idCita}")
    public ResponseEntity<?> getCita(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable @Positive(message = "El id del doctor debe ser un número positivo") Long idDoctor,
            @PathVariable @Positive(message = "El id de la cita debe ser un número positivo") Long idCita) {

        if (!autenticado(email, password)) {
            return error(HttpStatus.UNAUTHORIZED, MSG_NO_AUTENTICADO);
        }
        // Mismo mensaje si el doctor no existe, la cita no existe o es de otro doctor.
        return consultaService.obtenerCita(idDoctor, idCita)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, MSG_CITA_NO_ENCONTRADA));
    }

    /** GET /api/v1/admin/doctores/{idDoctor}/pacientes */
    @GetMapping("/{idDoctor}/pacientes")
    public ResponseEntity<?> getPacientes(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable @Positive(message = "El id del doctor debe ser un número positivo") Long idDoctor) {

        if (!autenticado(email, password)) {
            return error(HttpStatus.UNAUTHORIZED, MSG_NO_AUTENTICADO);
        }
        if (!consultaService.doctorExiste(idDoctor)) {
            return error(HttpStatus.NOT_FOUND, MSG_DOCTOR_NO_ENCONTRADO);
        }
        return ResponseEntity.ok(consultaService.listarPacientes(idDoctor));
    }

    // ---- Manejo de errores propio de este controller (400) ----

    /** @Positive en las variables de ruta: devuelve 400 con la lista de campos inválidos. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> detalles = ex.getConstraintViolations().stream()
                .map(v -> {
                    String ruta = v.getPropertyPath().toString();
                    String campo = ruta.substring(ruta.lastIndexOf('.') + 1);
                    return Map.of("campo", campo, "mensaje", v.getMessage());
                })
                .toList();
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "mensaje", "Datos inválidos",
                "detalles", detalles));
    }

    /** Id que no es número o fecha que no es AAAA-MM-DD. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String mensaje = "fecha".equals(ex.getName())
                ? "La fecha debe tener el formato AAAA-MM-DD"
                : "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        return error(HttpStatus.BAD_REQUEST, mensaje);
    }

    private boolean autenticado(String email, String password) {
        return adminPerfilService.autenticar(email, password).isPresent();
    }

    private static ResponseEntity<Map<String, Object>> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "mensaje", mensaje));
    }
}
