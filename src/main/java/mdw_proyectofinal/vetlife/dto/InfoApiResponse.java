package mdw_proyectofinal.vetlife.dto;

import java.util.List;

public class InfoApiResponse {
    private String nombre;
    private String version;
    private String descripcion;
    private List<Recurso> recursos;

    public static class Recurso {
        private String nombre;
        private String ruta;

        public Recurso() {
        }

        public Recurso(String nombre, String ruta) {
            this.nombre = nombre;
            this.ruta = ruta;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getRuta() {
            return ruta;
        }

        public void setRuta(String ruta) {
            this.ruta = ruta;
        }
    }

    public InfoApiResponse() {
    }

    public InfoApiResponse(String nombre, String version, String descripcion, List<Recurso> recursos) {
        this.nombre = nombre;
        this.version = version;
        this.descripcion = descripcion;
        this.recursos = recursos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = recursos;
    }
}
