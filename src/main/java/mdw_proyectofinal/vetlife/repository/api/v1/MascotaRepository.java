package mdw_proyectofinal.vetlife.repository.api.v1;

import mdw_proyectofinal.vetlife.model.api.v1.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByUsuarioId(Integer usuarioId);
    Optional<Mascota> findByIdAndUsuarioId(Long id, Integer usuarioId);
    boolean existsByUsuarioIdAndNombreIgnoreCaseAndEspecieId(Integer usuarioId, String nombre, Integer especieId);
}