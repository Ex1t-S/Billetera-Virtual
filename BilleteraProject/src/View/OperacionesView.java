package View;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import dao.*;
import clasesformato.MonedaEnum;

public class OperacionesView extends JFrame {
    private JButton cotizacionesButton;
    private JButton comprarCriptoButton;
    private JButton volverButton;
    private JTextArea cotizacionesTextArea;

    private TransaccionDAO transaccionDAO;
    private ActivoFiatDAO activoFiatDAO;
    private ActivoCriptoDAO activoCriptoDAO;
    private MonedaDAO monedaDAO;
    private Connection connection;
    private int idUsuario; // ID del usuario actual

    public OperacionesView(Runnable onVolver, int idUsuario, TransaccionDAO transaccionDAO, ActivoFiatDAO activoFiatDAO, ActivoCriptoDAO activoCriptoDAO, MonedaDAO monedaDAO, Connection connection) {
        this.transaccionDAO = transaccionDAO;
        this.activoFiatDAO = activoFiatDAO;
        this.activoCriptoDAO = activoCriptoDAO;
        this.monedaDAO = monedaDAO;
        this.idUsuario = idUsuario;
        this.connection = connection;

        setTitle("Mis Operaciones");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Botones
        cotizacionesButton = new JButton("Ver Cotizaciones");
        comprarCriptoButton = new JButton("Comprar Cripto");
        volverButton = new JButton("Volver");

        // Área de texto para cotizaciones
        cotizacionesTextArea = new JTextArea();
        cotizacionesTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cotizacionesTextArea);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cotizacionesButton);
        buttonPanel.add(comprarCriptoButton);
        buttonPanel.add(volverButton);

        // ActionListener para "Ver Cotizaciones"
        cotizacionesButton.addActionListener(e -> mostrarCotizaciones());

        // ActionListener para "Comprar Cripto"
        comprarCriptoButton.addActionListener(e -> mostrarVentanaCompra());

        // ActionListener para "Volver"
        volverButton.addActionListener(e -> {
            dispose(); // Cierra la ventana actual
            onVolver.run(); // Vuelve a la vista anterior
        });

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    // Mostrar cotizaciones
    private void mostrarCotizaciones() {
        try {
            StringBuilder cotizaciones = new StringBuilder("Cotizaciones (desde la base de datos):\n");
            for (MonedaEnum moneda : MonedaEnum.values()) {
                // Usar getIdMoneda() en vez de ordinal()
                double precio = monedaDAO.obtenerPrecioMoneda(connection, moneda.getIdMoneda());
                cotizaciones.append(moneda.getNombre()).append(": $").append(precio).append("\n");
            }
            cotizacionesTextArea.setText(cotizaciones.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener cotizaciones.");
            e.printStackTrace();
        }
    }


    // Ventana de compra de criptomonedas
    private void mostrarVentanaCompra() {
        JFrame compraFrame = new JFrame("Compra de Criptomonedas");
        compraFrame.setSize(400, 250);
        compraFrame.setLayout(new GridLayout(5, 2));

        JLabel labelCripto = new JLabel("Criptomoneda:");
        JComboBox<String> criptoBox = new JComboBox<>(new String[]{"Bitcoin", "Ethereum", "USD Coin", "Tether", "Dogecoin"});

        JLabel labelCantidad = new JLabel("Cantidad:");
        JTextField cantidadField = new JTextField();

        JLabel labelMetodoPago = new JLabel("Método de Pago:");
        JComboBox<String> metodoPagoBox = new JComboBox<>(new String[]{"PESOS ARS", "USDT"});

        JButton confirmarButton = new JButton("Confirmar Compra");
        JButton cancelarButton = new JButton("Cancelar");

        confirmarButton.addActionListener(e -> {
            String cripto = (String) criptoBox.getSelectedItem();
            String metodoPago = (String) metodoPagoBox.getSelectedItem();
            String cantidadTexto = cantidadField.getText();

            try {
                double cantidad = Double.parseDouble(cantidadTexto);
                int idMonedaPago = metodoPago.equals("PESOS ARS") ? MonedaEnum.PESO.ordinal() : MonedaEnum.TETHER.ordinal();
                int idCripto = criptoBox.getSelectedIndex() + 1;

                // Obtener precio de la cripto desde la base de datos
                double precioCripto = monedaDAO.obtenerPrecioMoneda(connection, idCripto);
                double montoTotal = cantidad * precioCripto;

                // Verificar saldo y descontar
                if (activoFiatDAO.verificarYDescontarSaldo(connection, idUsuario, idMonedaPago, montoTotal)) {
                    // Actualizar el stock de la moneda
                    monedaDAO.actualizarStockMoneda(connection, idCripto, cantidad);

                    // Registrar la cantidad comprada
                    activoCriptoDAO.create(connection, idUsuario, idCripto, cantidad);

                    // Registrar la transacción
                    transaccionDAO.registrarTransaccion(idUsuario, "Compra de " + cantidad + " " + cripto + " con " + metodoPago);

                    JOptionPane.showMessageDialog(compraFrame, "Compra realizada exitosamente.");
                    compraFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(compraFrame, "Saldo insuficiente en " + metodoPago);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(compraFrame, "Cantidad inválida. Inténtelo de nuevo.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(compraFrame, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelarButton.addActionListener(e -> compraFrame.dispose());

        // Agregar componentes a la ventana de compra
        compraFrame.add(labelCripto);
        compraFrame.add(criptoBox);
        compraFrame.add(labelCantidad);
        compraFrame.add(cantidadField);
        compraFrame.add(labelMetodoPago);
        compraFrame.add(metodoPagoBox);
        compraFrame.add(confirmarButton);
        compraFrame.add(cancelarButton);

        compraFrame.setLocationRelativeTo(this);
        compraFrame.setVisible(true);
    }
}
