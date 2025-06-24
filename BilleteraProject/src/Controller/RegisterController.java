package Controller;

import View.RegisterView;
import dao.UsuarioDAO;
import dao.UsuarioBD;
import Model.ConexionBD;
import clasesformato.Usuario;
import clasesformato.Persona;
import dao.PersonaBD;
import Wallet.*;
import java.sql.Connection;

public class RegisterController {
    private UsuarioDAO usuarioDAO;
    private PersonaBD personaDAO;
    private RegisterView registerView;
    private Connection connection;

    public RegisterController(RegisterView view) {
        this.connection = ConexionBD.getConnection();
        this.usuarioDAO = new UsuarioBD(connection);
        this.personaDAO = new PersonaBD();
        this.registerView = view;
    }

    private void registrarUsuario(String[] userData) {
        String nombres = userData[0];
        String apellidos = userData[1];
        String email = userData[2];
        String password = userData[3];
        boolean aceptaTerminos = Boolean.parseBoolean(userData[4]);

        if (!aceptaTerminos) {
            registerView.mostrarError("Debe aceptar los términos y condiciones.");
            return;
        }

        try {
            // Verificar si el email ya existe
            if (usuarioDAO.emailExiste(connection,email)) {
                registerView.mostrarError("El email ya está registrado.");
                return;
            }

            // 1. Insertar en PERSONA y obtener el ID generado
            Persona nuevaPersona = new Persona(0, nombres, apellidos);
            boolean personaCreada = personaDAO.create(connection, nuevaPersona);
            if (!personaCreada) {
                registerView.mostrarError("Error al registrar la persona.");
                return;
            }

            // 2. Insertar en USUARIO usando el ID_PERSONA generado
            int idPersona = nuevaPersona.getId(); // ID autogenerado
            Usuario nuevoUsuario = new Usuario(0, idPersona, email, password, aceptaTerminos);
            WalletController wallet = new WalletController();
            boolean usuarioCreado = usuarioDAO.create(connection, nuevoUsuario);
            if (usuarioCreado) {
                registerView.mostrarExito("Usuario registrado exitosamente.");
                registerView.dispose();
            } else {
                registerView.mostrarError("Error al registrar el usuario.");
            }

        } catch (Exception e) {
            registerView.mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
