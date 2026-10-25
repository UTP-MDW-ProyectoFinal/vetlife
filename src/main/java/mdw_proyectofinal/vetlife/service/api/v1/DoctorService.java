package mdw_proyectofinal.vetlife.service.api.v1;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mdw_proyectofinal.vetlife.dto.DoctorRegistroDTO;
import mdw_proyectofinal.vetlife.dto.DoctorResponse;
import mdw_proyectofinal.vetlife.model.api.v1.Doctor;
import mdw_proyectofinal.vetlife.model.api.v1.Especialidad;
import mdw_proyectofinal.vetlife.repository.api.v1.DoctorRepository;
import mdw_proyectofinal.vetlife.repository.api.v1.EspecialidadRepository;

/** Logica de listado con filtros y alta de doctores (P6). */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final EspecialidadRepository especialidadRepository;

    public DoctorService(DoctorRepository doctorRepository, EspecialidadRepository especialidadRepository) {
        this.doctorRepository = doctorRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> listar(Integer especialidadId, Boolean activo) {
        return doctorRepository.buscarConFiltros(especialidadId, activo).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Especialidad> buscarEspecialidad(Integer especialidadId) {
        return especialidadRepository.findById(especialidadId);
    }

    public boolean emailRegistrado(String email) {
        return doctorRepository.existsByEmail(email);
    }

    public boolean licenciaRegistrada(String numeroLicencia) {
        return doctorRepository.existsByNumeroLicencia(numeroLicencia);
    }

    public Doctor construirDoctor(DoctorRegistroDTO dto, Especialidad especialidad) {
        Doctor doctor = new Doctor();
        doctor.setEspecialidad(especialidad);
        doctor.setNombre(dto.getNombre().trim());
        doctor.setApellido(dto.getApellido().trim());
        doctor.setEmail(dto.getEmail().trim().toLowerCase());
        doctor.setTelefono(dto.getTelefono() == null ? null : dto.getTelefono().trim());
        doctor.setNumeroLicencia(dto.getNumeroLicencia().trim());
        doctor.setActivo(true);
        return doctor;
    }

    @Transactional
    public DoctorResponse guardar(Doctor doctor) {
        return aRespuesta(doctorRepository.save(doctor));
    }

    private DoctorResponse aRespuesta(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getEspecialidad().getId(),
                doctor.getEspecialidad().getNombre(),
                doctor.getNombre(),
                doctor.getApellido(),
                doctor.getEmail(),
                doctor.getTelefono(),
                doctor.getNumeroLicencia(),
                doctor.isActivo());
    }
}
