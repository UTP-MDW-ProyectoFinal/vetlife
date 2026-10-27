package mdw_proyectofinal.vetlife.repository.api.v1;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mdw_proyectofinal.vetlife.model.api.v1.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Query("""
            SELECT c FROM Cita c
            JOIN c.mascota m
            JOIN m.usuario u
            WHERE u.id = :usuarioId
            ORDER BY c.fechaHora ASC
            """)
    List<Cita> findByMascotaUsuarioId(@Param("usuarioId") Long usuarioId, Sort sort);

    @Query("""
            SELECT c FROM Cita c
            JOIN c.mascota m
            JOIN m.usuario u
            WHERE c.id = :id AND u.id = :usuarioId
            """)
    Optional<Cita> findByIdAndMascotaUsuarioId(@Param("id") Long id, @Param("usuarioId") Long usuarioId);

    boolean existsByDoctorIdAndFechaHoraAndEstadoNombreNot(Long doctorId, LocalDateTime fechaHora, String estadoNombre);
}
