package dao;
import java.sql.*;
import java.util.*;
public class ActivoCriptoBD implements ActivoCriptoDAO {
    private Connection connection;

    public ActivoCriptoBD(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Object[]> getActivosByUsuario(int idUsuario) {
        List<Object[]> activos = new ArrayList<>();
        String sql = """
            SELECT M.NOMBRE, AC.CANTIDAD, (AC.CANTIDAD * M.VALOR_DOLAR) AS VALOR_TOTAL
            FROM ACTIVO_CRIPTO AC
            JOIN MONEDA M ON AC.ID_MONEDA = M.ID
            WHERE AC.ID_USUARIO = ?;
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
            System.err.println("Error al obtener activos cripto: " + e.getMessage());
        }
        return activos;
    }
    public void create(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException {
        String sql = "INSERT INTO ACTIVO_CRIPTO (ID_USUARIO, ID_MONEDA, CANTIDAD) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMoneda);
            stmt.setDouble(3, cantidad);
            stmt.executeUpdate();
        }
    }
    public boolean existeActivoCripto(Connection connection, int idUsuario, int idMoneda) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ACTIVO_CRIPTO WHERE ID_USUARIO = ? AND ID_MONEDA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMoneda);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void actualizarSaldo(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException {
        String sql = "UPDATE ACTIVO_CRIPTO SET CANTIDAD = CANTIDAD + ? WHERE ID_USUARIO = ? AND ID_MONEDA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidad);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idMoneda);
            stmt.executeUpdate();
        }
    }
    public void eliminarDuplicadosCripto(Connection connection) throws SQLException {
        String sql = """
            DELETE FROM ACTIVO_CRIPTO
            WHERE ROWID NOT IN (
                SELECT MIN(ROWID)
                FROM ACTIVO_CRIPTO
                GROUP BY ID_USUARIO, ID_MONEDA
            )
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int filasEliminadas = stmt.executeUpdate();
            System.out.println("Duplicados eliminados de ACTIVO_CRIPTO: " + filasEliminadas);
        }
    }

    

}
