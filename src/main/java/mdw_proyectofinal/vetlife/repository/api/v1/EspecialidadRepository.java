package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mdw_proyectofinal.vetlife.model.api.v1.Especialidad;

/** Acceso JPA al catalogo de especialidades (P6). */
public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {

  Optional<Especialidad> findByNombre(String nombre);

}
