package mdw_proyectofinal.vetlife.model;

import java.util.List;

// Ticket de Brayan: modelo de una categoria de servicios con sus servicios
public record CategoriaServicio(String nombre, List<Servicio> servicios) {
}
// Fin Ticket de Brayan