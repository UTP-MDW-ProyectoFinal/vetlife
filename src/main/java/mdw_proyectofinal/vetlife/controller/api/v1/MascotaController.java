package mdw_proyectofinal.vetlife.controller.api.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import mdw_proyectofinal.vetlife.dto.*;
import mdw_proyectofinal.vetlife.service.api.v1.MascotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario/mascotas")
@RequiredArgsConstructor
@Validated
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<MascotaResponse>> listar(
            @RequestAttribute("usuarioId") Long usuarioId,
            @RequestParam(required = false) Integer especieId) {
        return ResponseEntity.ok(mascotaService.listar(usuarioId, especieId));
    }

    @PostMapping
    public ResponseEntity<MascotaResponse> crear(
            @RequestAttribute("usuarioId") Long usuarioId,
            @Valid @RequestBody CrearMascotaRequest request) {
        MascotaResponse response = mascotaService.crear(usuarioId, request);
        return ResponseEntity.created(URI.create("/api/v1/usuario/mascotas/" + response.id())).body(response);
    }

    @GetMapping("/{idMascota}")
    public ResponseEntity<MascotaResponse> obtener(
            @RequestAttribute("usuarioId") Long usuarioId,
            @PathVariable @Positive Long idMascota) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(idMascota, usuarioId));
    }

    @PatchMapping("/{idMascota}")
    public ResponseEntity<MascotaResponse> actualizar(
            @RequestAttribute("usuarioId") Long usuarioId,
            @PathVariable @Positive Long idMascota,
            @Valid @RequestBody ActualizarMascotaRequest request) {
        return ResponseEntity.ok(mascotaService.actualizar(idMascota, usuarioId, request));
    }
}