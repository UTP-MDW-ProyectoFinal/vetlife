package mdw_proyectofinal.vetlife.model;

import java.util.Locale;

// Ticket de Brayan: model base de Servicio
public class Servicio {

    private String id;
    private String nombre;
    private String categoria;
    private int duracion;
    private double precio;
    private boolean activo;
    private String icono;
    private String descripcion;

    public Servicio() {
    }

    public Servicio(String id, String nombre, String categoria, int duracion, double precio, boolean activo,
            String icono, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.duracion = duracion;
        this.precio = precio;
        this.activo = activo;
        this.icono = icono;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDuracionFormateada() {
        if (duracion >= 1440) {
            return Math.round(duracion / 1440.0) + " día(s)";
        }
        if (duracion >= 60) {
            return Math.round(duracion / 60.0) + " h";
        }
        return duracion + " min";
    }

    public String getPrecioFormateado() {
        return String.format(Locale.ROOT, "%.2f", precio);
    }
}
// Fin Ticket de Brayan
