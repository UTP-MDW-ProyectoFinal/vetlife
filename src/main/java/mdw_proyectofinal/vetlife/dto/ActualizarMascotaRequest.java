package mdw_proyectofinal.vetlife.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import mdw_proyectofinal.vetlife.model.api.v1.Sexo;

public record ActualizarMascotaRequest(
    @Size(min = 2, max = 50) String nombre,
    @Positive Integer especieId,
    @Size(max = 50) String raza,
    Sexo sexo,
    @PastOrPresent LocalDate fechaNacimiento,
    @Positive @DecimalMax("200") BigDecimal pesoKg
) {}