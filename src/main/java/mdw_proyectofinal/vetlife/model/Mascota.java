package mdw_proyectofinal.vetlife.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mdw_proyectofinal.vetlife.model.api.v1.Especie;
import mdw_proyectofinal.vetlife.model.api.v1.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mascota", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "nombre", "especie_id"})
})
@Getter
@Setter
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especie_id", nullable = false)
    private Especie especie;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 50)
    private String raza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private Sexo sexo;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "peso_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoKg;
}