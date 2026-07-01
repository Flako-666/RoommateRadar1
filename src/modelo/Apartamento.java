package modelo;

import java.time.LocalDate;

public class Apartamento {

    private int id;
    private String direccion;
    private String sector;
    private double precio;
    private int habitaciones;
    private String propietario;
    private String estado;
    private boolean aceptaMascotas;
    private LocalDate fechaPublicacion;
    private LocalDate fechaExpiracion;

    public Apartamento(int id, String direccion, String sector, double precio,
                       int habitaciones, String propietario, boolean aceptaMascotas) {

        this.id = id;
        this.direccion = direccion;
        this.sector = sector;
        this.precio = precio;
        this.habitaciones = habitaciones;
        this.propietario = propietario;
        this.aceptaMascotas = aceptaMascotas;
        this.estado = "DISPONIBLE";
        this.fechaPublicacion = LocalDate.now();
        this.fechaExpiracion = this.fechaPublicacion.plusDays(30);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(int habitaciones) {
        this.habitaciones = habitaciones;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isAceptaMascotas() {
        return aceptaMascotas;
    }

    public void setAceptaMascotas(boolean aceptaMascotas) {
        this.aceptaMascotas = aceptaMascotas;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean estaPublicacionVigente() {
        LocalDate hoy = LocalDate.now();
        return !hoy.isAfter(fechaExpiracion);
    }

    public boolean estaPublicacionVencida() {
        return LocalDate.now().isAfter(fechaExpiracion);
    }

    public boolean estaDisponibleParaSolicitud() {
        return estado.equals("DISPONIBLE") && estaPublicacionVigente();
    }

    public void renovarPublicacion() {
        this.fechaPublicacion = LocalDate.now();
        this.fechaExpiracion = this.fechaPublicacion.plusDays(30);
    }
}