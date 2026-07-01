package modelo;

public class Arrendador extends Usuario {

    public Arrendador(String usuario, String clave, String nombre,
                      String telefono, String correo) {

        super(usuario, clave, nombre, telefono, correo, "ARRENDADOR");
    }
}
