package mdw_proyectofinal.vetlife.model.api.v1;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Catalogo de especialidades medicas (P6). Tabla especialidad.
 * Semillas: Medicina general, Cirugia, Dermatologia, Cardiologia (ids 1 a 4).
 */
@Entity
@Table(name = "especialidad", uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = "nombre"))
public class Especialidad {

    /** Id Integer por ser tabla de catalogo (regla del equipo, seccion 5.2 de la guia). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    public Especialidad() {
    }

    public Especialidad(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
