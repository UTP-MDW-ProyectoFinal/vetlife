package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mdw_proyectofinal.vetlife.model.api.v1.EstadoCita;

public interface EstadoCitaRepository extends JpaRepository<EstadoCita, Integer> {

    Optional<EstadoCita> findByNombre(String nombre);
}
