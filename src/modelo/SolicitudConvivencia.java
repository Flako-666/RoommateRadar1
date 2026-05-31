package modelo;

import java.time.LocalDate;

public class SolicitudConvivencia {

    private int id;
    private Buscador buscador;
    private Apartamento apartamento;
    private String fecha;
    private String estado;

    public SolicitudConvivencia(int id, Buscador buscador, Apartamento apartamento) {
        this.id = id;
        this.buscador = buscador;
        this.apartamento = apartamento;
        this.fecha = LocalDate.now().toString();
        this.estado = "PENDIENTE";
    }

    public int getId() { return id; }

    public Buscador getBuscador() { return buscador; }

    public Apartamento getApartamento() { return apartamento; }

    public String getFecha() { return fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
