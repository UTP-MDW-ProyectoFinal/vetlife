package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Cita;

@Repository
public class CitaRepository {

    private final List<Cita> citas = new ArrayList<>();

    public Cita guardar(Cita cita) {
        cita.setId("C-" + System.currentTimeMillis());
        cita.setEstado("pendiente");
        citas.add(cita);
        return cita;
    }

    public List<Cita> listarPorUsuario(int usuarioId) {
        return citas.stream()
                .filter(c -> c.getUsuarioId() == usuarioId)
                .toList();
    }
}
