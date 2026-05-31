package interfaz;

import modelo.Apartamento;
import modelo.Buscador;
import modelo.SolicitudConvivencia;
import modelo.Usuario;
import negocio.SistemaRoommateRadar;

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
            if (op.equals("1")) login();
            else if (op.equals("2")) registro();
            else if (op.equals("3")) correr = false;
            else System.out.println("Opcion invalida.");
        }
        System.out.println("Hasta luego.");
        sc.close();
    }

    // login
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
        if (u.getRol().equals("ADMINISTRADOR")) menuAdmin();
        else menuBuscador((Buscador) u);
    }

    // registro de buscador
    private void registro() {
        System.out.println("\n--- Registro ---");

        System.out.print("Usuario: ");
        String usuario = sc.nextLine().trim();
        if (usuario.isEmpty()) { System.out.println("Error: campo obligatorio."); return; }

        System.out.print("Clave: ");
        String clave = sc.nextLine().trim();
        if (clave.isEmpty()) { System.out.println("Error: campo obligatorio."); return; }

        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("Error: campo obligatorio."); return; }

        System.out.print("Telefono: ");
        String tel = sc.nextLine().trim();
        if (tel.isEmpty()) { System.out.println("Error: campo obligatorio."); return; }

        System.out.print("Correo: ");
        String correo = sc.nextLine().trim();
        if (correo.isEmpty()) { System.out.println("Error: campo obligatorio."); return; }

        double presupuesto;
        System.out.print("Presupuesto mensual ($): ");
        try {
            presupuesto = Double.parseDouble(sc.nextLine().trim());
            if (presupuesto <= 0) { System.out.println("Error: debe ser mayor a 0."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
            return;
        }

        System.out.println("Ocupacion: 1) Estudiante  2) Profesional");
        System.out.print("Opcion: ");
        String opOcup = sc.nextLine().trim();
        String ocupacion = opOcup.equals("1") ? "Estudiante" : opOcup.equals("2") ? "Profesional" : null;
        if (ocupacion == null) { System.out.println("Error: opcion invalida."); return; }

        System.out.println("Horario: 1) Diurno  2) Nocturno");
        System.out.print("Opcion: ");
        String opHor = sc.nextLine().trim();
        String horario = opHor.equals("1") ? "Diurno" : opHor.equals("2") ? "Nocturno" : null;
        if (horario == null) { System.out.println("Error: opcion invalida."); return; }

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
        try { iRuido = Integer.parseInt(sc.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { iRuido = -1; }
        if (iRuido < 0 || iRuido > 2) { System.out.println("Error: opcion invalida."); return; }

        System.out.println("Nivel de orden: 1) Ordenado  2) Intermedio  3) Relajado");
        System.out.print("Opcion: ");
        String[] nivelesOrden = {"Ordenado", "Intermedio", "Relajado"};
        int iOrden;
        try { iOrden = Integer.parseInt(sc.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { iOrden = -1; }
        if (iOrden < 0 || iOrden > 2) { System.out.println("Error: opcion invalida."); return; }

        Buscador b = new Buscador(usuario, clave, nombre, tel, correo,
                presupuesto, ocupacion, horario, mascotas,
                nivelesRuido[iRuido], nivelesOrden[iOrden]);

        if (sistema.registrarBuscador(b)) {
            System.out.println("Registro exitoso. Ya puede iniciar sesion.");
        } else {
            System.out.println("Error: el usuario '" + usuario + "' ya existe.");
        }
    }

    // menu admin
    private void menuAdmin() {
        boolean correr = true;
        while (correr) {
            System.out.println("\n--- Menu Administrador ---");
            System.out.println("1. Registrar apartamento");
            System.out.println("2. Ver apartamentos disponibles");
            System.out.println("3. Ver todas las solicitudes");
            System.out.println("4. Aceptar solicitud");
            System.out.println("5. Rechazar solicitud");
            System.out.println("6. Cerrar sesion");
            System.out.print("Opcion: ");
            switch (sc.nextLine().trim()) {
                case "1": registrarApartamento(); break;
                case "2": verApartamentos();      break;
                case "3": verSolicitudes();       break;
                case "4": aceptarSolicitud();     break;
                case "5": rechazarSolicitud();    break;
                case "6": sistema.cerrarSesion(); correr = false; break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    // menu buscador
    private void menuBuscador(Buscador b) {
        boolean correr = true;
        while (correr) {
            System.out.println("\n--- Menu Buscador ---");
            System.out.println("1. Ver apartamentos disponibles");
            System.out.println("2. Enviar solicitud");
            System.out.println("3. Mis solicitudes");
            System.out.println("4. Cerrar sesion");
            System.out.print("Opcion: ");
            switch (sc.nextLine().trim()) {
                case "1": verApartamentos();      break;
                case "2": enviarSolicitud(b);    break;
                case "3": verMisSolicitudes(b);  break;
                case "4": sistema.cerrarSesion(); correr = false; break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    // registrar apartamento
    private void registrarApartamento() {
        System.out.println("\n--- Registrar apartamento ---");

        System.out.print("Direccion: ");
        String dir = sc.nextLine().trim();
        if (dir.isEmpty()) { System.out.println("Error: campo obligatorio."); return; }

        System.out.print("Sector: ");
        String sector = sc.nextLine().trim();
        if (sector.isEmpty()) { System.out.println("Error: campo obligatorio."); return; }

        double precio;
        System.out.print("Precio mensual ($): ");
        try {
            precio = Double.parseDouble(sc.nextLine().trim());
            if (precio <= 0) { System.out.println("Error: debe ser mayor a 0."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
            return;
        }

        int habitaciones;
        System.out.print("Habitaciones disponibles: ");
        try {
            habitaciones = Integer.parseInt(sc.nextLine().trim());
            if (habitaciones <= 0) { System.out.println("Error: debe ser al menos 1."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero entero.");
            return;
        }

        Apartamento apt = new Apartamento(0, dir, sector, precio,
                habitaciones, sistema.getActivo().getUsuario());
        sistema.registrarApartamento(apt);
        System.out.println("Apartamento registrado. ID: " + apt.getId());
    }

    // ver apartamentos disponibles
    private void verApartamentos() {
        List<Apartamento> lista = sistema.getDisponibles();
        if (lista.isEmpty()) {
            System.out.println("No hay apartamentos disponibles.");
            return;
        }
        System.out.println("\n--- Apartamentos disponibles ---");
        for (Apartamento a : lista) {
            System.out.printf("ID: %d | %s, %s | $%.2f/mes | %d habitacion(es)%n",
                    a.getId(), a.getDireccion(), a.getSector(),
                    a.getPrecio(), a.getHabitaciones());
        }
    }

    // enviar solicitud
    private void enviarSolicitud(Buscador b) {
        verApartamentos();
        System.out.print("ID del apartamento: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.println(sistema.enviarSolicitud(b, id));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }

    // aceptar solicitud
    private void aceptarSolicitud() {
        verSolicitudes();
        System.out.print("ID de la solicitud a aceptar: ");
        try {
            System.out.println(sistema.aceptarSolicitud(Integer.parseInt(sc.nextLine().trim())));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }

    // rechazar solicitud
    private void rechazarSolicitud() {
        verSolicitudes();
        System.out.print("ID de la solicitud a rechazar: ");
        try {
            System.out.println(sistema.rechazarSolicitud(Integer.parseInt(sc.nextLine().trim())));
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero valido.");
        }
    }

    // todas las solicitudes (admin)
    private void verSolicitudes() {
        List<SolicitudConvivencia> lista = sistema.getSolicitudes();
        if (lista.isEmpty()) {
            System.out.println("No hay solicitudes registradas.");
            return;
        }
        System.out.println("\n--- Solicitudes ---");
        for (SolicitudConvivencia s : lista) {
            System.out.printf("ID: %d | %s | %s, %s | %s | %s%n",
                    s.getId(), s.getBuscador().getNombre(),
                    s.getApartamento().getDireccion(), s.getApartamento().getSector(),
                    s.getFecha(), s.getEstado());
        }
    }

    // solicitudes del buscador activo
    private void verMisSolicitudes(Buscador b) {
        List<SolicitudConvivencia> lista = sistema.getSolicitudesDeUsuario(b.getUsuario());
        if (lista.isEmpty()) {
            System.out.println("No tienes solicitudes registradas.");
            return;
        }
        System.out.println("\n--- Mis solicitudes ---");
        for (SolicitudConvivencia s : lista) {
            System.out.printf("ID: %d | %s, %s | %s%n",
                    s.getId(), s.getApartamento().getDireccion(),
                    s.getApartamento().getSector(), s.getEstado());
        }
    }
}
