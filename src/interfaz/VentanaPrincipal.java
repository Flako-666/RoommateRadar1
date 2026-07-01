package interfaz;

import modelo.Arrendador;
import modelo.Buscador;
import negocio.SistemaRoommateRadar;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final SistemaRoommateRadar sistema;
    private final CardLayout cardLayout;
    private final JPanel contenedor;

    private final PanelLogin panelLogin;
    private final PanelRegistro panelRegistro;
    private final PanelAdmin panelAdmin;
    private final PanelBuscador panelBuscador;
    private final PanelArrendador panelArrendador;

    public VentanaPrincipal() {
        super("RoommateRadar");

        sistema = new SistemaRoommateRadar();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        panelLogin = new PanelLogin(this, sistema);
        panelRegistro = new PanelRegistro(this, sistema);
        panelAdmin = new PanelAdmin(this, sistema);
        panelBuscador = new PanelBuscador(this, sistema);
        panelArrendador = new PanelArrendador(this, sistema);

        contenedor.add(panelLogin, "login");
        contenedor.add(panelRegistro, "registro");
        contenedor.add(panelAdmin, "admin");
        contenedor.add(panelBuscador, "buscador");
        contenedor.add(panelArrendador, "arrendador");

        setContentPane(contenedor);

        mostrarLogin();
    }

    public void mostrarLogin() {
        panelLogin.limpiar();
        cardLayout.show(contenedor, "login");
    }

    public void mostrarRegistro() {
        panelRegistro.limpiar();
        cardLayout.show(contenedor, "registro");
    }

    public void mostrarAdmin() {
        panelAdmin.refrescarTodo();
        cardLayout.show(contenedor, "admin");
    }

    public void mostrarBuscador(Buscador b) {
        panelBuscador.iniciarSesionComo(b);
        cardLayout.show(contenedor, "buscador");
    }

    public void mostrarArrendador(Arrendador a) {
        panelArrendador.iniciarSesionComo(a);
        cardLayout.show(contenedor, "arrendador");
    }

    public void cerrarSesion() {
        sistema.cerrarSesion();
        mostrarLogin();
    }
}
