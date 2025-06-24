package Controller;

import View.LoginView;
import dao.*;
import Model.ConexionBD;
import clasesformato.Usuario;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class LoginController {
    private UsuarioDAO usuarioDAO;
    private LoginView vista;
    private Connection connection;

    public LoginController(LoginView vista) {
        this.connection = ConexionBD.getConnection();
        this.usuarioDAO = new UsuarioBD(connection);
        this.vista = vista;

        // Listener para el botón de inicio de sesión
        this.vista.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verificarCredenciales();
            }
        });
    }

    private void verificarCredenciales() {
        String email = vista.getEmail();
        String password = vista.getPassword();
        UsuarioDAO usuarioDAO = new UsuarioBD(connection);
        Usuario usuario =new UsuarioBD(connection).readByEmailAndPassword(connection, email,password);
        if (usuario != null) {
            vista.mostrarMensaje("Inicio de sesión exitoso.");
            vista.dispose(); // Cierra la ventana de login
            // Aquí se redirige a otra vista, como BalanceView
        } else {
            vista.mostrarMensaje("Email o contraseña incorrectos.");
        }
    }
}
