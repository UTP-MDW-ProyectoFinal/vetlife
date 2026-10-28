package mdw_proyectofinal.vetlife.service.api.v1;

import lombok.RequiredArgsConstructor;
import mdw_proyectofinal.vetlife.dto.*;
import mdw_proyectofinal.vetlife.model.api.v1.*;
import mdw_proyectofinal.vetlife.repository.api.v1.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final EspecieRepository especieRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<MascotaResponse> listar(Long usuarioId, Integer especieId) {
        return mascotaRepository.findByUsuarioId(usuarioId).stream()
            .filter(m -> especieId == null || m.getEspecie().getId().equals(especieId))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public MascotaResponse crear(Long usuarioId, CrearMascotaRequest req) {
        Especie especie = especieRepository.findById(req.especieId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada"));

        if (mascotaRepository.existsByUsuarioIdAndNombreIgnoreCaseAndEspecieId(usuarioId, req.nombre(), req.especieId())) {
            throw new ConflictoException("Ya tienes una mascota con este nombre y especie");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Mascota mascota = new Mascota();
        mascota.setUsuario(usuario);
        mascota.setEspecie(especie);
        mascota.setNombre(req.nombre());
        mascota.setRaza(req.raza());
        mascota.setSexo(req.sexo());
        mascota.setFechaNacimiento(req.fechaNacimiento());
        mascota.setPesoKg(req.pesoKg());

        return mapToResponse(mascotaRepository.save(mascota));
    }

    @Transactional(readOnly = true)
    public MascotaResponse obtenerPorId(Long idMascota, Long usuarioId) {
        Mascota mascota = mascotaRepository.findByIdAndUsuarioId(idMascota, usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada o no pertenece al usuario"));
        return mapToResponse(mascota);
    }

    public MascotaResponse actualizar(Long idMascota, Long usuarioId, ActualizarMascotaRequest req) {
        Mascota mascota = mascotaRepository.findByIdAndUsuarioId(idMascota, usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada o no pertenece al usuario"));

        if (req.nombre() == null && req.especieId() == null && req.raza() == null && 
            req.sexo() == null && req.fechaNacimiento() == null && req.pesoKg() == null) {
            throw new SolicitudInvalidaException("Debe enviar al menos un campo para actualizar");
        }

        String nuevoNombre = req.nombre() != null ? req.nombre() : mascota.getNombre();
        Integer nuevaEspecieId = req.especieId() != null ? req.especieId() : mascota.getEspecie().getId();

        if (!nuevoNombre.equalsIgnoreCase(mascota.getNombre()) || !nuevaEspecieId.equals(mascota.getEspecie().getId())) {
            if (mascotaRepository.existsByUsuarioIdAndNombreIgnoreCaseAndEspecieId(usuarioId, nuevoNombre, nuevaEspecieId)) {
                throw new ConflictoException("Ya tienes una mascota con este nombre y especie");
            }
        }

        if (req.nombre() != null) mascota.setNombre(req.nombre());
        if (req.especieId() != null) {
            Especie especie = especieRepository.findById(req.especieId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada"));
            mascota.setEspecie(especie);
        }
        if (req.raza() != null) mascota.setRaza(req.raza());
        if (req.sexo() != null) mascota.setSexo(req.sexo());
        if (req.fechaNacimiento() != null) mascota.setFechaNacimiento(req.fechaNacimiento());
        if (req.pesoKg() != null) mascota.setPesoKg(req.pesoKg());

        return mapToResponse(mascotaRepository.save(mascota));
    }

    private MascotaResponse mapToResponse(Mascota m) {
        int edad = Period.between(m.getFechaNacimiento(), LocalDate.now()).getYears();
        return new MascotaResponse(
            m.getId(),
            m.getNombre(),
            new MascotaResponse.EspecieDTO(m.getEspecie().getId(), m.getEspecie().getNombre()),
            m.getRaza(),
            m.getSexo(),
            m.getFechaNacimiento(),
            edad,
            m.getPesoKg()
        );
    }
}