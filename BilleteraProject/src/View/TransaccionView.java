package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransaccionView extends JFrame {
    public TransaccionView(List<String[]> transacciones) {
        setTitle("Historial de Transacciones");
        setSize(500, 300);

        String[] columnNames = {"Resumen", "Fecha y Hora"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] transaccion : transacciones) {
            model.addRow(transaccion);
        }

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }
}