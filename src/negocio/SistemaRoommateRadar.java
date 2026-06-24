package negocio;

import modelo.Apartamento;
import modelo.Buscador;
import modelo.SolicitudBusqueda;
import modelo.SolicitudConvivencia;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class SistemaRoommateRadar {

    private List<Usuario> usuarios;
    private List<Apartamento> apartamentos;
    private List<SolicitudConvivencia> solicitudesConvivencia;
    private List<SolicitudBusqueda> solicitudesBusqueda;

    private int contApt;
    private int contSolConvivencia;
    private int contSolBusqueda;

    private Usuario activo;

    public SistemaRoommateRadar() {
        usuarios = new ArrayList<>();
        apartamentos = new ArrayList<>();
        solicitudesConvivencia = new ArrayList<>();
        solicitudesBusqueda = new ArrayList<>();

        contApt = 1;
        contSolConvivencia = 1;
        contSolBusqueda = 1;

        activo = null;

        // Administrador por defecto
        usuarios.add(new Usuario(
                "admin",
                "admin123",
                "Administrador",
                "0000000000",
                "admin@roommate.com",
                "ADMINISTRADOR"
        ));
    }

    // Iniciar sesion
    public Usuario login(String usuario, String clave) {
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario) && u.getClave().equals(clave)) {
                activo = u;
                return u;
            }
        }
        return null;
    }

    // Cerrar sesion
    public void cerrarSesion() {
        activo = null;
    }

    // Obtener usuario activo
    public Usuario getActivo() {
        return activo;
    }

    // Verificar si ya existe un nombre de usuario
    public boolean existeUsuario(String usuario) {
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario)) {
                return true;
            }
        }
        return false;
    }

    // Registrar buscador
    public boolean registrarBuscador(Buscador b) {
        if (existeUsuario(b.getUsuario())) {
            return false;
        }

        usuarios.add(b);
        return true;
    }

    // Registrar apartamento
    public void registrarApartamento(Apartamento a) {
        a.setId(contApt);
        contApt++;
        apartamentos.add(a);
    }

    // Obtener apartamentos disponibles y con publicacion vigente
    public List<Apartamento> getDisponibles() {
        List<Apartamento> lista = new ArrayList<>();

        for (Apartamento a : apartamentos) {
            if (a.estaDisponibleParaSolicitud()) {
                lista.add(a);
            }
        }

        return lista;
    }

    // Obtener todos los apartamentos registrados
    public List<Apartamento> getApartamentos() {
        return apartamentos;
    }

    // Buscar apartamento por ID
    public Apartamento buscarApartamento(int id) {
        for (Apartamento a : apartamentos) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    // Enviar solicitud de convivencia a un apartamento especifico
    public String enviarSolicitud(Buscador b, int idApt) {
        Apartamento apt = buscarApartamento(idApt);

        if (apt == null) {
            return "Apartamento no encontrado.";
        }

        if (!apt.estaPublicacionVigente()) {
            return "La publicacion del apartamento ya expiro.";
        }

        if (!apt.getEstado().equals("DISPONIBLE")) {
            return "El apartamento no esta disponible.";
        }

        for (SolicitudConvivencia s : solicitudesConvivencia) {
            if (s.getBuscador().getUsuario().equals(b.getUsuario())
                    && s.getApartamento().getId() == idApt
                    && s.getEstado().equals("PENDIENTE")) {
                return "Ya tienes una solicitud pendiente para ese apartamento.";
            }
        }

        SolicitudConvivencia nueva = new SolicitudConvivencia(contSolConvivencia, b, apt);
        contSolConvivencia++;
        solicitudesConvivencia.add(nueva);

        return "Solicitud de convivencia enviada correctamente.";
    }

    // Aceptar solicitud de convivencia
    public String aceptarSolicitud(int id) {
        for (SolicitudConvivencia s : solicitudesConvivencia) {
            if (s.getId() == id) {

                if (!s.getEstado().equals("PENDIENTE")) {
                    return "La solicitud no esta pendiente.";
                }

                s.setEstado("ACEPTADA");
                s.getApartamento().setEstado("OCUPADO");

                return "Solicitud aceptada. Apartamento marcado como OCUPADO.";
            }
        }

        return "Solicitud no encontrada.";
    }

    // Rechazar solicitud de convivencia
    public String rechazarSolicitud(int id) {
        for (SolicitudConvivencia s : solicitudesConvivencia) {
            if (s.getId() == id) {

                if (!s.getEstado().equals("PENDIENTE")) {
                    return "La solicitud no esta pendiente.";
                }

                s.setEstado("RECHAZADA");

                return "Solicitud rechazada.";
            }
        }

        return "Solicitud no encontrada.";
    }

    // Obtener todas las solicitudes de convivencia
    public List<SolicitudConvivencia> getSolicitudes() {
        return solicitudesConvivencia;
    }

    // Obtener solicitudes de convivencia de un buscador
    public List<SolicitudConvivencia> getSolicitudesDeUsuario(String usuario) {
        List<SolicitudConvivencia> mias = new ArrayList<>();

        for (SolicitudConvivencia s : solicitudesConvivencia) {
            if (s.getBuscador().getUsuario().equals(usuario)) {
                mias.add(s);
            }
        }

        return mias;
    }

    // Registrar solicitud de busqueda de departamento
    public String registrarSolicitudBusqueda(Buscador buscador,
                                             String sectorDeseado,
                                             double presupuestoMaximo,
                                             int habitacionesNecesarias,
                                             boolean aceptaMascotas,
                                             String horarioPreferido) {

        if (sectorDeseado == null || sectorDeseado.trim().isEmpty()) {
            return "El sector deseado es obligatorio.";
        }

        if (presupuestoMaximo <= 0) {
            return "El presupuesto maximo debe ser mayor a 0.";
        }

        if (habitacionesNecesarias <= 0) {
            return "Las habitaciones necesarias deben ser al menos 1.";
        }

        if (horarioPreferido == null || horarioPreferido.trim().isEmpty()) {
            return "El horario preferido es obligatorio.";
        }

        SolicitudBusqueda nueva = new SolicitudBusqueda(
                contSolBusqueda,
                buscador,
                sectorDeseado,
                presupuestoMaximo,
                habitacionesNecesarias,
                aceptaMascotas,
                horarioPreferido
        );

        contSolBusqueda++;
        solicitudesBusqueda.add(nueva);

        return "Solicitud de busqueda registrada correctamente.";
    }

    // Obtener todas las solicitudes de busqueda
    public List<SolicitudBusqueda> getSolicitudesBusqueda() {
        return solicitudesBusqueda;
    }

    // Obtener solicitudes de busqueda de un buscador
    public List<SolicitudBusqueda> getSolicitudesBusquedaDeUsuario(String usuario) {
        List<SolicitudBusqueda> mias = new ArrayList<>();

        for (SolicitudBusqueda s : solicitudesBusqueda) {
            if (s.getBuscador().getUsuario().equals(usuario)) {
                mias.add(s);
            }
        }

        return mias;
    }

    // Cancelar solicitud de busqueda
    public String cancelarSolicitudBusqueda(int id, String usuario) {
        for (SolicitudBusqueda s : solicitudesBusqueda) {
            if (s.getId() == id && s.getBuscador().getUsuario().equals(usuario)) {

                if (!s.getEstado().equals("ACTIVA")) {
                    return "La solicitud de busqueda no esta activa.";
                }

                s.setEstado("CANCELADA");
                return "Solicitud de busqueda cancelada correctamente.";
            }
        }

        return "Solicitud de busqueda no encontrada.";
    }
    public String renovarPublicacionApartamento(int idApartamento) {
        Apartamento apt = buscarApartamento(idApartamento);

        if (apt == null) {
            return "Apartamento no encontrado.";
        }

        if (!apt.estaPublicacionVencida()) {
            return "La publicacion del apartamento aun esta vigente. No necesita renovacion.";
        }

        apt.renovarPublicacion();

        return "Publicacion renovada correctamente por 30 dias.";
    }
}