package mdw_proyectofinal.vetlife.repository.api.v1;

import org.springframework.data.jpa.repository.JpaRepository;

import mdw_proyectofinal.vetlife.model.api.v1.Especie;

public interface EspecieRepository extends JpaRepository<Especie, Integer> {
}
