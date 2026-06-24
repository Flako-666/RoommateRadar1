package interfaz;

import modelo.Apartamento;
import modelo.Buscador;
import modelo.SolicitudBusqueda;
import modelo.SolicitudConvivencia;
import modelo.Usuario;
import negocio.SistemaRoommateRadar;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsolaRoommateRadar {

    private SistemaRoommateRadar sistema;
    private Scanner sc;

    public ConsolaRoommateRadar() {
        sistema = new SistemaRoommateRadar();
        sc = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("=== RoommateRadar ===");

        boolean correr = true;

        while (correr) {
            System.out.println("\n1. Iniciar sesion");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");
            System.out.print("Opcion: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    login();
                    break;
                case "2":
                    registro();
                    break;
                case "3":
                    correr = false;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }

        System.out.println("Hasta luego.");
        sc.close();
    }

    // Iniciar sesion
    private void login() {
        System.out.println("\n--- Iniciar sesion ---");

        System.out.print("Usuario: ");
        String usuario = sc.nextLine().trim();

        System.out.print("Clave: ");
        String clave = sc.nextLine().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            System.out.println("Error: todos los campos son obligatorios.");
            return;
        }

        Usuario u = sistema.login(usuario, clave);

        if (u == null) {
            System.out.println("Error: usuario o clave incorrectos.");
            return;
        }

        System.out.println("Bienvenido, " + u.getNombre());

        if (u.getRol().equals("ADMINISTRADOR")) {
            menuAdmin();
        } else {
            menuBuscador((Buscador) u);
        }
    }

    // Registro de buscador
    private void registro() {
        System.out.println("\n--- Registro de buscador ---");

        System.out.print("Usuario: ");
        String usuario = sc.nextLine().trim();
        if (usuario.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        System.out.print("Clave: ");
        String clave = sc.nextLine().trim();
        if (clave.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        System.out.print("Telefono: ");
        String tel = sc.nextLine().trim();
        if (tel.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        System.out.print("Correo: ");
        String correo = sc.nextLine().trim();
        if (correo.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        double presupuesto;

        System.out.print("Presupuesto mensual ($): ");
        try {
            presupuesto = Double.parseDouble(sc.nextLine().trim());

            if (presupuesto <= 0) {
                System.out.println("Error: debe ser mayor a 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
            return;
        }

        System.out.println("Ocupacion: 1) Estudiante  2) Profesional");
        System.out.print("Opcion: ");
        String opOcup = sc.nextLine().trim();

        String ocupacion;

        if (opOcup.equals("1")) {
            ocupacion = "Estudiante";
        } else if (opOcup.equals("2")) {
            ocupacion = "Profesional";
        } else {
            System.out.println("Error: opcion invalida.");
            return;
        }

        System.out.println("Horario: 1) Diurno  2) Nocturno");
        System.out.print("Opcion: ");
        String opHor = sc.nextLine().trim();

        String horario;

        if (opHor.equals("1")) {
            horario = "Diurno";
        } else if (opHor.equals("2")) {
            horario = "Nocturno";
        } else {
            System.out.println("Error: opcion invalida.");
            return;
        }

        System.out.print("Tiene mascotas? (s/n): ");
        String opMasc = sc.nextLine().trim().toLowerCase();

        if (!opMasc.equals("s") && !opMasc.equals("n")) {
            System.out.println("Error: ingrese s o n.");
            return;
        }

        boolean mascotas = opMasc.equals("s");

        System.out.println("Nivel de ruido: 1) Bajo  2) Medio  3) Alto");
        System.out.print("Opcion: ");

        String[] nivelesRuido = {"Bajo", "Medio", "Alto"};
        int iRuido;

        try {
            iRuido = Integer.parseInt(sc.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            iRuido = -1;
        }

        if (iRuido < 0 || iRuido > 2) {
            System.out.println("Error: opcion invalida.");
            return;
        }

        System.out.println("Nivel de orden: 1) Ordenado  2) Intermedio  3) Relajado");
        System.out.print("Opcion: ");

        String[] nivelesOrden = {"Ordenado", "Intermedio", "Relajado"};
        int iOrden;

        try {
            iOrden = Integer.parseInt(sc.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            iOrden = -1;
        }

        if (iOrden < 0 || iOrden > 2) {
            System.out.println("Error: opcion invalida.");
            return;
        }

        Buscador b = new Buscador(
                usuario,
                clave,
                nombre,
                tel,
                correo,
                presupuesto,
                ocupacion,
                horario,
                mascotas,
                nivelesRuido[iRuido],
                nivelesOrden[iOrden]
        );

        if (sistema.registrarBuscador(b)) {
            System.out.println("Registro exitoso. Ya puede iniciar sesion.");
        } else {
            System.out.println("Error: el usuario '" + usuario + "' ya existe.");
        }
    }

    // Menu administrador
    private void menuAdmin() {
        boolean correr = true;

        while (correr) {
            System.out.println("\n--- Menu Administrador ---");
            System.out.println("1. Registrar apartamento");
            System.out.println("2. Ver apartamentos disponibles");
            System.out.println("3. Ver todos los apartamentos");
            System.out.println("4. Ver solicitudes de convivencia");
            System.out.println("5. Aceptar solicitud de convivencia");
            System.out.println("6. Rechazar solicitud de convivencia");
            System.out.println("7. Ver solicitudes de busqueda");
            System.out.println("8. Renovar publicacion vencida");
            System.out.println("9. Cerrar sesion");
            System.out.print("Opcion: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    registrarApartamento();
                    break;
                case "2":
                    verApartamentos();
                    break;
                case "3":
                    verTodosLosApartamentosAdmin();
                    break;
                case "4":
                    verSolicitudesConvivencia();
                    break;
                case "5":
                    aceptarSolicitud();
                    break;
                case "6":
                    rechazarSolicitud();
                    break;
                case "7":
                    verSolicitudesBusquedaAdmin();
                    break;
                case "8":
                    renovarPublicacion();
                    break;
                case "9":
                    sistema.cerrarSesion();
                    correr = false;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    // Menu buscador
    private void menuBuscador(Buscador b) {
        boolean correr = true;

        while (correr) {
            System.out.println("\n--- Menu Buscador ---");
            System.out.println("1. Ver apartamentos disponibles");
            System.out.println("2. Enviar solicitud de convivencia");
            System.out.println("3. Mis solicitudes de convivencia");
            System.out.println("4. Registrar solicitud de busqueda");
            System.out.println("5. Mis solicitudes de busqueda");
            System.out.println("6. Cancelar solicitud de busqueda");
            System.out.println("7. Cerrar sesion");
            System.out.print("Opcion: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    verApartamentos();
                    break;
                case "2":
                    enviarSolicitud(b);
                    break;
                case "3":
                    verMisSolicitudesConvivencia(b);
                    break;
                case "4":
                    registrarSolicitudBusqueda(b);
                    break;
                case "5":
                    verMisSolicitudesBusqueda(b);
                    break;
                case "6":
                    cancelarSolicitudBusqueda(b);
                    break;
                case "7":
                    sistema.cerrarSesion();
                    correr = false;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    // Registrar apartamento
    private void registrarApartamento() {
        System.out.println("\n--- Registrar apartamento ---");

        System.out.print("Direccion: ");
        String dir = sc.nextLine().trim();

        if (dir.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        System.out.print("Sector: ");
        String sector = sc.nextLine().trim();

        if (sector.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        double precio;

        System.out.print("Precio mensual ($): ");
        try {
            precio = Double.parseDouble(sc.nextLine().trim());

            if (precio <= 0) {
                System.out.println("Error: debe ser mayor a 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
            return;
        }

        int habitaciones;

        System.out.print("Habitaciones disponibles: ");
        try {
            habitaciones = Integer.parseInt(sc.nextLine().trim());

            if (habitaciones <= 0) {
                System.out.println("Error: debe ser al menos 1.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero entero.");
            return;
        }

        Apartamento apt = new Apartamento(
                0,
                dir,
                sector,
                precio,
                habitaciones,
                sistema.getActivo().getUsuario()
        );

        sistema.registrarApartamento(apt);

        System.out.println("Apartamento registrado correctamente. ID: " + apt.getId());
        System.out.println("Fecha de publicacion: " + apt.getFechaPublicacion());
        System.out.println("Fecha de expiracion automatica: " + apt.getFechaExpiracion());
    }

    // Ver apartamentos disponibles
    private boolean verApartamentos() {
        List<Apartamento> lista = sistema.getDisponibles();

        if (lista.isEmpty()) {
            System.out.println("No hay apartamentos disponibles con publicacion vigente.");
            return false;
        }

        System.out.println("\n--- Apartamentos disponibles ---");

        for (Apartamento a : lista) {
            System.out.printf(
                    "ID: %d | %s, %s | $%.2f/mes | %d habitacion(es) | Publicado: %s | Expira: %s%n",
                    a.getId(),
                    a.getDireccion(),
                    a.getSector(),
                    a.getPrecio(),
                    a.getHabitaciones(),
                    a.getFechaPublicacion(),
                    a.getFechaExpiracion()
            );
        }

        return true;
    }

    // Enviar solicitud de convivencia
    private void enviarSolicitud(Buscador b) {
        boolean hayApartamentos = verApartamentos();

        if (!hayApartamentos) {
            return;
        }

        System.out.print("ID del apartamento: ");

        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.println(sistema.enviarSolicitud(b, id));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }

    // Aceptar solicitud de convivencia
    private void aceptarSolicitud() {
        boolean haySolicitudes = verSolicitudesConvivencia();

        if (!haySolicitudes) {
            return;
        }

        System.out.print("ID de la solicitud a aceptar: ");

        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.println(sistema.aceptarSolicitud(id));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }

    // Rechazar solicitud de convivencia
    private void rechazarSolicitud() {
        boolean haySolicitudes = verSolicitudesConvivencia();

        if (!haySolicitudes) {
            return;
        }

        System.out.print("ID de la solicitud a rechazar: ");

        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.println(sistema.rechazarSolicitud(id));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }

    // Ver todas las solicitudes de convivencia
    private boolean verSolicitudesConvivencia() {
        List<SolicitudConvivencia> lista = sistema.getSolicitudes();

        if (lista.isEmpty()) {
            System.out.println("No hay solicitudes de convivencia registradas.");
            return false;
        }

        System.out.println("\n--- Solicitudes de convivencia ---");

        for (SolicitudConvivencia s : lista) {
            System.out.printf(
                    "ID: %d | Buscador: %s | Apartamento: %s, %s | Fecha: %s | Estado: %s%n",
                    s.getId(),
                    s.getBuscador().getNombre(),
                    s.getApartamento().getDireccion(),
                    s.getApartamento().getSector(),
                    s.getFecha(),
                    s.getEstado()
            );
        }

        return true;
    }

    // Ver solicitudes de convivencia del buscador activo
    private void verMisSolicitudesConvivencia(Buscador b) {
        List<SolicitudConvivencia> lista = sistema.getSolicitudesDeUsuario(b.getUsuario());

        if (lista.isEmpty()) {
            System.out.println("No tienes solicitudes de convivencia registradas.");
            return;
        }

        System.out.println("\n--- Mis solicitudes de convivencia ---");

        for (SolicitudConvivencia s : lista) {
            System.out.printf(
                    "ID: %d | Apartamento: %s, %s | Fecha: %s | Estado: %s%n",
                    s.getId(),
                    s.getApartamento().getDireccion(),
                    s.getApartamento().getSector(),
                    s.getFecha(),
                    s.getEstado()
            );
        }
    }

    // Registrar solicitud de busqueda
    private void registrarSolicitudBusqueda(Buscador b) {
        System.out.println("\n--- Registrar solicitud de busqueda ---");

        System.out.print("Sector deseado: ");
        String sectorDeseado = sc.nextLine().trim();

        if (sectorDeseado.isEmpty()) {
            System.out.println("Error: campo obligatorio.");
            return;
        }

        double presupuestoMaximo;

        System.out.print("Presupuesto maximo ($): ");
        try {
            presupuestoMaximo = Double.parseDouble(sc.nextLine().trim());

            if (presupuestoMaximo <= 0) {
                System.out.println("Error: debe ser mayor a 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
            return;
        }

        int habitacionesNecesarias;

        System.out.print("Habitaciones necesarias: ");
        try {
            habitacionesNecesarias = Integer.parseInt(sc.nextLine().trim());

            if (habitacionesNecesarias <= 0) {
                System.out.println("Error: debe ser al menos 1.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero entero.");
            return;
        }

        System.out.print("Acepta mascotas? (s/n): ");
        String opMasc = sc.nextLine().trim().toLowerCase();

        if (!opMasc.equals("s") && !opMasc.equals("n")) {
            System.out.println("Error: ingrese s o n.");
            return;
        }

        boolean aceptaMascotas = opMasc.equals("s");

        System.out.println("Horario preferido: 1) Diurno  2) Nocturno");
        System.out.print("Opcion: ");
        String opHorario = sc.nextLine().trim();

        String horarioPreferido;

        if (opHorario.equals("1")) {
            horarioPreferido = "Diurno";
        } else if (opHorario.equals("2")) {
            horarioPreferido = "Nocturno";
        } else {
            System.out.println("Error: opcion invalida.");
            return;
        }

        String resultado = sistema.registrarSolicitudBusqueda(
                b,
                sectorDeseado,
                presupuestoMaximo,
                habitacionesNecesarias,
                aceptaMascotas,
                horarioPreferido
        );

        System.out.println(resultado);
    }

    // Ver solicitudes de busqueda del administrador
    private void verSolicitudesBusquedaAdmin() {
        List<SolicitudBusqueda> lista = sistema.getSolicitudesBusqueda();

        if (lista.isEmpty()) {
            System.out.println("No hay solicitudes de busqueda registradas.");
            return;
        }

        System.out.println("\n--- Solicitudes de busqueda ---");

        for (SolicitudBusqueda s : lista) {
            System.out.printf(
                    "ID: %d | Buscador: %s | Sector: %s | Presupuesto max: $%.2f | Habitaciones: %d | Mascotas: %s | Horario: %s | Fecha: %s | Estado: %s%n",
                    s.getId(),
                    s.getBuscador().getNombre(),
                    s.getSectorDeseado(),
                    s.getPresupuestoMaximo(),
                    s.getHabitacionesNecesarias(),
                    s.isAceptaMascotas() ? "Si" : "No",
                    s.getHorarioPreferido(),
                    s.getFechaCreacion(),
                    s.getEstado()
            );
        }
    }

    // Ver solicitudes de busqueda del buscador activo
    private void verMisSolicitudesBusqueda(Buscador b) {
        List<SolicitudBusqueda> lista = sistema.getSolicitudesBusquedaDeUsuario(b.getUsuario());

        if (lista.isEmpty()) {
            System.out.println("No tienes solicitudes de busqueda registradas.");
            return;
        }

        System.out.println("\n--- Mis solicitudes de busqueda ---");

        for (SolicitudBusqueda s : lista) {
            System.out.printf(
                    "ID: %d | Sector: %s | Presupuesto max: $%.2f | Habitaciones: %d | Mascotas: %s | Horario: %s | Fecha: %s | Estado: %s%n",
                    s.getId(),
                    s.getSectorDeseado(),
                    s.getPresupuestoMaximo(),
                    s.getHabitacionesNecesarias(),
                    s.isAceptaMascotas() ? "Si" : "No",
                    s.getHorarioPreferido(),
                    s.getFechaCreacion(),
                    s.getEstado()
            );
        }
    }

    // Cancelar solicitud de busqueda
    private void cancelarSolicitudBusqueda(Buscador b) {
        verMisSolicitudesBusqueda(b);

        System.out.print("ID de la solicitud de busqueda a cancelar: ");

        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.println(sistema.cancelarSolicitudBusqueda(id, b.getUsuario()));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }
    private void verTodosLosApartamentosAdmin() {
        List<Apartamento> lista = sistema.getApartamentos();

        if (lista.isEmpty()) {
            System.out.println("No hay apartamentos registrados.");
            return;
        }

        System.out.println("\n--- Todos los apartamentos registrados ---");

        for (Apartamento a : lista) {
            String estadoPublicacion;

            if (a.estaPublicacionVencida()) {
                estadoPublicacion = "VENCIDA";
            } else {
                estadoPublicacion = "VIGENTE";
            }

            System.out.printf(
                    "ID: %d | %s, %s | $%.2f/mes | %d habitacion(es) | Estado: %s | Publicacion: %s | Publicado: %s | Expira: %s%n",
                    a.getId(),
                    a.getDireccion(),
                    a.getSector(),
                    a.getPrecio(),
                    a.getHabitaciones(),
                    a.getEstado(),
                    estadoPublicacion,
                    a.getFechaPublicacion(),
                    a.getFechaExpiracion()
            );
        }
    }

    private void renovarPublicacion() {
        verTodosLosApartamentosAdmin();

        System.out.print("ID del apartamento a renovar: ");

        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.println(sistema.renovarPublicacionApartamento(id));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }
}