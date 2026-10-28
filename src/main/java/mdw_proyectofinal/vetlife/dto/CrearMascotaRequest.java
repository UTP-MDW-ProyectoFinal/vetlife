package mdw_proyectofinal.vetlife.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import mdw_proyectofinal.vetlife.model.api.v1.Sexo;

public record CrearMascotaRequest(
    @NotBlank @Size(min = 2, max = 50) String nombre,
    @NotNull @Positive Integer especieId,
    @Size(max = 50) String raza,
    @NotNull Sexo sexo,
    @NotNull @PastOrPresent LocalDate fechaNacimiento,
    @NotNull @Positive @DecimalMax("200") BigDecimal pesoKg
) {}