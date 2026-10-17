package mdw_proyectofinal.vetlife.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mdw_proyectofinal.vetlife.model.Mascota;
import mdw_proyectofinal.vetlife.repository.MascotaRepository;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    public String registrar(String nombre, String especie, String raza, String edad, String sexo, int duenioId) {
        if (nombre == null || nombre.isBlank()) {
            return "El nombre de la mascota es obligatorio.";
        }
        if (especie == null || especie.isBlank()) {
            return "Selecciona una especie.";
        }

        Mascota mascota = new Mascota(0, nombre.trim(),
                especie,
                raza == null ? "" : raza.trim(),
                edad == null ? "" : edad.trim(),
                sexo == null || sexo.isBlank() ? "Macho" : sexo,
                duenioId);
        mascotaRepository.guardar(mascota);
        return null;
    }

    public List<Mascota> listarPorDuenio(int duenioId) {
        return mascotaRepository.listarPorDuenio(duenioId);
    }

    public List<Mascota> listarTodas() {
        return mascotaRepository.listarTodas();
    }
}
