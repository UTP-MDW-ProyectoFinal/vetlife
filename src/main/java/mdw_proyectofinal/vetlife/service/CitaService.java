package mdw_proyectofinal.vetlife.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Service;

import mdw_proyectofinal.vetlife.model.Cita;
import mdw_proyectofinal.vetlife.model.Mascota;
import mdw_proyectofinal.vetlife.model.Servicio;
import mdw_proyectofinal.vetlife.repository.CitaRepository;
import mdw_proyectofinal.vetlife.repository.MascotaRepository;
import mdw_proyectofinal.vetlife.repository.ServicioRepository;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final ServicioRepository servicioRepository;
    private final MascotaRepository mascotaRepository;

    public CitaService(CitaRepository citaRepository, ServicioRepository servicioRepository,
            MascotaRepository mascotaRepository) {
        this.citaRepository = citaRepository;
        this.servicioRepository = servicioRepository;
        this.mascotaRepository = mascotaRepository;
    }

    public String reservar(String servicioId, int mascotaId, String fecha, String hora, String notas, int usuarioId) {
        Servicio servicio = servicioRepository.buscarPorId(servicioId == null ? "" : servicioId.trim())
                .filter(Servicio::isActivo)
                .orElse(null);
        if (servicio == null) {
            return "Selecciona un servicio válido.";
        }

        Mascota mascota = mascotaRepository.buscarPorId(mascotaId).orElse(null);
        if (mascota == null) {
            return "Selecciona una mascota válida.";
        }
        if (mascota.getDuenioId() != usuarioId) {
            return "La mascota seleccionada no pertenece a tu cuenta.";
        }

        LocalDate fechaCita;
        LocalTime horaCita;
        try {
            fechaCita = LocalDate.parse(fecha.trim());
            horaCita = LocalTime.parse(hora.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            return "La fecha y la hora deben tener un formato válido.";
        }

        LocalDate hoy = LocalDate.now();
        if (fechaCita.isBefore(hoy)) {
            return "La fecha de la cita no puede ser anterior a hoy.";
        }
        if (fechaCita.equals(hoy) && horaCita.isBefore(LocalTime.now())) {
            return "La hora de la cita ya pasó. Selecciona un horario posterior a la hora actual.";
        }

        Cita cita = new Cita(null, servicio.getNombre(), mascota.getId(), mascota.getNombre(),
                fechaCita.toString(), horaCita.toString(),
                notas == null ? "" : notas.trim(), null, usuarioId);
        citaRepository.guardar(cita);
        return null;
    }
}
