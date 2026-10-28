package mdw_proyectofinal.vetlife.page;

import java.util.List;

import org.springframework.stereotype.Service;

import mdw_proyectofinal.vetlife.dto.ContactoResponse;
import mdw_proyectofinal.vetlife.dto.InfoApiResponse;
import mdw_proyectofinal.vetlife.dto.ServicioResponse;
import mdw_proyectofinal.vetlife.repository.ServicioRepository;

@Service
public class PageService {

    private final ServicioRepository servicioRepository;

    public PageService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public InfoApiResponse getInfo() {
        List<InfoApiResponse.Recurso> recursos = List.of(
                new InfoApiResponse.Recurso("usuarios", "/api/v1/usuario"),
                new InfoApiResponse.Recurso("admins", "/api/v1/admin"),
                new InfoApiResponse.Recurso("servicios", "/api/v1/servicios"));
        return new InfoApiResponse("VetLife API", "1.0.0", "API Veterinaria VetLife", recursos);
    }

    public ContactoResponse getContacto() {
        return new ContactoResponse("Av. Siempre Viva 123", "+51 999 999 999", "contacto@vetlife.com",
                "Lunes a Viernes de 9:00 a 18:00");
    }

    public List<ServicioResponse> getServicios() {
        return servicioRepository.listarActivos().stream()
                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                .map(s -> new ServicioResponse(
                        s.getId() != null ? Long.parseLong(s.getId().replaceAll("\\D+", "0")) : null,
                        s.getNombre(),
                        s.getCategoria(),
                        java.math.BigDecimal.valueOf(s.getPrecio())))
                .toList();
    }
}
