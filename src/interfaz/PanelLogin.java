package interfaz;

import modelo.Arrendador;
import modelo.Buscador;
import modelo.Usuario;
import negocio.SistemaRoommateRadar;

import javax.swing.*;
import java.awt.*;

public class PanelLogin extends JPanel {

    private final VentanaPrincipal ventana;
    private final SistemaRoommateRadar sistema;

    private final JTextField txtUsuario;
    private final JPasswordField txtClave;

    public PanelLogin(VentanaPrincipal ventana, SistemaRoommateRadar sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new GridBagLayout());

        JPanel caja = new JPanel(new GridBagLayout());
        caja.setBorder(BorderFactory.createTitledBorder("Iniciar sesion - RoommateRadar"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtUsuario = new JTextField(18);
        txtClave = new JPasswordField(18);

        int fila = 0;

        c.gridx = 0; c.gridy = fila; caja.add(new JLabel("Usuario:"), c);
        c.gridx = 1; c.gridy = fila; caja.add(txtUsuario, c);
        fila++;

        c.gridx = 0; c.gridy = fila; caja.add(new JLabel("Clave:"), c);
        c.gridx = 1; c.gridy = fila; caja.add(txtClave, c);
        fila++;

        JButton btnLogin = new JButton("Iniciar sesion");
        JButton btnRegistro = new JButton("Registrarse como buscador");

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnLogin);
        botones.add(btnRegistro);

        c.gridx = 0; c.gridy = fila; c.gridwidth = 2;
        caja.add(botones, c);

        add(caja);

        btnLogin.addActionListener(e -> iniciarSesion());
        btnRegistro.addActionListener(e -> ventana.mostrarRegistro());

        txtClave.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = sistema.login(usuario, clave);

        if (u == null) {
            if (sistema.estaBaneado(usuario, clave)) {
                JOptionPane.showMessageDialog(this, "Este usuario esta bloqueado por el administrador.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o clave incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        switch (u.getRol()) {
            case "ADMINISTRADOR":
                ventana.mostrarAdmin();
                break;
            case "ARRENDADOR":
                ventana.mostrarArrendador((Arrendador) u);
                break;
            default:
                ventana.mostrarBuscador((Buscador) u);
        }
    }

    public void limpiar() {
        txtUsuario.setText("");
        txtClave.setText("");
    }
}
