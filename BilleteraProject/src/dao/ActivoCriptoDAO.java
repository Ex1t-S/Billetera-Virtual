package dao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
public interface ActivoCriptoDAO {
	List<Object[]> getActivosByUsuario(int idUsuario);
	void create(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException;
	public void actualizarSaldo(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException;
	public boolean existeActivoCripto(Connection connection, int idUsuario, int idMoneda) throws SQLException;
	public void eliminarDuplicadosCripto(Connection connection) throws SQLException;
}
