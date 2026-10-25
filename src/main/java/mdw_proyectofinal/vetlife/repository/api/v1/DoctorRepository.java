package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mdw_proyectofinal.vetlife.model.api.v1.Doctor;

/** Acceso JPA a los doctores (P6). */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

  boolean existsByEmail(String email);

  boolean existsByNumeroLicencia(String numeroLicencia);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByNumeroLicenciaAndIdNot(String numeroLicencia, Long id);

  @Query("""
      SELECT d FROM Doctor d
      WHERE (:especialidadId IS NULL OR d.especialidad.id = :especialidadId)
        AND (:activo IS NULL OR d.activo = :activo)
      ORDER BY d.apellido ASC, d.nombre ASC
      """)
  List<Doctor> buscarConFiltros(@Param("especialidadId") Integer especialidadId,
      @Param("activo") Boolean activo);

}
