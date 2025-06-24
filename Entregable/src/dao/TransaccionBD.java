package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
public class TransaccionBD implements TransaccionDAO {
	private Connection connection;

    // Constructor que recibe la conexión
    public TransaccionBD(Connection connection) {
        this.connection = connection;
    }
	public void create(Connection connection, String nomenclaturaCripto, double cantidadCripto, String nomenclaturaFiat, double cantidadFiat) {
        String resumen = "Compra de " + cantidadCripto + " " + nomenclaturaCripto + " por " + cantidadFiat + " " + nomenclaturaFiat;
        LocalDateTime fechaHora = LocalDateTime.now();
        
        String sql = "INSERT INTO TRANSACCION (RESUMEN, FECHA_HORA) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, resumen);
            pstmt.setTimestamp(2, Timestamp.valueOf(fechaHora));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al registrar la transacción: " + e.getMessage());
        }
    }
	public List<String> read(Connection connection) {
	    List<String> transacciones = new ArrayList<>();
	    String sql = "SELECT * FROM TRANSACCION";
	    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	            String resumen = rs.getString("RESUMEN");
	            Timestamp fechaHora = rs.getTimestamp("FECHA_HORA");
	            transacciones.add("Resumen: " + resumen + ", Fecha y Hora: " + fechaHora.toLocalDateTime());
	        }
	    } catch (SQLException e) {
	        System.out.println("Error al listar las transacciones: " + e.getMessage());
	    }
	    return transacciones;
	}
	public void delete(Connection connection) {
        String sql = "DELETE FROM TRANSACCION";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Tabla Transaccion limpiada.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar tabla transaccion: " + e.getMessage());
        }
    }
	 @Override
	    public List<String[]> getTransaccionesByUsuario(Connection connection, int idUsuario) throws SQLException {
	        String sql = "SELECT RESUMEN, FECHA_HORA FROM TRANSACCION WHERE ID_USUARIO = ?";
	        List<String[]> transacciones = new ArrayList<>();

	        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	            stmt.setInt(1, idUsuario);
	            ResultSet rs = stmt.executeQuery();

	            while (rs.next()) {
	                transacciones.add(new String[]{
	                    rs.getString("RESUMEN"),
	                    rs.getString("FECHA_HORA")
	                });
	            }
	        }
	        return transacciones;
	    }
	    public void registrarTransaccion(int idUsuario, String resumen) throws SQLException {
	        String sql = "INSERT INTO TRANSACCION (ID_USUARIO, RESUMEN, FECHA_HORA) VALUES (?, ?, datetime('now'))";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setInt(1, idUsuario);
	            pstmt.setString(2, resumen);
	            pstmt.executeUpdate();
	        }
	    }
}