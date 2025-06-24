package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
public interface ActivoFiatDAO {
    List<Object[]> getActivosByUsuario(int idUsuario);
    void create(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException;
    public boolean existeActivoFiat(Connection connection, int idUsuario, int idMoneda) throws SQLException;
    public void actualizarSaldo(Connection connection, int idUsuario, int idMoneda, double cantidad) throws SQLException;
    public void eliminarDuplicadosFiat(Connection connection) throws SQLException;
    public boolean verificarYDescontarSaldo(Connection connection, int idUsuario, int idMoneda, double montoTotal) throws SQLException;
}
