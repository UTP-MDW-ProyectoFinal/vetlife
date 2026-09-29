package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Mascota;

// Ticket de Brayan: repository en memoria de mascotas
@Repository
public class MascotaRepository {

    private final List<Mascota> mascotas = new ArrayList<>();

    public MascotaRepository() {
        mascotas.add(new Mascota("101", "Doggy", "Perrito", "Caniche"));
        mascotas.add(new Mascota("102", "Michi", "Gato", "Siamés"));
    }

    public List<Mascota> findAll() {
        return new ArrayList<>(mascotas);
    }

    public Optional<Mascota> findById(String id) {
        return mascotas.stream().filter(mascota -> mascota.getId().equals(id)).findFirst();
    }
}
// Fin Ticket de Brayan
