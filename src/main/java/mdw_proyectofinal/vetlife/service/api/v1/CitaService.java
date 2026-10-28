package mdw_proyectofinal.vetlife.service.api.v1;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mdw_proyectofinal.vetlife.dto.ActualizarCitaRequest;
import mdw_proyectofinal.vetlife.dto.CitaResponse;
import mdw_proyectofinal.vetlife.dto.CrearCitaRequest;
import mdw_proyectofinal.vetlife.model.api.v1.Cita;
import mdw_proyectofinal.vetlife.model.api.v1.Doctor;
import mdw_proyectofinal.vetlife.model.api.v1.EstadoCita;
import mdw_proyectofinal.vetlife.model.api.v1.Mascota;
import mdw_proyectofinal.vetlife.repository.api.v1.CitaRepository;
import mdw_proyectofinal.vetlife.repository.api.v1.DoctorRepository;
import mdw_proyectofinal.vetlife.repository.api.v1.EstadoCitaRepository;
import mdw_proyectofinal.vetlife.repository.api.v1.MascotaRepository;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final DoctorRepository doctorRepository;
    private final EstadoCitaRepository estadoCitaRepository;

    public CitaService(CitaRepository citaRepository,
            MascotaRepository mascotaRepository,
            DoctorRepository doctorRepository,
            EstadoCitaRepository estadoCitaRepository) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
        this.doctorRepository = doctorRepository;
        this.estadoCitaRepository = estadoCitaRepository;
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> listarPorUsuario(Long usuarioId, String estadoNombre) {
        if (estadoNombre != null && !estadoNombre.isBlank()) {
            String nombreNormalizado = estadoNombre.trim().toUpperCase();
            if (!List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.CANCELADA, EstadoCita.COMPLETADA)
                    .contains(nombreNormalizado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado indicado no existe");
            }
        }

        var sort = Sort.by(Sort.Direction.ASC, "fechaHora");
        List<Cita> citas = citaRepository.findByMascotaUsuarioId(usuarioId, sort);
        if (estadoNombre == null || estadoNombre.isBlank()) {
            return citas.stream().map(this::toResponse).toList();
        }

        String nombreNormalizado = estadoNombre.trim().toUpperCase();
        return citas.stream()
                .filter(c -> c.getEstado() != null && nombreNormalizado.equals(c.getEstado().getNombre()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CitaResponse crear(Long usuarioId, CrearCitaRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud es obligatoria");
        }

        Mascota mascota = mascotaRepository.findByIdAndUsuarioId(request.getMascotaId(), usuarioId.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La mascota no existe o no pertenece al usuario"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El doctor no existe"));

        if (!doctor.isActivo()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El doctor no está activo");
        }

        EstadoCita pendiente = estadoCitaRepository.findByNombre(EstadoCita.PENDIENTE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No existe el estado PENDIENTE"));

        if (citaRepository.existsByDoctorIdAndFechaHoraAndEstadoNombreNot(doctor.getId(), request.getFechaHora(), EstadoCita.CANCELADA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El doctor ya tiene una cita activa en esa fecha y hora");
        }

        Cita cita = new Cita(mascota, doctor, pendiente, request.getFechaHora(), request.getMotivo().trim());
        Cita guardada = citaRepository.save(cita);
        return toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public CitaResponse buscarPorId(Long usuarioId, Long citaId) {
        return citaRepository.findByIdAndMascotaUsuarioId(citaId, usuarioId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La cita no existe o no pertenece al usuario"));
    }

    @Transactional
    public CitaResponse actualizar(Long usuarioId, Long citaId, ActualizarCitaRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud es obligatoria");
        }

        boolean tieneCampos = request.getFechaHora() != null || request.getMotivo() != null || request.getEstado() != null;
        if (!tieneCampos) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar al menos un campo para actualizar");
        }

        Cita cita = citaRepository.findByIdAndMascotaUsuarioId(citaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La cita no existe o no pertenece al usuario"));

        if (!EstadoCita.PENDIENTE.equals(cita.getEstado().getNombre())
                && !EstadoCita.CONFIRMADA.equals(cita.getEstado().getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden modificar citas pendientes o confirmadas");
        }

        if (request.getEstado() != null) {
            if (!EstadoCita.CANCELADA.equals(request.getEstado().trim().toUpperCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permite cancelar la cita");
            }
            EstadoCita cancelada = estadoCitaRepository.findByNombre(EstadoCita.CANCELADA)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No existe el estado CANCELADA"));
            cita.setEstado(cancelada);
        }

        if (request.getFechaHora() != null) {
            if (cita.getEstado() != null && EstadoCita.CANCELADA.equals(cita.getEstado().getNombre())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede modificar una cita cancelada");
            }
            if (citaRepository.existsByDoctorIdAndFechaHoraAndEstadoNombreNot(cita.getDoctor().getId(), request.getFechaHora(), EstadoCita.CANCELADA)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El doctor ya tiene otra cita activa en esa fecha y hora");
            }
            cita.setFechaHora(request.getFechaHora());
        }

        if (request.getMotivo() != null) {
            cita.setMotivo(request.getMotivo().trim());
        }

        return toResponse(citaRepository.save(cita));
    }

    private CitaResponse toResponse(Cita cita) {
        return new CitaResponse(
                cita.getId(),
                cita.getMascota().getId(),
                cita.getMascota().getNombre(),
                cita.getDoctor().getId(),
                cita.getDoctor().getNombre() + " " + cita.getDoctor().getApellido(),
                cita.getFechaHora(),
                cita.getMotivo(),
                cita.getEstado().getNombre());
    }
}
