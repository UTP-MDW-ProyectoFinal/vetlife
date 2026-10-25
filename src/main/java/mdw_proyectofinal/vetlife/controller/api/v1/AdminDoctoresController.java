package mdw_proyectofinal.vetlife.controller.api.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mdw_proyectofinal.vetlife.dto.DoctorRegistroDTO;
import mdw_proyectofinal.vetlife.dto.DoctorResponse;
import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.model.api.v1.Doctor;
import mdw_proyectofinal.vetlife.model.api.v1.Especialidad;
import mdw_proyectofinal.vetlife.service.api.v1.AdminPerfilService;
import mdw_proyectofinal.vetlife.service.api.v1.DoctorService;

/** GET y POST /api/v1/admin/doctores (P6). El POST solo lo permite el admin con id 1. */
@RestController
@RequestMapping(ApiVersion.BASE_PATH + "/admin")
public class AdminDoctoresController {

    private final DoctorService doctorService;
    private final AdminPerfilService adminPerfilService;

    public AdminDoctoresController(DoctorService doctorService, AdminPerfilService adminPerfilService) {
        this.doctorService = doctorService;
        this.adminPerfilService = adminPerfilService;
    }

    @GetMapping("doctores")
    public ResponseEntity<?> getDoctores(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @RequestParam(value = "especialidadId", required = false) Integer especialidadId,
            @RequestParam(value = "activo", required = false) String activoParam) {

        Optional<Admin> admin = adminPerfilService.autenticar(email, password);
        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales ausentes o incorrectas");
        }

        Boolean activo = null;
        if (activoParam != null && !activoParam.isBlank()) {
            if ("true".equalsIgnoreCase(activoParam)) {
                activo = true;
            } else if ("false".equalsIgnoreCase(activoParam)) {
                activo = false;
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El filtro activo solo acepta true o false");
            }
        }

        if (especialidadId != null) {
            if (especialidadId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El filtro especialidadId debe ser un id positivo");
            }
            if (doctorService.buscarEspecialidad(especialidadId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("La especialidad " + especialidadId + " no existe");
            }
        }

        return ResponseEntity.ok(doctorService.listar(especialidadId, activo));
    }

    @PostMapping("doctores")
    public ResponseEntity<?> postDoctor(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @Valid @RequestBody DoctorRegistroDTO dto,
            BindingResult result) {

        Optional<Admin> admin = adminPerfilService.autenticar(email, password);
        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales ausentes o incorrectas");
        }

        if (admin.get().getId() != AdminPerfilService.ADAM_ID) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo el admin principal (id 1) puede registrar doctores");
        }

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(erroresPorCampo(result));
        }

        Optional<Especialidad> especialidad = doctorService.buscarEspecialidad(dto.getEspecialidadId());
        if (especialidad.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La especialidad " + dto.getEspecialidadId() + " no existe");
        }

        String emailNormalizado = dto.getEmail().trim().toLowerCase();
        if (doctorService.emailRegistrado(emailNormalizado)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya esta registrado por otro doctor");
        }
        if (doctorService.licenciaRegistrada(dto.getNumeroLicencia().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El numero de licencia ya esta registrado por otro doctor");
        }

        Doctor doctor = doctorService.construirDoctor(dto, especialidad.get());
        DoctorResponse respuesta = doctorService.guardar(doctor);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, ApiVersion.BASE_PATH + "/admin/doctores/" + respuesta.id())
                .body(respuesta);
    }

    private Map<String, List<String>> erroresPorCampo(BindingResult result) {
        Map<String, List<String>> errores = new HashMap<>();
        result.getFieldErrors().forEach(error -> errores
                .computeIfAbsent(error.getField(), campo -> new ArrayList<>())
                .add(error.getDefaultMessage()));
        return errores;
    }
}
