package dao;
import java.sql.*;
import java.util.*;
public class ActivoFiatBD implements ActivoFiatDAO {
    private Connection connection;

    public ActivoFiatBD(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Object[]> getActivosByUsuario(int idUsuario) {
        List<Object[]> activos = new ArrayList<>();
        String sql = """
            SELECT M.NOMBRE, AF.CANTIDAD, (AF.CANTIDAD * M.VALOR_DOLAR) AS VALOR_TOTAL
            FROM ACTIVO_FIAT AF
            JOIN MONEDA M ON AF.ID_MONEDA = M.ID
            WHERE AF.ID_USUARIO = ?;
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                activos.add(new Object[]{
                    rs.getString("NOMBRE"),
                    rs.getDouble("CANTIDAD"),
                    rs.getDouble("VALOR_TOTAL")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener activos fiat: " + e.getMessage());
        }
        return activos;
    }
    public void create(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException {
        String sql = "INSERT INTO ACTIVO_FIAT (ID_USUARIO, ID_MONEDA, CANTIDAD) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMoneda);
            stmt.setDouble(3, cantidad);
            stmt.executeUpdate();
        }
    }
    public boolean existeActivoFiat(Connection connection, int idUsuario, int idMoneda) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ACTIVO_FIAT WHERE ID_USUARIO = ? AND ID_MONEDA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMoneda);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void actualizarSaldo(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException {
        String sql = "UPDATE ACTIVO_FIAT SET CANTIDAD = CANTIDAD + ? WHERE ID_USUARIO = ? AND ID_MONEDA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidad);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idMoneda);
            stmt.executeUpdate();
        }
    }
    public void eliminarDuplicadosFiat(Connection connection) throws SQLException {
        String sql = """
            DELETE FROM ACTIVO_FIAT
            WHERE ROWID NOT IN (
                SELECT MIN(ROWID)
                FROM ACTIVO_FIAT
                GROUP BY ID_USUARIO, ID_MONEDA
            )
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int filasEliminadas = stmt.executeUpdate();
            System.out.println("Duplicados eliminados de ACTIVO_FIAT: " + filasEliminadas);
        }
    }
    public boolean verificarYDescontarSaldo(Connection connection, int idUsuario, int idMoneda, double montoTotal) throws SQLException {
        String queryObtenerSaldo = "SELECT CANTIDAD FROM ACTIVO_FIAT WHERE ID_USUARIO = ? AND ID_MONEDA = ?";
        String queryActualizarSaldo = "UPDATE ACTIVO_FIAT SET CANTIDAD = CANTIDAD - ? WHERE ID_USUARIO = ? AND ID_MONEDA = ?";

        try (PreparedStatement stmtObtener = connection.prepareStatement(queryObtenerSaldo);
             PreparedStatement stmtActualizar = connection.prepareStatement(queryActualizarSaldo)) {

            // Obtener saldo actual
            stmtObtener.setInt(1, idUsuario);
            stmtObtener.setInt(2, idMoneda);
            ResultSet rs = stmtObtener.executeQuery();

            if (rs.next()) {
                double saldoActual = rs.getDouble("CANTIDAD");

                if (saldoActual >= montoTotal) {
                    // Actualizar saldo restando el monto
                    stmtActualizar.setDouble(1, montoTotal);
                    stmtActualizar.setInt(2, idUsuario);
                    stmtActualizar.setInt(3, idMoneda);
                    stmtActualizar.executeUpdate();
                    return true; // Operación exitosa
                }
            }
        }
        return false; // Saldo insuficiente
    }

}
