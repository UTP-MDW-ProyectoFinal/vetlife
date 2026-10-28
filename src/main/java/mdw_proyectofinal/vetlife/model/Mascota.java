package mdw_proyectofinal.vetlife.model;

public class Mascota {

    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private String edad;
    private String sexo;
    private int duenioId;
    private boolean activa;
    private int visitas;

    public Mascota() {
        this.activa = true;
    }

    public Mascota(int id, String nombre, String especie, String raza, String edad, String sexo, int duenioId) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.sexo = sexo;
        this.duenioId = duenioId;
        this.activa = true;
    }

    public Mascota(int id, String nombre, String especie, String raza, String edad, String sexo, int duenioId,
            int visitas) {
        this(id, nombre, especie, raza, edad, sexo, duenioId);
        this.visitas = visitas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getDuenioId() {
        return duenioId;
    }

    public void setDuenioId(int duenioId) {
        this.duenioId = duenioId;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public int getVisitas() {
        return visitas;
    }

    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }
}
