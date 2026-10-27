package mdw_proyectofinal.vetlife.repository.api.v1;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mdw_proyectofinal.vetlife.model.api.v1.Mascota;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByUsuarioId(Long usuarioId);

    Optional<Mascota> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndNombreIgnoreCaseAndEspecieId(Long usuarioId, String nombre, Integer especieId);
}
