package mdw_proyectofinal.vetlife.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ActualizarCitaRequest {

    @Future(message = "La fecha de la cita debe estar en el futuro")
    private LocalDateTime fechaHora;

    @Size(min = 1, max = 300, message = "El motivo debe tener entre 1 y 300 caracteres")
    private String motivo;

    @Pattern(regexp = "CANCELADA", message = "Solo se permite cancelar la cita")
    private String estado;

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
