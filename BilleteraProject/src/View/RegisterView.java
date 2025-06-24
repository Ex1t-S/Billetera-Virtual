package View;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class RegisterView extends JFrame {
    private JTextField nombreField, apellidoField, emailField;
    private JPasswordField passwordField;
    private JCheckBox terminosCheckBox;
    private JButton registrarButton;

    private Consumer<String[]> onRegisterListener;

    public RegisterView() {
        setTitle("Registro de Usuario");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10));

        // Componentes
        add(new JLabel("Nombres:"));
        nombreField = new JTextField();
        add(nombreField);

        add(new JLabel("Apellidos:"));
        apellidoField = new JTextField();
        add(apellidoField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Aceptar Términos:"));
        terminosCheckBox = new JCheckBox();
        add(terminosCheckBox);

        registrarButton = new JButton("Registrar");
        add(new JLabel()); // Espaciador
        add(registrarButton);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Acción del botón registrar
        registrarButton.addActionListener(e -> {
            if (onRegisterListener != null) {
                String nombres = nombreField.getText().trim();
                String apellidos = apellidoField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String aceptaTerminos = String.valueOf(terminosCheckBox.isSelected());

                // Validación de campos vacíos
                if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    mostrarError("Todos los campos son obligatorios.");
                    return;
                }

                // Crear y enviar el arreglo
                String[] userData = {nombres, apellidos, email, password, aceptaTerminos};
                onRegisterListener.accept(userData);
            }
        });
    }

    public void setOnRegisterListener(Consumer<String[]> listener) {
        this.onRegisterListener = listener;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}
