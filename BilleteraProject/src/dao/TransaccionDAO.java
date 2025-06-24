package dao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
public interface TransaccionDAO {
	void create(Connection connection, String nomenclaturaCripto, double cantidadCripto, String nomenclaturaFiat, double cantidadFiat);
	List<String> read(Connection connection);
	void delete(Connection connection);
	List<String[]> getTransaccionesByUsuario(Connection connection, int idUsuario) throws SQLException;
	public void registrarTransaccion(int idUsuario, String resumen) throws SQLException;
}
