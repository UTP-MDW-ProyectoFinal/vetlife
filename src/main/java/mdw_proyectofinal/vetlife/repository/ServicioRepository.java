package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Servicio;

@Repository
public class ServicioRepository {

    private final List<Servicio> servicios = new ArrayList<>();

    public ServicioRepository() {
        servicios.add(new Servicio("S-01", "Laparoscopía", "Cirugía", "1 h", 180, "bi-camera-reels"));
        servicios.add(new Servicio("S-02", "Cardiología", "Medicina Interna", "45 min", 55, "bi-heart-pulse"));
        servicios.add(new Servicio("S-03", "Endoscopia", "Imagenología", "30 min", 45, "bi-camera-video"));
        servicios.add(new Servicio("S-04", "Ginecología", "Medicina Interna", "45 min", 95, "bi-gender-female"));
        servicios.add(new Servicio("S-05", "Traumatología", "Cirugía", "1 h", 150, "bi-bandaid"));
        servicios.add(new Servicio("S-06", "Patología", "Medicina Interna", "20 min", 40, "bi-clipboard2-pulse"));
        servicios.add(new Servicio("S-07", "Anestesia Inhalatoria", "Cirugía", "3 h", 30, "bi-lungs"));
        servicios.add(new Servicio("S-08", "Ecografía", "Imagenología", "20 min", 35, "bi-soundwave"));
        servicios.add(new Servicio("S-09", "Rayos X Digital", "Imagenología", "30 min", 60, "bi-radioactive"));
        servicios.add(new Servicio("S-10", "Laboratorio", "Diagnóstico", "2 h", 20, "bi-droplet"));
        servicios.add(new Servicio("S-11", "Hospitalización", "Cuidados", "1 día(s)", 120, "bi-hospital"));
    }

    public List<Servicio> listarTodas() {
        return List.copyOf(servicios);
    }

    public List<Servicio> listarActivos() {
        return servicios.stream()
                .filter(Servicio::isActivo)
                .toList();
    }

    public List<String> listarCategorias() {
        return servicios.stream()
                .map(Servicio::getCategoria)
                .distinct()
                .toList();
    }

    public Map<String, List<Servicio>> listarPorCategoria() {
        Map<String, List<Servicio>> porCategoria = new LinkedHashMap<>();
        for (Servicio servicio : servicios) {
            porCategoria.computeIfAbsent(servicio.getCategoria(), clave -> new ArrayList<>()).add(servicio);
        }
        return porCategoria;
    }

    public Optional<Servicio> buscarPorId(String id) {
        return servicios.stream().filter(s -> s.getId().equals(id)).findFirst();
    }
}
