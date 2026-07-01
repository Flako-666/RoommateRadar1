package interfaz;

import modelo.Apartamento;
import modelo.Arrendador;
import modelo.Buscador;
import modelo.SolicitudConvivencia;
import negocio.SistemaRoommateRadar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelArrendador extends JPanel {

    private final VentanaPrincipal ventana;
    private final SistemaRoommateRadar sistema;

    private Arrendador activo;

    private final JLabel lblBienvenida;

    private final DefaultTableModel modeloMisApartamentos;
    private final DefaultTableModel modeloSolicitudesRecibidas;

    private static final String CUALQUIERA = "Cualquiera";

    private JComboBox<String> cmbFiltroOcupacion;
    private JComboBox<String> cmbFiltroHorario;
    private JComboBox<String> cmbFiltroMascotas;
    private JComboBox<String> cmbFiltroRuido;
    private JComboBox<String> cmbFiltroOrden;

    public PanelArrendador(VentanaPrincipal ventana, SistemaRoommateRadar sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout());

        lblBienvenida = new JLabel("", SwingConstants.CENTER);
        lblBienvenida.setFont(lblBienvenida.getFont().deriveFont(Font.BOLD, 18f));
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(lblBienvenida, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Registrar apartamento", crearPanelRegistrarApartamento());

        modeloMisApartamentos = new DefaultTableModel(
                new Object[]{"ID", "Direccion", "Sector", "Precio", "Habitaciones","Mascotas", "Estado", "Publicacion", "Publicado", "Expira"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Mis apartamentos", crearPanelMisApartamentos());

        modeloSolicitudesRecibidas = new DefaultTableModel(
                new Object[]{"ID", "Buscador", "Apartamento", "Sector", "Fecha", "Estado",
                        "Presupuesto", "Ocupacion", "Horario", "Mascotas", "Ruido", "Orden"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Solicitudes recibidas", crearPanelSolicitudesRecibidas());

        add(tabs, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar sesion");
        btnCerrarSesion.addActionListener(e -> ventana.cerrarSesion());

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inferior.add(btnCerrarSesion);
        add(inferior, BorderLayout.SOUTH);
    }

    public void iniciarSesionComo(Arrendador a) {
        this.activo = a;
        lblBienvenida.setText("Bienvenido, " + a.getNombre());
        refrescarTodo();
    }

    // ---------- Registrar apartamento propio ----------

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
                    activo.getUsuario(), chkMascotas.isSelected());
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

    // ---------- Mis apartamentos + renovar ----------

    private JPanel crearPanelMisApartamentos() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable tabla = new JTable(modeloMisApartamentos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEditar = new JButton("Editar seleccionado");
        JButton btnRenovar = new JButton("Renovar publicacion vencida");
        JButton btnBaja = new JButton("Dar de baja");
        JButton btnHabilitar = new JButton("Habilitar");

        btnActualizar.addActionListener(e -> refrescarMisApartamentos());

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un apartamento de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloMisApartamentos.getValueAt(fila, 0);
            editarApartamentoDialog(id);
        });

        btnRenovar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un apartamento de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloMisApartamentos.getValueAt(fila, 0);
            String resultado = sistema.renovarPublicacionApartamento(id);
            JOptionPane.showMessageDialog(this, resultado);
            refrescarMisApartamentos();
        });

        btnBaja.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un apartamento de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloMisApartamentos.getValueAt(fila, 0);
            String resultado = sistema.darDeBajaApartamento(id);
            JOptionPane.showMessageDialog(this, resultado);
            refrescarMisApartamentos();
        });

        btnHabilitar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un apartamento de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloMisApartamentos.getValueAt(fila, 0);
            String resultado = sistema.habilitarApartamento(id);
            JOptionPane.showMessageDialog(this, resultado);
            refrescarMisApartamentos();
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        sur.add(btnEditar);
        sur.add(btnRenovar);
        sur.add(btnBaja);
        sur.add(btnHabilitar);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    private void editarApartamentoDialog(int id) {
        Apartamento apt = sistema.buscarApartamento(id);
        if (apt == null) {
            JOptionPane.showMessageDialog(this, "Apartamento no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField txtDireccion = new JTextField(apt.getDireccion(), 20);
        JTextField txtSector = new JTextField(apt.getSector(), 20);
        JTextField txtPrecio = new JTextField(String.valueOf(apt.getPrecio()), 20);
        JTextField txtHabitaciones = new JTextField(String.valueOf(apt.getHabitaciones()), 20);
        JCheckBox chkMascotas = new JCheckBox("Acepta mascotas", apt.isAceptaMascotas());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

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

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Habitaciones:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtHabitaciones, c);
        fila++;

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2; panel.add(chkMascotas, c);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Editar apartamento",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        double precio;
        int habitaciones;

        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un precio valido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero entero de habitaciones.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String resultado = sistema.editarApartamento(id, txtDireccion.getText().trim(),
                txtSector.getText().trim(), precio, habitaciones, chkMascotas.isSelected());

        JOptionPane.showMessageDialog(this, resultado);
        refrescarMisApartamentos();
    }

    // ---------- Solicitudes recibidas + aceptar/rechazar ----------

    private JPanel crearPanelSolicitudesRecibidas() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtrar por intereses del buscador"));

        cmbFiltroOcupacion = new JComboBox<>(new String[]{CUALQUIERA, "Estudiante", "Profesional"});
        cmbFiltroHorario = new JComboBox<>(new String[]{CUALQUIERA, "Diurno", "Nocturno"});
        cmbFiltroMascotas = new JComboBox<>(new String[]{CUALQUIERA, "Si", "No"});
        cmbFiltroRuido = new JComboBox<>(new String[]{CUALQUIERA, "Bajo", "Medio", "Alto"});
        cmbFiltroOrden = new JComboBox<>(new String[]{CUALQUIERA, "Ordenado", "Intermedio", "Relajado"});

        filtros.add(new JLabel("Ocupacion:"));
        filtros.add(cmbFiltroOcupacion);
        filtros.add(new JLabel("Horario:"));
        filtros.add(cmbFiltroHorario);
        filtros.add(new JLabel("Mascotas:"));
        filtros.add(cmbFiltroMascotas);
        filtros.add(new JLabel("Ruido:"));
        filtros.add(cmbFiltroRuido);
        filtros.add(new JLabel("Orden:"));
        filtros.add(cmbFiltroOrden);

        java.awt.event.ActionListener aplicarFiltro = e -> refrescarSolicitudesRecibidas();
        cmbFiltroOcupacion.addActionListener(aplicarFiltro);
        cmbFiltroHorario.addActionListener(aplicarFiltro);
        cmbFiltroMascotas.addActionListener(aplicarFiltro);
        cmbFiltroRuido.addActionListener(aplicarFiltro);
        cmbFiltroOrden.addActionListener(aplicarFiltro);

        panel.add(filtros, BorderLayout.NORTH);

        JTable tabla = new JTable(modeloSolicitudesRecibidas);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnAceptar = new JButton("Aceptar seleccionada");
        JButton btnRechazar = new JButton("Rechazar seleccionada");

        btnActualizar.addActionListener(e -> refrescarSolicitudesRecibidas());

        btnAceptar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloSolicitudesRecibidas.getValueAt(fila, 0);
            JOptionPane.showMessageDialog(this, sistema.aceptarSolicitud(id));
            refrescarSolicitudesRecibidas();
        });

        btnRechazar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloSolicitudesRecibidas.getValueAt(fila, 0);
            JOptionPane.showMessageDialog(this, sistema.rechazarSolicitud(id));
            refrescarSolicitudesRecibidas();
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        sur.add(btnAceptar);
        sur.add(btnRechazar);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    // ---------- Refrescos ----------

    private void refrescarTodo() {
        refrescarMisApartamentos();
        refrescarSolicitudesRecibidas();
    }

    private void refrescarMisApartamentos() {
        if (activo == null) return;

        modeloMisApartamentos.setRowCount(0);
        List<Apartamento> lista = sistema.getApartamentosDeArrendador(activo.getUsuario());

        for (Apartamento a : lista) {
            String estadoPublicacion = a.estaPublicacionVencida() ? "VENCIDA" : "VIGENTE";

            modeloMisApartamentos.addRow(new Object[]{
                    a.getId(), a.getDireccion(), a.getSector(),
                    String.format("$%.2f", a.getPrecio()), a.getHabitaciones(),
                    a.isAceptaMascotas() ? "Si" : "No",
                    a.getEstado(), estadoPublicacion,
                    a.getFechaPublicacion(), a.getFechaExpiracion()
            });
        }
    }

    private void refrescarSolicitudesRecibidas() {
        if (activo == null) return;

        modeloSolicitudesRecibidas.setRowCount(0);
        List<SolicitudConvivencia> lista = sistema.getSolicitudesDeArrendador(activo.getUsuario());

        String fOcupacion = (String) cmbFiltroOcupacion.getSelectedItem();
        String fHorario = (String) cmbFiltroHorario.getSelectedItem();
        String fMascotas = (String) cmbFiltroMascotas.getSelectedItem();
        String fRuido = (String) cmbFiltroRuido.getSelectedItem();
        String fOrden = (String) cmbFiltroOrden.getSelectedItem();

        for (SolicitudConvivencia s : lista) {
            Buscador b = s.getBuscador();

            if (!CUALQUIERA.equals(fOcupacion) && !b.getOcupacion().equals(fOcupacion)) continue;
            if (!CUALQUIERA.equals(fHorario) && !b.getHorario().equals(fHorario)) continue;
            if (!CUALQUIERA.equals(fMascotas) && !(b.isMascotas() ? "Si" : "No").equals(fMascotas)) continue;
            if (!CUALQUIERA.equals(fRuido) && !b.getRuido().equals(fRuido)) continue;
            if (!CUALQUIERA.equals(fOrden) && !b.getOrden().equals(fOrden)) continue;

            modeloSolicitudesRecibidas.addRow(new Object[]{
                    s.getId(), b.getNombre(),
                    s.getApartamento().getDireccion(), s.getApartamento().getSector(),
                    s.getFecha(), s.getEstado(),
                    String.format("$%.2f", b.getPresupuesto()), b.getOcupacion(), b.getHorario(),
                    b.isMascotas() ? "Si" : "No", b.getRuido(), b.getOrden()
            });
        }
    }
}
