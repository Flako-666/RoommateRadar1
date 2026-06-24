package modelo;

public class Usuario {

    private String usuario;
    private String clave;
    private String nombre;
    private String telefono;
    private String correo;
    private String rol;

    public Usuario(String usuario, String clave, String nombre,
                   String telefono, String correo, String rol) {
        this.usuario = usuario;
        this.clave = clave;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.rol = rol;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}