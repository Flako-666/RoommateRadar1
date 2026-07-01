package negocio;

import modelo.Apartamento;
import modelo.Arrendador;
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

        cargarDatosDemo();
    }

    // Datos quemados de demostracion: 20 arrendadores, 20 buscadores,
    // 20 apartamentos, 20 solicitudes de convivencia y 20 de busqueda
    private void cargarDatosDemo() {
        String[] nombres = {
                "Carlos", "Maria", "Jose", "Ana", "Luis", "Laura", "Diego", "Valentina",
                "Andres", "Camila", "Pablo", "Sofia", "Jorge", "Daniela", "Miguel", "Paula",
                "Ricardo", "Gabriela", "Fernando", "Isabella"
        };

        String[] apellidos = {
                "Garcia", "Martinez", "Lopez", "Hernandez", "Gonzalez", "Perez", "Sanchez",
                "Ramirez", "Torres", "Flores", "Rivera", "Gomez", "Diaz", "Reyes", "Morales",
                "Cruz", "Ortiz", "Gutierrez", "Chavez", "Ramos"
        };

        String[] sectores = {
                "Norte", "Sur", "Centro", "La Mariscal", "Cumbaya", "Gonzalez Suarez",
                "El Bosque", "La Floresta", "Quitumbe", "Carcelen", "Tumbaco", "San Rafael",
                "Conocoto", "Chillogallo", "El Inca", "La Carolina", "Solanda", "Iniaquito",
                "Cotocollao", "Pomasqui"
        };

        String[] ocupaciones = {"Estudiante", "Profesional"};
        String[] horarios = {"Diurno", "Nocturno"};
        String[] niveles = {"Bajo", "Medio", "Alto"};
        String[] ordenes = {"Ordenado", "Intermedio", "Relajado"};

        List<Arrendador> arrendadoresDemo = new ArrayList<>();
        List<Buscador> buscadoresDemo = new ArrayList<>();
        List<Apartamento> apartamentosDemo = new ArrayList<>();

        // 20 arrendadores
        for (int i = 0; i < 20; i++) {
            Arrendador a = new Arrendador(
                    "arrendador" + (i + 1),
                    "clave123",
                    nombres[i] + " " + apellidos[i],
                    "09" + String.format("%08d", 10000000 + i),
                    "arrendador" + (i + 1) + "@roommate.com"
            );

            registrarArrendador(a);
            arrendadoresDemo.add(a);
        }

        // 20 buscadores
        for (int i = 0; i < 20; i++) {
            Buscador b = new Buscador(
                    "buscador" + (i + 1),
                    "clave123",
                    nombres[19 - i] + " " + apellidos[i],
                    "09" + String.format("%08d", 50000000 + i),
                    "buscador" + (i + 1) + "@roommate.com",
                    300 + (i * 25),
                    ocupaciones[i % ocupaciones.length],
                    horarios[i % horarios.length],
                    i % 2 == 0,
                    niveles[i % niveles.length],
                    ordenes[i % ordenes.length]
            );

            registrarBuscador(b);
            buscadoresDemo.add(b);
        }

        // 20 apartamentos, uno por arrendador
        for (int i = 0; i < 20; i++) {
            Apartamento apt = new Apartamento(
                    0,
                    "Calle " + (i + 1) + " y Av. Principal",
                    sectores[i],
                    250 + (i * 20),
                    (i % 4) + 1,
                    arrendadoresDemo.get(i).getUsuario(),
                    i % 2 == 0
            );

            registrarApartamento(apt);
            apartamentosDemo.add(apt);
        }

        // 20 solicitudes de convivencia (una por buscador, sobre el apartamento de igual indice)
        for (int i = 0; i < 20; i++) {
            Buscador b = buscadoresDemo.get(i);
            Apartamento apt = apartamentosDemo.get(i);

            enviarSolicitud(b, apt.getId());
            SolicitudConvivencia nueva = solicitudesConvivencia.get(solicitudesConvivencia.size() - 1);

            if (i % 3 == 0) {
                aceptarSolicitud(nueva.getId());
            } else if (i % 3 == 1) {
                rechazarSolicitud(nueva.getId());
            }
            // el resto queda PENDIENTE
        }

        // 20 solicitudes de busqueda
        for (int i = 0; i < 20; i++) {
            Buscador b = buscadoresDemo.get(i);

            registrarSolicitudBusqueda(
                    b,
                    sectores[(i + 5) % sectores.length],
                    400 + (i * 15),
                    (i % 3) + 1,
                    i % 2 == 1,
                    horarios[(i + 1) % horarios.length]
            );
        }
    }

    // Iniciar sesion
    public Usuario login(String usuario, String clave) {
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario) && u.getClave().equals(clave)) {
                if (!u.isActivo()) {
                    return null;
                }
                activo = u;
                return u;
            }
        }
        return null;
    }

    // Verificar si las credenciales corresponden a un usuario bloqueado/baneado
    public boolean estaBaneado(String usuario, String clave) {
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario) && u.getClave().equals(clave)) {
                return !u.isActivo();
            }
        }
        return false;
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

    // Obtener todos los usuarios registrados (para gestion del administrador)
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    // Eliminar un usuario del sistema (no aplica al administrador)
    public String eliminarUsuario(String usuario) {
        if (usuario.equals("admin")) {
            return "No se puede eliminar al administrador.";
        }

        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario)) {
                usuarios.remove(u);
                return "Usuario eliminado correctamente.";
            }
        }

        return "Usuario no encontrado.";
    }

    // Bloquear/desbloquear (bannear) un usuario (no aplica al administrador)
    public String cambiarEstadoUsuario(String usuario) {
        if (usuario.equals("admin")) {
            return "No se puede bloquear al administrador.";
        }

        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario)) {
                u.setActivo(!u.isActivo());
                return u.isActivo()
                        ? "Usuario desbloqueado correctamente."
                        : "Usuario bloqueado correctamente.";
            }
        }

        return "Usuario no encontrado.";
    }

    // Registrar buscador
    public boolean registrarBuscador(Buscador b) {
        if (existeUsuario(b.getUsuario())) {
            return false;
        }

        usuarios.add(b);
        return true;
    }

    // Registrar arrendador
    public boolean registrarArrendador(Arrendador a) {
        if (existeUsuario(a.getUsuario())) {
            return false;
        }

        usuarios.add(a);
        return true;
    }

    // Obtener apartamentos publicados por un arrendador especifico
    public List<Apartamento> getApartamentosDeArrendador(String usuarioArrendador) {
        List<Apartamento> lista = new ArrayList<>();

        for (Apartamento a : apartamentos) {
            if (a.getPropietario().equals(usuarioArrendador)) {
                lista.add(a);
            }
        }

        return lista;
    }

    // Obtener solicitudes de convivencia recibidas sobre los apartamentos de un arrendador
    public List<SolicitudConvivencia> getSolicitudesDeArrendador(String usuarioArrendador) {
        List<SolicitudConvivencia> lista = new ArrayList<>();

        for (SolicitudConvivencia s : solicitudesConvivencia) {
            if (s.getApartamento().getPropietario().equals(usuarioArrendador)) {
                lista.add(s);
            }
        }

        return lista;
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

    // Obtener apartamentos disponibles filtrados por las preferencias del buscador
    // (cualquier parametro en null significa que ese criterio no se aplica)
    public List<Apartamento> getDisponiblesFiltrados(String sector, Integer habitacionesMinimas,
                                                       Double presupuestoMaximo, Boolean soloConMascotas) {
        List<Apartamento> lista = new ArrayList<>();

        for (Apartamento a : getDisponibles()) {
            if (sector != null && !sector.trim().isEmpty()
                    && !a.getSector().toLowerCase().contains(sector.trim().toLowerCase())) {
                continue;
            }

            if (habitacionesMinimas != null && a.getHabitaciones() < habitacionesMinimas) {
                continue;
            }

            if (presupuestoMaximo != null && a.getPrecio() > presupuestoMaximo) {
                continue;
            }

            if (soloConMascotas != null && soloConMascotas && !a.isAceptaMascotas()) {
                continue;
            }

            lista.add(a);
        }

        return lista;
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

    // Editar los datos de un apartamento
    public String editarApartamento(int id, String direccion, String sector,
                                     double precio, int habitaciones, boolean aceptaMascotas) {
        Apartamento apt = buscarApartamento(id);

        if (apt == null) {
            return "Apartamento no encontrado.";
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            return "La direccion es obligatoria.";
        }

        if (sector == null || sector.trim().isEmpty()) {
            return "El sector es obligatorio.";
        }

        if (precio <= 0) {
            return "El precio debe ser mayor a 0.";
        }

        if (habitaciones <= 0) {
            return "Debe haber al menos 1 habitacion.";
        }

        apt.setDireccion(direccion);
        apt.setSector(sector);
        apt.setPrecio(precio);
        apt.setHabitaciones(habitaciones);
        apt.setAceptaMascotas(aceptaMascotas);

        return "Apartamento actualizado correctamente.";
    }

    // Editar las preferencias propias de un buscador
    public String editarPreferenciasBuscador(String usuario, double presupuesto, String ocupacion,
                                              String horario, boolean mascotas, String ruido, String orden) {

        if (presupuesto <= 0) {
            return "El presupuesto debe ser mayor a 0.";
        }

        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usuario) && u instanceof Buscador) {
                Buscador b = (Buscador) u;
                b.setPresupuesto(presupuesto);
                b.setOcupacion(ocupacion);
                b.setHorario(horario);
                b.setMascotas(mascotas);
                b.setRuido(ruido);
                b.setOrden(orden);
                return "Preferencias actualizadas correctamente.";
            }
        }

        return "Buscador no encontrado.";
    }

    // Dar de baja la publicacion de un apartamento (independiente de su vigencia)
    public String darDeBajaApartamento(int id) {
        Apartamento apt = buscarApartamento(id);

        if (apt == null) {
            return "Apartamento no encontrado.";
        }

        if (apt.getEstado().equals("INACTIVO")) {
            return "El apartamento ya esta dado de baja.";
        }

        apt.setEstado("INACTIVO");
        return "Apartamento dado de baja correctamente.";
    }

    // Volver a habilitar un apartamento que estaba dado de baja
    public String habilitarApartamento(int id) {
        Apartamento apt = buscarApartamento(id);

        if (apt == null) {
            return "Apartamento no encontrado.";
        }

        if (!apt.getEstado().equals("INACTIVO")) {
            return "El apartamento no esta dado de baja.";
        }

        apt.setEstado("DISPONIBLE");
        return "Apartamento habilitado correctamente.";
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

    // Editar una solicitud de busqueda propia (solo si esta activa)
    public String editarSolicitudBusqueda(int id, String usuario, String sectorDeseado,
                                          double presupuestoMaximo, int habitacionesNecesarias,
                                          boolean aceptaMascotas, String horarioPreferido) {

        for (SolicitudBusqueda s : solicitudesBusqueda) {
            if (s.getId() == id && s.getBuscador().getUsuario().equals(usuario)) {

                if (!s.getEstado().equals("ACTIVA")) {
                    return "Solo se pueden editar solicitudes activas.";
                }

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

                s.setSectorDeseado(sectorDeseado);
                s.setPresupuestoMaximo(presupuestoMaximo);
                s.setHabitacionesNecesarias(habitacionesNecesarias);
                s.setAceptaMascotas(aceptaMascotas);
                s.setHorarioPreferido(horarioPreferido);

                return "Solicitud de busqueda actualizada correctamente.";
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