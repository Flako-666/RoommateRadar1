package interfaz;

import modelo.Apartamento;
import modelo.SolicitudBusqueda;
import modelo.SolicitudConvivencia;
import modelo.Usuario;
import negocio.SistemaRoommateRadar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelAdmin extends JPanel {

    private final VentanaPrincipal ventana;
    private final SistemaRoommateRadar sistema;

    private final DefaultTableModel modeloDisponibles;
    private final DefaultTableModel modeloTodos;
    private final DefaultTableModel modeloSolicitudesConvivencia;
    private final DefaultTableModel modeloSolicitudesBusqueda;
    private final DefaultTableModel modeloUsuarios;

    public PanelAdmin(VentanaPrincipal ventana, SistemaRoommateRadar sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Panel de Administrador", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // --- Tab: Apartamentos disponibles ---
        modeloDisponibles = new DefaultTableModel(
                new Object[]{"ID", "Direccion", "Sector", "Precio", "Habitaciones", "Mascotas", "Publicado", "Expira"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Apartamentos disponibles", crearPanelTabla(modeloDisponibles, this::refrescarDisponibles));

        // --- Tab: Todos los apartamentos ---
        modeloTodos = new DefaultTableModel(
                new Object[]{"ID", "Direccion", "Sector", "Precio", "Habitaciones", "Mascotas", "Propietario", "Estado", "Publicacion", "Publicado", "Expira"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Todos los apartamentos", crearPanelTodosApartamentos());

        // --- Tab: Solicitudes de convivencia (solo lectura, las gestiona el arrendador) ---
        modeloSolicitudesConvivencia = new DefaultTableModel(
                new Object[]{"ID", "Buscador", "Apartamento", "Sector", "Fecha", "Estado"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Solicitudes de convivencia", crearPanelTabla(modeloSolicitudesConvivencia, this::refrescarSolicitudesConvivencia));

        // --- Tab: Solicitudes de busqueda ---
        modeloSolicitudesBusqueda = new DefaultTableModel(
                new Object[]{"ID", "Buscador", "Sector", "Presup. max", "Habitaciones", "Mascotas", "Horario", "Fecha", "Estado"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Solicitudes de busqueda", crearPanelTabla(modeloSolicitudesBusqueda, this::refrescarSolicitudesBusqueda));

        // --- Tab: Gestion de usuarios ---
        modeloUsuarios = new DefaultTableModel(
                new Object[]{"Usuario", "Clave", "Nombre", "Rol", "Telefono", "Correo", "Estado"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Gestion de usuarios", crearPanelGestionUsuarios());

        add(tabs, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar sesion");
        btnCerrarSesion.addActionListener(e -> ventana.cerrarSesion());

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inferior.add(btnCerrarSesion);
        add(inferior, BorderLayout.SOUTH);
    }

    // ---------- Registrar apartamento ----------

    private JPanel crearPanelRegistrarApartamento() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtDireccion = new JTextField(20);
        JTextField txtSector = new JTextField(20);
        JTextField txtPrecio = new JTextField(20);
        JTextField txtHabitaciones = new JTextField(20);
        JCheckBox chkMascotas = new JCheckBox("Acepta mascotas");

        int fila = 0;
        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Direccion:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtDireccion, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Sector:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtSector, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Precio mensual ($):"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtPrecio, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Habitaciones disponibles:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtHabitaciones, c);
        fila++;

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2; panel.add(chkMascotas, c);
        c.gridwidth = 1;
        fila++;

        JButton btnRegistrar = new JButton("Registrar apartamento");
        c.gridx = 0; c.gridy = fila; c.gridwidth = 2;
        panel.add(btnRegistrar, c);

        btnRegistrar.addActionListener(e -> {
            String dir = txtDireccion.getText().trim();
            String sector = txtSector.getText().trim();

            if (dir.isEmpty() || sector.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Direccion y sector son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double precio;
            int habitaciones;

            try {
                precio = Double.parseDouble(txtPrecio.getText().trim());
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un precio valido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
                if (habitaciones <= 0) {
                    JOptionPane.showMessageDialog(this, "Debe haber al menos 1 habitacion.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un numero entero de habitaciones.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Apartamento apt = new Apartamento(0, dir, sector, precio, habitaciones,
                    sistema.getActivo().getUsuario(), chkMascotas.isSelected());
            sistema.registrarApartamento(apt);

            JOptionPane.showMessageDialog(this,
                    "Apartamento registrado. ID: " + apt.getId()
                            + "\nPublicado: " + apt.getFechaPublicacion()
                            + "\nExpira: " + apt.getFechaExpiracion());

            txtDireccion.setText("");
            txtSector.setText("");
            txtPrecio.setText("");
            txtHabitaciones.setText("");
            chkMascotas.setSelected(false);

            refrescarTodo();
        });

        return panel;
    }

    // ---------- Tabla generica con boton de actualizar ----------

    private JPanel crearPanelTabla(DefaultTableModel modelo, Runnable refrescar) {
        JPanel panel = new JPanel(new BorderLayout());

        JTable tabla = new JTable(modelo);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar.run());

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        panel.add(sur, BorderLayout.SOUTH);

        refrescar.run();

        return panel;
    }

    // ---------- Todos los apartamentos + renovar ----------

    private JPanel crearPanelTodosApartamentos() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable tabla = new JTable(modeloTodos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnRenovar = new JButton("Renovar publicacion vencida");

        btnActualizar.addActionListener(e -> refrescarTodos());

        btnRenovar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un apartamento de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id = (int) modeloTodos.getValueAt(fila, 0);
            String resultado = sistema.renovarPublicacionApartamento(id);
            JOptionPane.showMessageDialog(this, resultado);
            refrescarTodos();
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        sur.add(btnRenovar);
        panel.add(sur, BorderLayout.SOUTH);

        refrescarTodos();

        return panel;
    }

    // ---------- Gestion de usuarios (ver/eliminar/bannear) ----------

    private JPanel crearPanelGestionUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable tabla = new JTable(modeloUsuarios);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnBannear = new JButton("Bloquear/Desbloquear seleccionado");
        JButton btnEliminar = new JButton("Eliminar seleccionado");

        btnActualizar.addActionListener(e -> refrescarUsuarios());

        btnBannear.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String usuario = (String) modeloUsuarios.getValueAt(fila, 0);
            JOptionPane.showMessageDialog(this, sistema.cambiarEstadoUsuario(usuario));
            refrescarUsuarios();
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String usuario = (String) modeloUsuarios.getValueAt(fila, 0);

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que desea eliminar al usuario '" + usuario + "'?",
                    "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, sistema.eliminarUsuario(usuario));
                refrescarUsuarios();
            }
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        sur.add(btnBannear);
        sur.add(btnEliminar);
        panel.add(sur, BorderLayout.SOUTH);

        refrescarUsuarios();

        return panel;
    }

    // ---------- Refrescos ----------

    public void refrescarTodo() {
        refrescarDisponibles();
        refrescarTodos();
        refrescarSolicitudesConvivencia();
        refrescarSolicitudesBusqueda();
        refrescarUsuarios();
    }

    private void refrescarDisponibles() {
        modeloDisponibles.setRowCount(0);
        List<Apartamento> lista = sistema.getDisponibles();

        for (Apartamento a : lista) {
            modeloDisponibles.addRow(new Object[]{
                    a.getId(), a.getDireccion(), a.getSector(),
                    String.format("$%.2f", a.getPrecio()), a.getHabitaciones(),
                    a.isAceptaMascotas() ? "Si" : "No",
                    a.getFechaPublicacion(), a.getFechaExpiracion()
            });
        }
    }

    private void refrescarTodos() {
        modeloTodos.setRowCount(0);
        List<Apartamento> lista = sistema.getApartamentos();

        for (Apartamento a : lista) {
            String estadoPublicacion = a.estaPublicacionVencida() ? "VENCIDA" : "VIGENTE";

            modeloTodos.addRow(new Object[]{
                    a.getId(), a.getDireccion(), a.getSector(),
                    String.format("$%.2f", a.getPrecio()), a.getHabitaciones(),
                    a.isAceptaMascotas() ? "Si" : "No",
                    a.getPropietario(), a.getEstado(), estadoPublicacion,
                    a.getFechaPublicacion(), a.getFechaExpiracion()
            });
        }
    }

    private void refrescarSolicitudesConvivencia() {
        modeloSolicitudesConvivencia.setRowCount(0);
        List<SolicitudConvivencia> lista = sistema.getSolicitudes();

        for (SolicitudConvivencia s : lista) {
            modeloSolicitudesConvivencia.addRow(new Object[]{
                    s.getId(), s.getBuscador().getNombre(),
                    s.getApartamento().getDireccion(), s.getApartamento().getSector(),
                    s.getFecha(), s.getEstado()
            });
        }
    }

    private void refrescarSolicitudesBusqueda() {
        modeloSolicitudesBusqueda.setRowCount(0);
        List<SolicitudBusqueda> lista = sistema.getSolicitudesBusqueda();

        for (SolicitudBusqueda s : lista) {
            modeloSolicitudesBusqueda.addRow(new Object[]{
                    s.getId(), s.getBuscador().getNombre(), s.getSectorDeseado(),
                    String.format("$%.2f", s.getPresupuestoMaximo()), s.getHabitacionesNecesarias(),
                    s.isAceptaMascotas() ? "Si" : "No", s.getHorarioPreferido(),
                    s.getFechaCreacion(), s.getEstado()
            });
        }
    }

    private void refrescarUsuarios() {
        modeloUsuarios.setRowCount(0);
        List<Usuario> lista = sistema.getUsuarios();

        for (Usuario u : lista) {
            modeloUsuarios.addRow(new Object[]{
                    u.getUsuario(), u.getClave(), u.getNombre(), u.getRol(),
                    u.getTelefono(), u.getCorreo(),
                    u.isActivo() ? "ACTIVO" : "BLOQUEADO"
            });
        }
    }
}
