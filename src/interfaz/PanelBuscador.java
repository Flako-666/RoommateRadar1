package interfaz;

import modelo.Apartamento;
import modelo.Buscador;
import modelo.SolicitudBusqueda;
import modelo.SolicitudConvivencia;
import negocio.SistemaRoommateRadar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelBuscador extends JPanel {

    private final VentanaPrincipal ventana;
    private final SistemaRoommateRadar sistema;

    private Buscador activo;

    private JCheckBox chkFiltrarPresupuesto;
    private JCheckBox chkFiltrarMascotas;
    private JTextField txtFiltroSector;
    private JTextField txtFiltroHabitaciones;

    private JTextField txtPrefPresupuesto;
    private JComboBox<String> cmbPrefOcupacion;
    private JComboBox<String> cmbPrefHorario;
    private JCheckBox chkPrefMascotas;
    private JComboBox<String> cmbPrefRuido;
    private JComboBox<String> cmbPrefOrden;

    private final JLabel lblBienvenida;

    private final DefaultTableModel modeloDisponibles;
    private final DefaultTableModel modeloMisSolicitudesConvivencia;
    private final DefaultTableModel modeloMisSolicitudesBusqueda;

    public PanelBuscador(VentanaPrincipal ventana, SistemaRoommateRadar sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout());

        lblBienvenida = new JLabel("", SwingConstants.CENTER);
        lblBienvenida.setFont(lblBienvenida.getFont().deriveFont(Font.BOLD, 18f));
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(lblBienvenida, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        modeloDisponibles = new DefaultTableModel(
                new Object[]{"ID", "Direccion", "Sector", "Precio", "Habitaciones", "Mascotas", "Publicado", "Expira"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Apartamentos disponibles", crearPanelDisponibles());

        tabs.addTab("Mis preferencias", crearPanelPreferencias());

        modeloMisSolicitudesConvivencia = new DefaultTableModel(
                new Object[]{"ID", "Apartamento", "Sector", "Fecha", "Estado"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Mis solicitudes de convivencia", crearPanelTablaSimple(modeloMisSolicitudesConvivencia, this::refrescarMisSolicitudesConvivencia));

        tabs.addTab("Registrar solicitud de busqueda", crearPanelRegistrarSolicitudBusqueda());

        modeloMisSolicitudesBusqueda = new DefaultTableModel(
                new Object[]{"ID", "Sector", "Presup. max", "Habitaciones", "Mascotas", "Horario", "Fecha", "Estado"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabs.addTab("Mis solicitudes de busqueda", crearPanelMisSolicitudesBusqueda());

        add(tabs, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar sesion");
        btnCerrarSesion.addActionListener(e -> ventana.cerrarSesion());

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inferior.add(btnCerrarSesion);
        add(inferior, BorderLayout.SOUTH);
    }

    public void iniciarSesionComo(Buscador b) {
        this.activo = b;
        lblBienvenida.setText("Bienvenido, " + b.getNombre());
        cargarPreferencias();
        refrescarTodo();
    }

    // ---------- Apartamentos disponibles + enviar solicitud ----------

    private JPanel crearPanelDisponibles() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtrar por mis preferencias"));

        txtFiltroSector = new JTextField(10);
        txtFiltroHabitaciones = new JTextField(4);
        chkFiltrarPresupuesto = new JCheckBox("Solo dentro de mi presupuesto");
        chkFiltrarMascotas = new JCheckBox("Solo que acepten mascotas");

        filtros.add(new JLabel("Sector contiene:"));
        filtros.add(txtFiltroSector);
        filtros.add(new JLabel("Habitaciones minimas:"));
        filtros.add(txtFiltroHabitaciones);
        filtros.add(chkFiltrarPresupuesto);
        filtros.add(chkFiltrarMascotas);

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnLimpiarFiltros = new JButton("Limpiar filtros");
        filtros.add(btnFiltrar);
        filtros.add(btnLimpiarFiltros);

        btnFiltrar.addActionListener(e -> refrescarDisponibles());
        chkFiltrarPresupuesto.addActionListener(e -> refrescarDisponibles());
        chkFiltrarMascotas.addActionListener(e -> refrescarDisponibles());

        btnLimpiarFiltros.addActionListener(e -> {
            txtFiltroSector.setText("");
            txtFiltroHabitaciones.setText("");
            chkFiltrarPresupuesto.setSelected(false);
            chkFiltrarMascotas.setSelected(false);
            refrescarDisponibles();
        });

        panel.add(filtros, BorderLayout.NORTH);

        JTable tabla = new JTable(modeloDisponibles);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEnviar = new JButton("Enviar solicitud de convivencia");

        btnActualizar.addActionListener(e -> refrescarDisponibles());

        btnEnviar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un apartamento de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloDisponibles.getValueAt(fila, 0);
            String resultado = sistema.enviarSolicitud(activo, id);
            JOptionPane.showMessageDialog(this, resultado);
            refrescarDisponibles();
            refrescarMisSolicitudesConvivencia();
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        sur.add(btnEnviar);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    // ---------- Mis preferencias ----------

    private JPanel crearPanelPreferencias() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtPrefPresupuesto = new JTextField(18);
        cmbPrefOcupacion = new JComboBox<>(new String[]{"Estudiante", "Profesional"});
        cmbPrefHorario = new JComboBox<>(new String[]{"Diurno", "Nocturno"});
        chkPrefMascotas = new JCheckBox("Tiene mascotas");
        cmbPrefRuido = new JComboBox<>(new String[]{"Bajo", "Medio", "Alto"});
        cmbPrefOrden = new JComboBox<>(new String[]{"Ordenado", "Intermedio", "Relajado"});

        int fila = 0;
        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Presupuesto mensual ($):"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtPrefPresupuesto, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Ocupacion:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(cmbPrefOcupacion, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Horario preferido:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(cmbPrefHorario, c);
        fila++;

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2; panel.add(chkPrefMascotas, c);
        c.gridwidth = 1;
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Nivel de ruido:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(cmbPrefRuido, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Nivel de orden:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(cmbPrefOrden, c);
        fila++;

        JButton btnGuardar = new JButton("Guardar cambios");
        c.gridx = 0; c.gridy = fila; c.gridwidth = 2;
        panel.add(btnGuardar, c);

        btnGuardar.addActionListener(e -> {
            double presupuesto;

            try {
                presupuesto = Double.parseDouble(txtPrefPresupuesto.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un presupuesto valido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String resultado = sistema.editarPreferenciasBuscador(
                    activo.getUsuario(), presupuesto,
                    (String) cmbPrefOcupacion.getSelectedItem(),
                    (String) cmbPrefHorario.getSelectedItem(),
                    chkPrefMascotas.isSelected(),
                    (String) cmbPrefRuido.getSelectedItem(),
                    (String) cmbPrefOrden.getSelectedItem()
            );

            JOptionPane.showMessageDialog(this, resultado);
            refrescarDisponibles();
        });

        return panel;
    }

    private void cargarPreferencias() {
        if (activo == null) return;

        txtPrefPresupuesto.setText(String.valueOf(activo.getPresupuesto()));
        cmbPrefOcupacion.setSelectedItem(activo.getOcupacion());
        cmbPrefHorario.setSelectedItem(activo.getHorario());
        chkPrefMascotas.setSelected(activo.isMascotas());
        cmbPrefRuido.setSelectedItem(activo.getRuido());
        cmbPrefOrden.setSelectedItem(activo.getOrden());
    }

    private JPanel crearPanelTablaSimple(DefaultTableModel modelo, Runnable refrescar) {
        JPanel panel = new JPanel(new BorderLayout());

        JTable tabla = new JTable(modelo);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar.run());

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    // ---------- Registrar solicitud de busqueda ----------

    private JPanel crearPanelRegistrarSolicitudBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtSector = new JTextField(18);
        JTextField txtPresupuesto = new JTextField(18);
        JTextField txtHabitaciones = new JTextField(18);
        JCheckBox chkMascotas = new JCheckBox("Acepta mascotas");
        JComboBox<String> cmbHorario = new JComboBox<>(new String[]{"Diurno", "Nocturno"});

        int fila = 0;
        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Sector deseado:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtSector, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Presupuesto maximo ($):"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtPresupuesto, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Habitaciones necesarias:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtHabitaciones, c);
        fila++;

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2; panel.add(chkMascotas, c);
        c.gridwidth = 1;
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Horario preferido:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(cmbHorario, c);
        fila++;

        JButton btnRegistrar = new JButton("Registrar solicitud de busqueda");
        c.gridx = 0; c.gridy = fila; c.gridwidth = 2;
        panel.add(btnRegistrar, c);

        btnRegistrar.addActionListener(e -> {
            String sector = txtSector.getText().trim();

            if (sector.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El sector deseado es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double presupuesto;
            int habitaciones;

            try {
                presupuesto = Double.parseDouble(txtPresupuesto.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un presupuesto valido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un numero entero de habitaciones.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String resultado = sistema.registrarSolicitudBusqueda(
                    activo, sector, presupuesto, habitaciones,
                    chkMascotas.isSelected(), (String) cmbHorario.getSelectedItem()
            );

            JOptionPane.showMessageDialog(this, resultado);

            txtSector.setText("");
            txtPresupuesto.setText("");
            txtHabitaciones.setText("");
            chkMascotas.setSelected(false);
            cmbHorario.setSelectedIndex(0);

            refrescarMisSolicitudesBusqueda();
        });

        return panel;
    }

    // ---------- Mis solicitudes de busqueda + cancelar ----------

    private JPanel crearPanelMisSolicitudesBusqueda() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable tabla = new JTable(modeloMisSolicitudesBusqueda);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEditar = new JButton("Editar seleccionada");
        JButton btnCancelar = new JButton("Cancelar seleccionada");

        btnActualizar.addActionListener(e -> refrescarMisSolicitudesBusqueda());

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloMisSolicitudesBusqueda.getValueAt(fila, 0);
            editarSolicitudBusquedaDialog(id);
        });

        btnCancelar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (int) modeloMisSolicitudesBusqueda.getValueAt(fila, 0);
            String resultado = sistema.cancelarSolicitudBusqueda(id, activo.getUsuario());
            JOptionPane.showMessageDialog(this, resultado);
            refrescarMisSolicitudesBusqueda();
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnActualizar);
        sur.add(btnEditar);
        sur.add(btnCancelar);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    private void editarSolicitudBusquedaDialog(int id) {
        SolicitudBusqueda actual = null;
        for (SolicitudBusqueda s : sistema.getSolicitudesBusquedaDeUsuario(activo.getUsuario())) {
            if (s.getId() == id) {
                actual = s;
                break;
            }
        }

        if (actual == null) {
            JOptionPane.showMessageDialog(this, "Solicitud no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField txtSector = new JTextField(actual.getSectorDeseado(), 18);
        JTextField txtPresupuesto = new JTextField(String.valueOf(actual.getPresupuestoMaximo()), 18);
        JTextField txtHabitaciones = new JTextField(String.valueOf(actual.getHabitacionesNecesarias()), 18);
        JCheckBox chkMascotas = new JCheckBox("Acepta mascotas", actual.isAceptaMascotas());
        JComboBox<String> cmbHorario = new JComboBox<>(new String[]{"Diurno", "Nocturno"});
        cmbHorario.setSelectedItem(actual.getHorarioPreferido());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Sector deseado:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtSector, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Presupuesto maximo ($):"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtPresupuesto, c);
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Habitaciones necesarias:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(txtHabitaciones, c);
        fila++;

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2; panel.add(chkMascotas, c);
        c.gridwidth = 1;
        fila++;

        c.gridx = 0; c.gridy = fila; panel.add(new JLabel("Horario preferido:"), c);
        c.gridx = 1; c.gridy = fila; panel.add(cmbHorario, c);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Editar solicitud de busqueda",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        double presupuesto;
        int habitaciones;

        try {
            presupuesto = Double.parseDouble(txtPresupuesto.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un presupuesto valido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero entero de habitaciones.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String resultado = sistema.editarSolicitudBusqueda(id, activo.getUsuario(),
                txtSector.getText().trim(), presupuesto, habitaciones,
                chkMascotas.isSelected(), (String) cmbHorario.getSelectedItem());

        JOptionPane.showMessageDialog(this, resultado);
        refrescarMisSolicitudesBusqueda();
    }

    // ---------- Refrescos ----------

    private void refrescarTodo() {
        refrescarDisponibles();
        refrescarMisSolicitudesConvivencia();
        refrescarMisSolicitudesBusqueda();
    }

    private void refrescarDisponibles() {
        if (activo == null) return;

        modeloDisponibles.setRowCount(0);

        String sector = txtFiltroSector.getText().trim();
        Integer habitacionesMin = null;
        String habText = txtFiltroHabitaciones.getText().trim();
        if (!habText.isEmpty()) {
            try {
                habitacionesMin = Integer.parseInt(habText);
            } catch (NumberFormatException ex) {
                habitacionesMin = null;
            }
        }
        Double presupuestoMaximo = chkFiltrarPresupuesto.isSelected() ? activo.getPresupuesto() : null;
        Boolean soloConMascotas = chkFiltrarMascotas.isSelected() ? Boolean.TRUE : null;

        List<Apartamento> lista = sistema.getDisponiblesFiltrados(sector, habitacionesMin, presupuestoMaximo, soloConMascotas);

        for (Apartamento a : lista) {
            modeloDisponibles.addRow(new Object[]{
                    a.getId(), a.getDireccion(), a.getSector(),
                    String.format("$%.2f", a.getPrecio()), a.getHabitaciones(),
                    a.isAceptaMascotas() ? "Si" : "No",
                    a.getFechaPublicacion(), a.getFechaExpiracion()
            });
        }
    }

    private void refrescarMisSolicitudesConvivencia() {
        if (activo == null) return;

        modeloMisSolicitudesConvivencia.setRowCount(0);
        List<SolicitudConvivencia> lista = sistema.getSolicitudesDeUsuario(activo.getUsuario());

        for (SolicitudConvivencia s : lista) {
            modeloMisSolicitudesConvivencia.addRow(new Object[]{
                    s.getId(), s.getApartamento().getDireccion(), s.getApartamento().getSector(),
                    s.getFecha(), s.getEstado()
            });
        }
    }

    private void refrescarMisSolicitudesBusqueda() {
        if (activo == null) return;

        modeloMisSolicitudesBusqueda.setRowCount(0);
        List<SolicitudBusqueda> lista = sistema.getSolicitudesBusquedaDeUsuario(activo.getUsuario());

        for (SolicitudBusqueda s : lista) {
            modeloMisSolicitudesBusqueda.addRow(new Object[]{
                    s.getId(), s.getSectorDeseado(),
                    String.format("$%.2f", s.getPresupuestoMaximo()), s.getHabitacionesNecesarias(),
                    s.isAceptaMascotas() ? "Si" : "No", s.getHorarioPreferido(),
                    s.getFechaCreacion(), s.getEstado()
            });
        }
    }
}
