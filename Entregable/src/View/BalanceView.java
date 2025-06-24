package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BalanceView extends JFrame {
    private JTable table;
    private JButton exportarCSVButton;
    private JButton generarSaldoButton;
    private JButton verTransaccionesButton;
    private JButton irAOperacionesButton;
    private JButton cerrarSesionButton;

    public BalanceView(List<Object[]> balanceCompleto, Runnable onGenerarSaldo, Runnable onVerTransacciones, Runnable onIrAOperaciones, Runnable onCerrarSesion) {
        setTitle("Balance de Activos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear la tabla
        String[] columnNames = {"Activo", "Cantidad", "Valor Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Object[] activo : balanceCompleto) {
            tableModel.addRow(activo);
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Botones
        JButton exportarCSVButton = new JButton("Exportar como CSV");
        JButton generarSaldoButton = new JButton("Generar Saldo");
        JButton verTransaccionesButton = new JButton("Ver Transacciones");
        JButton irAOperacionesButton = new JButton("Ir a Operaciones");
        JButton cerrarSesionButton = new JButton("Cerrar Sesión");

        // Acciones de los botones
        exportarCSVButton.addActionListener(e -> exportarComoCSV(balanceCompleto));
        generarSaldoButton.addActionListener(e -> onGenerarSaldo.run());
        verTransaccionesButton.addActionListener(e -> onVerTransacciones.run());
        irAOperacionesButton.addActionListener(e -> onIrAOperaciones.run());
        cerrarSesionButton.addActionListener(e -> onCerrarSesion.run());

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exportarCSVButton);
        buttonPanel.add(generarSaldoButton);
        buttonPanel.add(verTransaccionesButton);
        buttonPanel.add(irAOperacionesButton);
        buttonPanel.add(cerrarSesionButton); // Aquí se agrega correctamente

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }
   
    private void exportarComoCSV(List<Object[]> balanceCompleto) {
        try (java.io.FileWriter writer = new java.io.FileWriter("balance.csv")) {
            writer.write("Activo,Cantidad,Valor Total\n");
            for (Object[] row : balanceCompleto) {
                writer.write(row[0] + "," + row[1] + "," + row[2] + "\n");
            }
            JOptionPane.showMessageDialog(this, "Balance exportado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar CSV: " + ex.getMessage());
        }
    }
}
