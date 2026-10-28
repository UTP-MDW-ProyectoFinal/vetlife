package mdw_proyectofinal.vetlife.dto;

import java.math.BigDecimal;

public class ServicioResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioDesde;

    public ServicioResponse() {
    }

    public ServicioResponse(Long id, String nombre, String descripcion, BigDecimal precioDesde) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioDesde = precioDesde;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioDesde() {
        return precioDesde;
    }

    public void setPrecioDesde(BigDecimal precioDesde) {
        this.precioDesde = precioDesde;
    }
}
