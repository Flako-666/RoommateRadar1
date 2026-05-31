package negocio;

import modelo.Apartamento;
import modelo.Buscador;
import modelo.SolicitudConvivencia;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class SistemaRoommateRadar {

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Apartamento> apartamentos = new ArrayList<>();
    private List<SolicitudConvivencia> solicitudes = new ArrayList<>();
    private int contApt = 1;
    private int contSol = 1;
    private Usuario activo;

    public SistemaRoommateRadar() {
        // admin por defecto
        usuarios.add(new Usuario("admin", "admin123", "Administrador",
                "0000000000", "admin@roommate.com", "ADMINISTRADOR"));
    }

    // login
    public Usuario login(String usuario, String clave) {
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario) && u.getClave().equals(clave)) {
                activo = u;
                return u;
            }
        }
        return null;
    }

    // verifica si el usuario ya existe
    public boolean existeUsuario(String usuario) {
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario)) return true;
        }
        return false;
    }

    // registro
    public boolean registrarBuscador(Buscador b) {
        if (existeUsuario(b.getUsuario())) return false;
        usuarios.add(b);
        return true;
    }

    public void cerrarSesion() { activo = null; }

    public Usuario getActivo() { return activo; }

    // registrar apartamento
    public void registrarApartamento(Apartamento a) {
        a.setId(contApt++);
        apartamentos.add(a);
    }

    // lista de apartamentos disponibles
    public List<Apartamento> getDisponibles() {
        List<Apartamento> lista = new ArrayList<>();
        for (Apartamento a : apartamentos) {
            if (a.getEstado().equals("DISPONIBLE")) lista.add(a);
        }
        return lista;
    }

    // buscar apartamento por id
    public Apartamento buscarApartamento(int id) {
        for (Apartamento a : apartamentos) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    // enviar solicitud con validaciones
    public String enviarSolicitud(Buscador b, int idApt) {
        Apartamento apt = buscarApartamento(idApt);
        if (apt == null) return "Apartamento no encontrado.";
        if (!apt.getEstado().equals("DISPONIBLE")) return "El apartamento no esta disponible.";
        for (SolicitudConvivencia s : solicitudes) {
            if (s.getBuscador().getUsuario().equals(b.getUsuario())
                    && s.getApartamento().getId() == idApt
                    && s.getEstado().equals("PENDIENTE")) {
                return "Ya tienes una solicitud pendiente para ese apartamento.";
            }
        }
        solicitudes.add(new SolicitudConvivencia(contSol++, b, apt));
        return "Solicitud enviada correctamente.";
    }

    // aceptar y marcar apartamento como ocupado
    public String aceptarSolicitud(int id) {
        for (SolicitudConvivencia s : solicitudes) {
            if (s.getId() == id) {
                if (!s.getEstado().equals("PENDIENTE")) return "La solicitud no esta pendiente.";
                s.setEstado("ACEPTADA");
                s.getApartamento().setEstado("OCUPADO");
                return "Solicitud aceptada. Apartamento marcado como OCUPADO.";
            }
        }
        return "Solicitud no encontrada.";
    }

    // rechazar solicitud
    public String rechazarSolicitud(int id) {
        for (SolicitudConvivencia s : solicitudes) {
            if (s.getId() == id) {
                if (!s.getEstado().equals("PENDIENTE")) return "La solicitud no esta pendiente.";
                s.setEstado("RECHAZADA");
                return "Solicitud rechazada.";
            }
        }
        return "Solicitud no encontrada.";
    }

    public List<SolicitudConvivencia> getSolicitudes() { return solicitudes; }

    public List<SolicitudConvivencia> getSolicitudesDeUsuario(String usuario) {
        List<SolicitudConvivencia> mias = new ArrayList<>();
        for (SolicitudConvivencia s : solicitudes) {
            if (s.getBuscador().getUsuario().equals(usuario)) mias.add(s);
        }
        return mias;
    }
}
