package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Mascota;

@Repository
public class MascotaRepository {

    private final List<Mascota> mascotas = new ArrayList<>();
    private int siguienteId = 1;

    public MascotaRepository() {
        mascotas.add(new Mascota(1, "Luna", "Perro", "Border Collie", "4 años", "Hembra", 1, 12));
        // Los ids 101 y 102 coinciden con las opciones del select en citas.html
        mascotas.add(new Mascota(101, "Doggy", "Perro", "Caniche", "3 años", "Macho", 1, 8));
        mascotas.add(new Mascota(102, "Michi", "Gato", "Siamés", "2 años", "Hembra", 1, 5));
        siguienteId = 103;
    }

    public Mascota guardar(Mascota mascota) {
        if (mascota.getId() == 0) {
            mascota.setId(siguienteId++);
        }
        mascotas.add(mascota);
        return mascota;
    }

    public List<Mascota> listarTodas() {
        return List.copyOf(mascotas);
    }

    public List<Mascota> listarPorDuenio(int duenioId) {
        return mascotas.stream()
                .filter(m -> m.getDuenioId() == duenioId)
                .toList();
    }

    public Optional<Mascota> buscarPorId(int id) {
        return mascotas.stream().filter(m -> m.getId() == id).findFirst();
    }
}
