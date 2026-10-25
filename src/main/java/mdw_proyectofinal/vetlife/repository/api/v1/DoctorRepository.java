package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mdw_proyectofinal.vetlife.model.api.v1.Doctor;

/**
 * Repositorio JPA de Doctor (P6).
 * Contrato de la guia (seccion 5.3): findById, findAll, existsByEmail,
 * existsByNumeroLicencia y una consulta con filtros opcionales de
 * especialidad y activo, ordenada por apellido y nombre.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

  boolean existsByEmail(String email);

  boolean existsByNumeroLicencia(String numeroLicencia);

  /** Para completar el chequeo de duplicados sin chocar consigo mismo. */
  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByNumeroLicenciaAndIdNot(String numeroLicencia, Long id);

  /**
   * Listado con filtros opcionales: especialidadId y activo.
   * Ordenado por apellido y nombre, como pide el GET /api/v1/admin/doctores.
   */
  @Query("""
      SELECT d FROM Doctor d
      WHERE (:especialidadId IS NULL OR d.especialidad.id = :especialidadId)
        AND (:activo IS NULL OR d.activo = :activo)
      ORDER BY d.apellido ASC, d.nombre ASC
      """)
  List<Doctor> buscarConFiltros(@Param("especialidadId") Integer especialidadId,
      @Param("activo") Boolean activo);

}
