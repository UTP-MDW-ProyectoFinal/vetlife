package mdw_proyectofinal.vetlife.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import mdw_proyectofinal.vetlife.model.api.v1.Sexo;

public record MascotaResponse(
    Long id,
    String nombre,
    EspecieDTO especie,
    String raza,
    Sexo sexo,
    LocalDate fechaNacimiento,
    int edadAnios,
    BigDecimal pesoKg
) {
    public record EspecieDTO(Integer id, String nombre) {}
}