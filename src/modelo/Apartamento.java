package modelo;

public class Apartamento {

    private int id;
    private String direccion;
    private String sector;
    private double precio;
    private int habitaciones;
    private String propietario;
    private String estado;

    public Apartamento(int id, String direccion, String sector, double precio,
                       int habitaciones, String propietario) {
        this.id = id;
        this.direccion = direccion;
        this.sector = sector;
        this.precio = precio;
        this.habitaciones = habitaciones;
        this.propietario = propietario;
        this.estado = "DISPONIBLE";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getHabitaciones() { return habitaciones; }
    public void setHabitaciones(int habitaciones) { this.habitaciones = habitaciones; }

    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
