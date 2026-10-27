package mdw_proyectofinal.vetlife.controller.api.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import mdw_proyectofinal.vetlife.dto.ActualizarCitaRequest;
import mdw_proyectofinal.vetlife.dto.CitaResponse;
import mdw_proyectofinal.vetlife.dto.CrearCitaRequest;
import mdw_proyectofinal.vetlife.model.api.v1.Usuario;
import mdw_proyectofinal.vetlife.service.api.v1.CitaService;
import mdw_proyectofinal.vetlife.service.api.v1.UsuarioService;
import mdw_proyectofinal.vetlife.utils.EncryptPassword;

@RestController
@RequestMapping(ApiVersion.BASE_PATH + "/usuario")
public class CitaController {

    private final CitaService citaService;
    private final UsuarioService usuarioService;

    public CitaController(CitaService citaService, UsuarioService usuarioService) {
        this.citaService = citaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/citas")
    public ResponseEntity<?> listar(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @RequestParam(value = "estado", required = false) String estado) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }

        try {
            return ResponseEntity.ok(citaService.listarPorUsuario(Long.valueOf(usuario.getId()), estado));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @PostMapping("/citas")
    public ResponseEntity<?> crear(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @Valid @RequestBody CrearCitaRequest request,
            BindingResult result) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(erroresPorCampo(result));
        }

        try {
            CitaResponse creada = citaService.crear(Long.valueOf(usuario.getId()), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.LOCATION, ApiVersion.BASE_PATH + "/usuario/citas/" + creada.id())
                    .body(creada);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @GetMapping("/citas/{idCita}")
    public ResponseEntity<?> buscar(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable Long idCita) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }
        try {
            return ResponseEntity.ok(citaService.buscarPorId(Long.valueOf(usuario.getId()), idCita));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @PatchMapping("/citas/{idCita}")
    public ResponseEntity<?> actualizar(@RequestHeader(value = "email", required = false) String email,
            @RequestHeader(value = "password", required = false) String password,
            @PathVariable Long idCita,
            @Valid @RequestBody ActualizarCitaRequest request,
            BindingResult result) {
        Usuario usuario = autenticar(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales ausentes o incorrectas");
        }
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(erroresPorCampo(result));
        }

        try {
            return ResponseEntity.ok(citaService.actualizar(Long.valueOf(usuario.getId()), idCita, request));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
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

    private Map<String, List<String>> erroresPorCampo(BindingResult result) {
        Map<String, List<String>> errores = new HashMap<>();
        result.getFieldErrors().forEach(error -> errores
                .computeIfAbsent(error.getField(), campo -> new ArrayList<>())
                .add(error.getDefaultMessage()));
        return errores;
    }
}
