package mdw_proyectofinal.vetlife.model;

// Ticket de Brayan: model base de Mascota
public class Mascota {

    private String id;
    private String nombre;
    private String especie;
    private String raza;

    public Mascota() {
    }

    public Mascota(String id, String nombre, String especie, String raza) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
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

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }
}
// Fin Ticket de Brayan
