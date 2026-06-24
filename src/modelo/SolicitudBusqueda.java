package modelo;

import java.time.LocalDate;

public class SolicitudBusqueda {

    private int id;
    private Buscador buscador;
    private String sectorDeseado;
    private double presupuestoMaximo;
    private int habitacionesNecesarias;
    private boolean aceptaMascotas;
    private String horarioPreferido;
    private LocalDate fechaCreacion;
    private String estado;

    public SolicitudBusqueda(int id, Buscador buscador, String sectorDeseado,
                             double presupuestoMaximo, int habitacionesNecesarias,
                             boolean aceptaMascotas, String horarioPreferido) {

        this.id = id;
        this.buscador = buscador;
        this.sectorDeseado = sectorDeseado;
        this.presupuestoMaximo = presupuestoMaximo;
        this.habitacionesNecesarias = habitacionesNecesarias;
        this.aceptaMascotas = aceptaMascotas;
        this.horarioPreferido = horarioPreferido;
        this.fechaCreacion = LocalDate.now();
        this.estado = "ACTIVA";
    }

    public int getId() {
        return id;
    }

    public Buscador getBuscador() {
        return buscador;
    }

    public String getSectorDeseado() {
        return sectorDeseado;
    }

    public void setSectorDeseado(String sectorDeseado) {
        this.sectorDeseado = sectorDeseado;
    }

    public double getPresupuestoMaximo() {
        return presupuestoMaximo;
    }

    public void setPresupuestoMaximo(double presupuestoMaximo) {
        this.presupuestoMaximo = presupuestoMaximo;
    }

    public int getHabitacionesNecesarias() {
        return habitacionesNecesarias;
    }

    public void setHabitacionesNecesarias(int habitacionesNecesarias) {
        this.habitacionesNecesarias = habitacionesNecesarias;
    }

    public boolean isAceptaMascotas() {
        return aceptaMascotas;
    }

    public void setAceptaMascotas(boolean aceptaMascotas) {
        this.aceptaMascotas = aceptaMascotas;
    }

    public String getHorarioPreferido() {
        return horarioPreferido;
    }

    public void setHorarioPreferido(String horarioPreferido) {
        this.horarioPreferido = horarioPreferido;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}