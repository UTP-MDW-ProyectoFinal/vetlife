package mdw_proyectofinal.vetlife.dto;

public class AdminResponse {
    private Integer id;
    private String username;
    private String nombre;
    private String email;

    public AdminResponse() {
    }

    public AdminResponse(Integer id, String username, String nombre, String email) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
