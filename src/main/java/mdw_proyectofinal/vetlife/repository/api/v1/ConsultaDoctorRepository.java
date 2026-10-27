package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import mdw_proyectofinal.vetlife.model.api.v1.Doctor;

/**
 * Lectura de doctores para P5. Extiende Repository (no JpaRepository): solo expone
 * lo declarado aqui, asi que no hay forma de escribir ni borrar datos por accidente.
 */
public interface ConsultaDoctorRepository extends Repository<Doctor, Long> {

    Optional<Doctor> findById(Long id);

    boolean existsById(Long id);
}
