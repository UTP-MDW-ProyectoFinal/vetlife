package mdw_proyectofinal.vetlife.dto;

import java.time.LocalDateTime;

/** Cita vista por el admin, con su mascota y su cliente (P5). */
public record CitaAdminResponse(Long id, LocalDateTime fechaHora, String motivo, String estado,
        MascotaResumen mascota, ClienteResumen cliente) {

    public record MascotaResumen(Long id, String nombre, String especie) {
    }

    /** Datos del cliente dueno de la mascota; nunca incluye password. */
    public record ClienteResumen(Long id, String nombre, String correo) {
    }
}
