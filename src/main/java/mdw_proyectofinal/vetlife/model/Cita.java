package mdw_proyectofinal.vetlife.model;

public class Cita {

    private String id;
    private String servicio;
    private int mascotaId;
    private String mascotaNombre;
    private String fecha;
    private String hora;
    private String notas;
    private String estado;
    private int usuarioId;

    public Cita() {
    }

    public Cita(String id, String servicio, int mascotaId, String mascotaNombre, String fecha, String hora,
            String notas, String estado, int usuarioId) {
        this.id = id;
        this.servicio = servicio;
        this.mascotaId = mascotaId;
        this.mascotaNombre = mascotaNombre;
        this.fecha = fecha;
        this.hora = hora;
        this.notas = notas;
        this.estado = estado;
        this.usuarioId = usuarioId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public int getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(int mascotaId) {
        this.mascotaId = mascotaId;
    }

    public String getMascotaNombre() {
        return mascotaNombre;
    }

    public void setMascotaNombre(String mascotaNombre) {
        this.mascotaNombre = mascotaNombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}
