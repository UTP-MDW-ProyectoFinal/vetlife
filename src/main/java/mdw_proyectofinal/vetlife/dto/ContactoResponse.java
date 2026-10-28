package mdw_proyectofinal.vetlife.dto;

public class ContactoResponse {
    private String direccion;
    private String telefono;
    private String email;
    private String horarioAtencion;

    public ContactoResponse() {
    }

    public ContactoResponse(String direccion, String telefono, String email, String horarioAtencion) {
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.horarioAtencion = horarioAtencion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHorarioAtencion() {
        return horarioAtencion;
    }

    public void setHorarioAtencion(String horarioAtencion) {
        this.horarioAtencion = horarioAtencion;
    }
}
