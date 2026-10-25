package mdw_proyectofinal.vetlife.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Cuerpo del POST /api/v1/admin/doctores. */
public class DoctorRegistroDTO {

    @NotBlank(message = "El nombre del doctor es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del doctor es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;

    @NotNull(message = "La especialidad es obligatoria")
    @Positive(message = "La especialidad debe ser un id positivo")
    private Integer especialidadId;

    @NotBlank(message = "El email del doctor es obligatorio")
    @Email(message = "El email del doctor no es valido")
    @Size(max = 120, message = "El email no puede exceder 120 caracteres")
    private String email;

    @Pattern(regexp = "[0-9]{7,15}", message = "El telefono debe tener de 7 a 15 digitos")
    private String telefono;

    @NotBlank(message = "El numero de licencia es obligatorio")
    @Size(max = 30, message = "El numero de licencia no puede exceder 30 caracteres")
    @Pattern(regexp = "[A-Za-z0-9-]+", message = "El numero de licencia admite letras, numeros y guiones")
    private String numeroLicencia;

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

    public Integer getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(Integer especialidadId) {
        this.especialidadId = especialidadId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }
}
