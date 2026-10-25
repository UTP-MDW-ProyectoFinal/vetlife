package mdw_proyectofinal.vetlife.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo del PATCH /api/v1/admin/perfil (P6).
 * Todos los campos son opcionales: solo se aplican y validan los que llegan
 * (regla de la seccion 5.5 de la guia: @Size/@Email aceptan null).
 *
 * IMPORTANTE: el DTO deliberadamente NO incluye username ni id, porque el perfil
 * del admin no permite cambiarlos (asi nadie se renombra a adam).
 */
public class ActualizarAdminDTO {

    @Size(min = 1, max = 60, message = "El nombre debe tener entre 1 y 60 caracteres")
    private String nombre;

    @Size(min = 1, max = 60, message = "El apellido debe tener entre 1 y 60 caracteres")
    private String apellido;

    @Size(max = 60, message = "El cargo no puede exceder 60 caracteres")
    private String cargo;

    @Email(message = "El correo no es valido")
    @Size(max = 120, message = "El correo no puede exceder 120 caracteres")
    private String correo;

    @Size(min = 8, max = 64, message = "La contrasena debe tener entre 8 y 64 caracteres")
    private String password;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean vacio() {
        return nombre == null && apellido == null && cargo == null && correo == null && password == null;
    }
}
