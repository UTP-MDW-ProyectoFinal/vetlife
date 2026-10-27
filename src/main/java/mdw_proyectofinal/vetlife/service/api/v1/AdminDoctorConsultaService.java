package mdw_proyectofinal.vetlife.service.api.v1;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mdw_proyectofinal.vetlife.dto.CitaAdminResponse;
import mdw_proyectofinal.vetlife.dto.DoctorDetalleResponse;
import mdw_proyectofinal.vetlife.dto.PacienteResponse;
import mdw_proyectofinal.vetlife.model.api.v1.Cita;
import mdw_proyectofinal.vetlife.model.api.v1.EstadoCita;
import mdw_proyectofinal.vetlife.model.api.v1.Mascota;
import mdw_proyectofinal.vetlife.model.api.v1.Usuario;
import mdw_proyectofinal.vetlife.repository.api.v1.ConsultaCitaRepository;
import mdw_proyectofinal.vetlife.repository.api.v1.ConsultaDoctorRepository;
import mdw_proyectofinal.vetlife.repository.api.v1.EstadoCitaRepository;

/**
 * Consultas de admin sobre doctores (P5). Solo lectura: cruza doctor, cita, mascota
 * y usuario, y convierte a DTO dentro de la transaccion (open-in-view esta apagado).
 */
@Service
@Transactional(readOnly = true)
public class AdminDoctorConsultaService {

    private final ConsultaDoctorRepository doctorRepository;
    private final ConsultaCitaRepository citaRepository;
    private final EstadoCitaRepository estadoCitaRepository;

    public AdminDoctorConsultaService(ConsultaDoctorRepository doctorRepository,
            ConsultaCitaRepository citaRepository,
            EstadoCitaRepository estadoCitaRepository) {
        this.doctorRepository = doctorRepository;
        this.citaRepository = citaRepository;
        this.estadoCitaRepository = estadoCitaRepository;
    }

    public boolean doctorExiste(Long idDoctor) {
        return doctorRepository.existsById(idDoctor);
    }

    /** El repositorio de estados es de P3: aqui solo se lee. */
    public boolean estadoExiste(String estado) {
        return estadoCitaRepository.findByNombre(estado).isPresent();
    }

    /** Ficha del doctor con totalCitas y citasPendientes calculados con consultas de conteo. */
    public Optional<DoctorDetalleResponse> obtenerDetalle(Long idDoctor) {
        return doctorRepository.findById(idDoctor).map(doctor -> new DoctorDetalleResponse(
                doctor.getId(),
                doctor.getNombre(),
                doctor.getApellido(),
                doctor.getEspecialidad().getNombre(),
                doctor.getEmail(),
                doctor.getTelefono(),
                doctor.getNumeroLicencia(),
                doctor.isActivo(),
                citaRepository.countByDoctorId(idDoctor),
                citaRepository.countByDoctorIdAndEstadoNombre(idDoctor, EstadoCita.PENDIENTE)));
    }

    /** Citas del doctor por fecha ascendente; estado y fecha (un dia completo) son opcionales. */
    public List<CitaAdminResponse> listarCitas(Long idDoctor, String estado, LocalDate fecha) {
        LocalDateTime desde = fecha == null ? null : fecha.atStartOfDay();
        LocalDateTime hasta = fecha == null ? null : fecha.plusDays(1).atStartOfDay();
        return citaRepository.buscarCitasDeDoctor(idDoctor, estado, desde, hasta).stream()
                .map(this::aCitaAdmin)
                .toList();
    }

    /** Vacio si la cita no existe o es de otro doctor. */
    public Optional<CitaAdminResponse> obtenerCita(Long idDoctor, Long idCita) {
        return citaRepository.buscarCitaDeDoctor(idDoctor, idCita).map(this::aCitaAdmin);
    }

    /** Mascotas distintas con citas no canceladas con el doctor, ordenadas por nombre. */
    public List<PacienteResponse> listarPacientes(Long idDoctor) {
        List<Object[]> filas = citaRepository.agruparPacientesDeDoctor(idDoctor);
        if (filas.isEmpty()) {
            return List.of();
        }

        Map<Long, Object[]> agregadoPorMascota = new HashMap<>();
        for (Object[] fila : filas) {
            agregadoPorMascota.put(((Number) fila[0]).longValue(), fila);
        }

        // Una sola consulta carga mascota + especie + dueno de todos los ids, ya ordenadas por nombre.
        return citaRepository.buscarMascotasConEspecieYDuenio(agregadoPorMascota.keySet()).stream()
                .map(mascota -> {
                    Object[] agregado = agregadoPorMascota.get(mascota.getId());
                    return aPaciente(mascota, ((Number) agregado[1]).longValue(), aFecha(agregado[2]));
                })
                .toList();
    }

    private CitaAdminResponse aCitaAdmin(Cita cita) {
        Mascota mascota = cita.getMascota();
        Usuario cliente = mascota.getUsuario();
        return new CitaAdminResponse(
                cita.getId(),
                cita.getFechaHora(),
                cita.getMotivo(),
                cita.getEstado().getNombre(),
                new CitaAdminResponse.MascotaResumen(mascota.getId(), mascota.getNombre(),
                        mascota.getEspecie().getNombre()),
                new CitaAdminResponse.ClienteResumen(Long.valueOf(cliente.getId()), cliente.getNombre(),
                        cliente.getCorreo()));
    }

    private PacienteResponse aPaciente(Mascota mascota, long totalCitas, LocalDateTime ultimaVisita) {
        Usuario duenio = mascota.getUsuario();
        return new PacienteResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie().getNombre(),
                mascota.getRaza(),
                new PacienteResponse.Duenio(Long.valueOf(duenio.getId()), duenio.getNombre(), duenio.getCorreo()),
                totalCitas,
                ultimaVisita);
    }

    private LocalDateTime aFecha(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof LocalDateTime fecha) {
            return fecha;
        }
        return ((Timestamp) valor).toLocalDateTime();
    }
}
