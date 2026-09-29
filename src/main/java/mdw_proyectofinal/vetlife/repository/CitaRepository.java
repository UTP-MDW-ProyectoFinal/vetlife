package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Cita;

// Ticket de Brayan: repository en memoria de citas
@Repository
public class CitaRepository {

    private final List<Cita> citas = new ArrayList<>();

    public List<Cita> findAll() {
        return new ArrayList<>(citas);
    }

    public Cita save(Cita cita) {
        if (cita.getId() == null || cita.getId().isBlank()) {
            cita.setId("C-" + System.currentTimeMillis());
        }
        if (cita.getEstado() == null || cita.getEstado().isBlank()) {
            cita.setEstado("pendiente");
        }
        citas.add(cita);
        return cita;
    }
}
// Fin Ticket de Brayan
