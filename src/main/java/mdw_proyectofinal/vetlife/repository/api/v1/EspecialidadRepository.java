package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mdw_proyectofinal.vetlife.model.api.v1.Especialidad;

/**
 * Repositorio JPA del catalogo de especialidades (P6).
 * Contrato de la guia (seccion 5.3): findAll y findById.
 */
public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {

  Optional<Especialidad> findByNombre(String nombre);

}
