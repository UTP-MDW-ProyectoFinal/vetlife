package mdw_proyectofinal.vetlife.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import mdw_proyectofinal.vetlife.model.CategoriaServicio;
import mdw_proyectofinal.vetlife.model.HorarioDisponible;
import mdw_proyectofinal.vetlife.model.Servicio;

// Ticket de Brayan: horarios y agrupacion de servicios que necesitan las vistas
@Service
public class CatalogoService {

    public List<HorarioDisponible> generarHorarios() {
        List<HorarioDisponible> horarios = new ArrayList<>();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(17, 30);
        DateTimeFormatter formatoValor = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT);
        DateTimeFormatter formatoEtiqueta = DateTimeFormatter.ofPattern("hh:mm a", Locale.ROOT);

        for (LocalTime hora = inicio; !hora.isAfter(fin); hora = hora.plusMinutes(30)) {
            horarios.add(new HorarioDisponible(hora.format(formatoValor), hora.format(formatoEtiqueta)));
        }
        return horarios;
    }

    public List<CategoriaServicio> agruparPorCategoria(List<Servicio> servicios) {
        Map<String, List<Servicio>> porCategoria = new LinkedHashMap<>();
        for (Servicio servicio : servicios) {
            porCategoria.computeIfAbsent(servicio.getCategoria(), clave -> new ArrayList<>()).add(servicio);
        }

        List<CategoriaServicio> categorias = new ArrayList<>();
        porCategoria.forEach((nombre, lista) -> categorias.add(new CategoriaServicio(nombre, lista)));
        return categorias;
    }
}
// Fin Ticket de Brayan