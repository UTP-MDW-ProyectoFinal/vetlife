package mdw_proyectofinal.vetlife.repository.api.v1;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import mdw_proyectofinal.vetlife.model.api.v1.Cita;
import mdw_proyectofinal.vetlife.model.api.v1.Mascota;

/**
 * Consultas de solo lectura sobre citas para los endpoints de admin (P5).
 * Los JOIN FETCH cargan mascota, especie, dueno y estado en una sola sentencia SQL,
 * evitando el problema de N+1 consultas.
 */
public interface ConsultaCitaRepository extends Repository<Cita, Long> {

    /** Todas las citas del doctor. */
    long countByDoctorId(Long doctorId);

    /** Citas del doctor en un estado dado (por ejemplo PENDIENTE). */
    long countByDoctorIdAndEstadoNombre(Long doctorId, String estado);

    /** Citas del doctor por fecha ascendente; estado y rango de fechas opcionales (null = sin filtro). */
    @Query("""
            SELECT c FROM Cita c
            JOIN FETCH c.estado e
            JOIN FETCH c.mascota m
            JOIN FETCH m.especie
            JOIN FETCH m.usuario
            WHERE c.doctor.id = :doctorId
              AND (:estado IS NULL OR e.nombre = :estado)
              AND (:desde IS NULL OR c.fechaHora >= :desde)
              AND (:hasta IS NULL OR c.fechaHora < :hasta)
            ORDER BY c.fechaHora ASC, c.id ASC
            """)
    List<Cita> buscarCitasDeDoctor(@Param("doctorId") Long doctorId,
            @Param("estado") String estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    /** Una cita, solo si es de ese doctor. Si es de otro doctor no devuelve nada. */
    @Query("""
            SELECT c FROM Cita c
            JOIN FETCH c.estado
            JOIN FETCH c.mascota m
            JOIN FETCH m.especie
            JOIN FETCH m.usuario
            WHERE c.id = :citaId AND c.doctor.id = :doctorId
            """)
    Optional<Cita> buscarCitaDeDoctor(@Param("doctorId") Long doctorId, @Param("citaId") Long citaId);

    /**
     * Agrupa por mascota las citas NO canceladas del doctor. Cada fila es
     * [mascotaId, totalCitas, ultimaVisita], donde ultimaVisita es el MAX de fechaHora
     * solo entre las COMPLETADA (null si no tiene).
     */
    @Query("""
            SELECT m.id, COUNT(c),
                   MAX(CASE WHEN e.nombre = 'COMPLETADA' THEN c.fechaHora ELSE NULL END)
            FROM Cita c
            JOIN c.mascota m
            JOIN c.estado e
            WHERE c.doctor.id = :doctorId
              AND e.nombre <> 'CANCELADA'
            GROUP BY m.id
            """)
    List<Object[]> agruparPacientesDeDoctor(@Param("doctorId") Long doctorId);

    /** Mascotas con su especie y su dueno cargados en una sola consulta, ordenadas por nombre. */
    @Query("""
            SELECT m FROM Mascota m
            JOIN FETCH m.especie
            JOIN FETCH m.usuario
            WHERE m.id IN :ids
            ORDER BY m.nombre ASC, m.id ASC
            """)
    List<Mascota> buscarMascotasConEspecieYDuenio(@Param("ids") Collection<Long> ids);
}
