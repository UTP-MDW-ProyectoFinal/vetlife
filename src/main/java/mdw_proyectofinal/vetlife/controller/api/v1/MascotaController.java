package mdw_proyectofinal.vetlife.controller.api.v1;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import mdw_proyectofinal.vetlife.dto.ActualizarMascotaRequest;
import mdw_proyectofinal.vetlife.dto.CrearMascotaRequest;
import mdw_proyectofinal.vetlife.dto.MascotaResponse;
import mdw_proyectofinal.vetlife.model.api.v1.Usuario;
import mdw_proyectofinal.vetlife.service.api.v1.MascotaService;
import mdw_proyectofinal.vetlife.service.api.v1.UsuarioService;
import mdw_proyectofinal.vetlife.utils.EncryptPassword;

/**
 * GET/POST/PATCH /api/v1/usuario/mascotas: coleccion de mascotas del usuario
 * autenticado (headers email/password, estilo de api/v1).
 */
@RestController
@RequestMapping(ApiVersion.BASE_PATH + "/usuario/mascotas")
@Validated
public class MascotaController {

    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

    public MascotaController(MascotaService mascotaService, UsuarioService usuarioService) {
        this.mascotaService = mascotaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @RequestParam(required = false) Integer especieId) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }
        return ResponseEntity.ok(mascotaService.listar(usuario.getId(), especieId));
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @Valid @RequestBody CrearMascotaRequest request) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }
        MascotaResponse response = mascotaService.crear(usuario.getId(), request);
        return ResponseEntity.created(URI.create(ApiVersion.BASE_PATH + "/usuario/mascotas/" + response.id()))
                .body(response);
    }

    @GetMapping("/{idMascota}")
    public ResponseEntity<?> obtener(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable @Positive Long idMascota) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }
        return ResponseEntity.ok(mascotaService.obtenerPorId(idMascota, usuario.getId()));
    }

    @PatchMapping("/{idMascota}")
    public ResponseEntity<?> actualizar(
            @RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable @Positive Long idMascota,
            @Valid @RequestBody ActualizarMascotaRequest request) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }
        return ResponseEntity.ok(mascotaService.actualizar(idMascota, usuario.getId(), request));
    }

    private Usuario autenticar(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        Usuario usuario = usuarioService.getUsuario(email.trim());
        if (usuario == null) {
            return null;
        }

        String passwordHash = usuarioService.getPassword(email.trim());
        if (!EncryptPassword.test(passwordHash, password)) {
            return null;
        }
        return usuario;
    }
}
