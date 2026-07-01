package interfaz;

import modelo.Arrendador;
import modelo.Buscador;
import negocio.SistemaRoommateRadar;

import javax.swing.*;
import java.awt.*;

public class PanelRegistro extends JPanel {

    private static final String TIPO_BUSCADOR = "Buscador (busca apartamento)";
    private static final String TIPO_ARRENDADOR = "Arrendador (publica apartamentos)";

    private final VentanaPrincipal ventana;
    private final SistemaRoommateRadar sistema;

    private final JComboBox<String> cmbTipoCuenta;

    private final JTextField txtUsuario;
    private final JPasswordField txtClave;
    private final JTextField txtNombre;
    private final JTextField txtTelefono;
    private final JTextField txtCorreo;

    private final CardLayout cardCampos;
    private final JPanel panelCampos;

    private final JTextField txtPresupuesto;
    private final JComboBox<String> cmbOcupacion;
    private final JComboBox<String> cmbHorario;
    private final JCheckBox chkMascotas;
    private final JComboBox<String> cmbRuido;
    private final JComboBox<String> cmbOrden;

    public PanelRegistro(VentanaPrincipal ventana, SistemaRoommateRadar sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Registro de usuario"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        cmbTipoCuenta = new JComboBox<>(new String[]{TIPO_BUSCADOR, TIPO_ARRENDADOR});
        txtUsuario = new JTextField(18);
        txtClave = new JPasswordField(18);
        txtNombre = new JTextField(18);
        txtTelefono = new JTextField(18);
        txtCorreo = new JTextField(18);

        int fila = 0;
        fila = agregarFila(form, c, fila, "Tipo de cuenta:", cmbTipoCuenta);
        fila = agregarFila(form, c, fila, "Usuario:", txtUsuario);
        fila = agregarFila(form, c, fila, "Clave:", txtClave);
        fila = agregarFila(form, c, fila, "Nombre completo:", txtNombre);
        fila = agregarFila(form, c, fila, "Telefono:", txtTelefono);
        fila = agregarFila(form, c, fila, "Correo:", txtCorreo);

        // Campos especificos segun tipo de cuenta
        cardCampos = new CardLayout();
        panelCampos = new JPanel(cardCampos);

        txtPresupuesto = new JTextField(18);
        cmbOcupacion = new JComboBox<>(new String[]{"Estudiante", "Profesional"});
        cmbHorario = new JComboBox<>(new String[]{"Diurno", "Nocturno"});
        chkMascotas = new JCheckBox("Tiene mascotas");
        cmbRuido = new JComboBox<>(new String[]{"Bajo", "Medio", "Alto"});
        cmbOrden = new JComboBox<>(new String[]{"Ordenado", "Intermedio", "Relajado"});

        panelCampos.add(crearCamposBuscador(), TIPO_BUSCADOR);
        panelCampos.add(crearCamposArrendador(), TIPO_ARRENDADOR);

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2;
        form.add(panelCampos, c);
        c.gridwidth = 1;
        fila++;

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnVolver = new JButton("Volver");

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnRegistrar);
        botones.add(btnVolver);

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2;
        form.add(botones, c);

        JScrollPane scroll = new JScrollPane(form);
        add(scroll, BorderLayout.CENTER);

        cmbTipoCuenta.addActionListener(e ->
                cardCampos.show(panelCampos, (String) cmbTipoCuenta.getSelectedItem()));

        btnRegistrar.addActionListener(e -> registrar());
        btnVolver.addActionListener(e -> ventana.mostrarLogin());
    }

    private JPanel crearCamposBuscador() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        fila = agregarFila(panel, c, fila, "Presupuesto mensual ($):", txtPresupuesto);
        fila = agregarFila(panel, c, fila, "Ocupacion:", cmbOcupacion);
        fila = agregarFila(panel, c, fila, "Horario preferido:", cmbHorario);
        fila = agregarFila(panel, c, fila, "", chkMascotas);
        fila = agregarFila(panel, c, fila, "Nivel de ruido:", cmbRuido);
        agregarFila(panel, c, fila, "Nivel de orden:", cmbOrden);

        return panel;
    }

    private JPanel crearCamposArrendador() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("No se requieren datos adicionales para arrendador.", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private int agregarFila(JPanel form, GridBagConstraints c, int fila, String etiqueta, JComponent campo) {
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = fila; form.add(new JLabel(etiqueta), c);
        c.gridx = 1; c.gridy = fila; form.add(campo, c);
        return fila + 1;
    }

    private void registrar() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (usuario.isEmpty() || clave.isEmpty() || nombre.isEmpty()
                || telefono.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!nombre.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
            JOptionPane.showMessageDialog(this, "El nombre solo puede contener letras y espacios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean esBuscador = TIPO_BUSCADOR.equals(cmbTipoCuenta.getSelectedItem());
        boolean exito;

        if (esBuscador) {
            exito = registrarComoBuscador(usuario, clave, nombre, telefono, correo);
        } else {
            Arrendador a = new Arrendador(usuario, clave, nombre, telefono, correo);
            exito = sistema.registrarArrendador(a);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "Registro exitoso. Ya puede iniciar sesion.");
            ventana.mostrarLogin();
        } else {
            JOptionPane.showMessageDialog(this, "El usuario '" + usuario + "' ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean registrarComoBuscador(String usuario, String clave, String nombre, String telefono, String correo) {
        String presupuestoTexto = txtPresupuesto.getText().trim();

        if (presupuestoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El presupuesto es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        double presupuesto;
        try {
            presupuesto = Double.parseDouble(presupuestoTexto);
            if (presupuesto <= 0) {
                JOptionPane.showMessageDialog(this, "El presupuesto debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero valido para el presupuesto.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Buscador b = new Buscador(
                usuario,
                clave,
                nombre,
                telefono,
                correo,
                presupuesto,
                (String) cmbOcupacion.getSelectedItem(),
                (String) cmbHorario.getSelectedItem(),
                chkMascotas.isSelected(),
                (String) cmbRuido.getSelectedItem(),
                (String) cmbOrden.getSelectedItem()
        );

        return sistema.registrarBuscador(b);
    }

    public void limpiar() {
        cmbTipoCuenta.setSelectedIndex(0);
        cardCampos.show(panelCampos, TIPO_BUSCADOR);
        txtUsuario.setText("");
        txtClave.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtPresupuesto.setText("");
        cmbOcupacion.setSelectedIndex(0);
        cmbHorario.setSelectedIndex(0);
        chkMascotas.setSelected(false);
        cmbRuido.setSelectedIndex(0);
        cmbOrden.setSelectedIndex(0);
    }
}
