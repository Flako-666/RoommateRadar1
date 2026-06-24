package modelo;

public class Buscador extends Usuario {

    private double presupuesto;
    private String ocupacion;
    private String horario;
    private boolean mascotas;
    private String ruido;
    private String orden;

    public Buscador(String usuario, String clave, String nombre,
                    String telefono, String correo, double presupuesto,
                    String ocupacion, String horario, boolean mascotas,
                    String ruido, String orden) {

        super(usuario, clave, nombre, telefono, correo, "BUSCADOR");
        this.presupuesto = presupuesto;
        this.ocupacion = ocupacion;
        this.horario = horario;
        this.mascotas = mascotas;
        this.ruido = ruido;
        this.orden = orden;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean isMascotas() {
        return mascotas;
    }

    public void setMascotas(boolean mascotas) {
        this.mascotas = mascotas;
    }

    public String getRuido() {
        return ruido;
    }

    public void setRuido(String ruido) {
        this.ruido = ruido;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }
}