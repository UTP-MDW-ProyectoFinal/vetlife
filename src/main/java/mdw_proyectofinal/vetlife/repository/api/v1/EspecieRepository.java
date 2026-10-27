package mdw_proyectofinal.vetlife.repository.api.v1;

import mdw_proyectofinal.vetlife.model.api.v1.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecieRepository extends JpaRepository<Especie, Integer> {
}