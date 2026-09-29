package mdw_proyectofinal.vetlife.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import mdw_proyectofinal.vetlife.model.Servicio;

// Ticket de Brayan: repository en memoria de servicios
@Repository
public class ServicioRepository {

    private final List<Servicio> servicios = new ArrayList<>();

    public ServicioRepository() {
        servicios.add(new Servicio("S-01", "Laparoscopía", "Cirugía", 60, 180, true, "bi-camera-reels",
                "Cirugías mínimamente invasivas con recuperación más rápida y segura."));
        servicios.add(new Servicio("S-02", "Cardiología", "Medicina Interna", 45, 55, true, "bi-heart-pulse",
                "Evaluación y seguimiento de la salud cardiovascular de tu mascota."));
        servicios.add(new Servicio("S-03", "Endoscopia", "Imagenología", 30, 45, true, "bi-camera-video",
                "Exploración interna precisa sin necesidad de cirugía abierta."));
        servicios.add(new Servicio("S-04", "Ginecología", "Medicina Interna", 45, 95, true, "bi-gender-female",
                "Salud reproductiva, gestación y control hormonal especializado."));
        servicios.add(new Servicio("S-05", "Traumatología", "Cirugía", 60, 150, true, "bi-bandaid",
                "Diagnóstico y tratamiento de fracturas, luxaciones y lesiones óseas."));
        servicios.add(new Servicio("S-06", "Patología", "Medicina Interna", 20, 40, true, "bi-clipboard2-pulse",
                "Análisis de tejidos y muestras para diagnósticos certeros."));
        servicios.add(new Servicio("S-07", "Anestesia Inhalatoria", "Cirugía", 180, 30, true, "bi-lungs",
                "Procedimientos seguros con monitoreo constante durante la sedación."));
        servicios.add(new Servicio("S-08", "Ecografía", "Imagenología", 20, 35, true, "bi-soundwave",
                "Imágenes en tiempo real para un diagnóstico rápido y sin dolor."));
        servicios.add(new Servicio("S-09", "Rayos X Digital", "Imagenología", 30, 60, true, "bi-radioactive",
                "Radiografías digitales de alta resolución para diagnóstico inmediato."));
        servicios.add(new Servicio("S-10", "Laboratorio", "Diagnóstico", 120, 20, true, "bi-droplet",
                "Análisis clínicos completos con resultados confiables y oportunos."));
        servicios.add(new Servicio("S-11", "Hospitalización", "Cuidados", 1440, 120, true, "bi-hospital",
                "Cuidado intensivo y monitoreo continuo en casos que lo requieran."));
    }

    public List<Servicio> findAll() {
        return new ArrayList<>(servicios);
    }

    public List<Servicio> findAllActivos() {
        List<Servicio> activos = new ArrayList<>();
        for (Servicio servicio : servicios) {
            if (servicio.isActivo()) {
                activos.add(servicio);
            }
        }
        return activos;
    }

    public Optional<Servicio> findById(String id) {
        return servicios.stream().filter(servicio -> servicio.getId().equals(id)).findFirst();
    }
}
// Fin Ticket de Brayan
