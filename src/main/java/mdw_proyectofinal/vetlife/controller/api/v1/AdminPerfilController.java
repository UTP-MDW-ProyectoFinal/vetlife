package mdw_proyectofinal.vetlife.controller.api.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mdw_proyectofinal.vetlife.dto.ActualizarAdminDTO;
import mdw_proyectofinal.vetlife.dto.PerfilAdminResponse;
import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.service.api.v1.AdminPerfilService;
import mdw_proyectofinal.vetlife.utils.EncryptPassword;

/** GET y PATCH /api/v1/admin/perfil: cada admin opera sobre su propio perfil (P6). */
@RestController
@RequestMapping(ApiVersion.BASE_PATH + "/admin")
public class AdminPerfilController {

    private final AdminPerfilService adminPerfilService;

    public AdminPerfilController(AdminPerfilService adminPerfilService) {
        this.adminPerfilService = adminPerfilService;
    }

    @GetMapping("perfil")
    public ResponseEntity<?> getPerfil(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password) {
        Optional<Admin> admin = adminPerfilService.autenticar(email, password);
        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales ausentes o incorrectas");
        }
        return ResponseEntity.ok(adminPerfilService.aRespuesta(admin.get()));
    }

    @PatchMapping("perfil")
    public ResponseEntity<?> patchPerfil(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @Valid @RequestBody ActualizarAdminDTO dto,
            BindingResult result) {

        Optional<Admin> admin = adminPerfilService.autenticar(email, password);
        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales ausentes o incorrectas");
        }

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(erroresPorCampo(result));
        }

        if (dto.vacio()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "Debe enviar al menos un campo para actualizar"));
        }

        Admin perfil = admin.get();

        if (dto.getCorreo() != null) {
            String correoNuevo = dto.getCorreo().trim().toLowerCase();
            if (adminPerfilService.correoEnUsoPorOtroAdmin(correoNuevo, perfil.getId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El correo ya esta en uso por otro admin");
            }
            perfil.setCorreo(correoNuevo);
        }

        if (dto.getNombre() != null) {
            perfil.setNombre(dto.getNombre().trim());
        }
        if (dto.getApellido() != null) {
            perfil.setApellido(dto.getApellido().trim());
        }
        if (dto.getCargo() != null) {
            perfil.setCargo(dto.getCargo().trim());
        }
        if (dto.getPassword() != null) {
            perfil.setContrasena(EncryptPassword.encrypt(dto.getPassword()));
        }

        adminPerfilService.guardar(perfil);
        return ResponseEntity.ok(adminPerfilService.aRespuesta(perfil));
    }

    private Map<String, List<String>> erroresPorCampo(BindingResult result) {
        Map<String, List<String>> errores = new HashMap<>();
        result.getFieldErrors().forEach(error -> errores
                .computeIfAbsent(error.getField(), campo -> new ArrayList<>())
                .add(error.getDefaultMessage()));
        return errores;
    }
}
