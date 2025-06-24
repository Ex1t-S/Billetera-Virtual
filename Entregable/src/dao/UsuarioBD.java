package dao;

import clasesformato.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioBD implements UsuarioDAO {
    private Connection connection;

    // Constructor que recibe la conexión
    public UsuarioBD(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean create(Connection connection, Usuario usuario) {
        String sql = "INSERT INTO USUARIO (ID_PERSONA, EMAIL, PASSWORD, ACEPTA_TERMINOS) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuario.getIdPersona());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPassword());
            stmt.setBoolean(4, usuario.getAceptaTerminos());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Usuario readByEmailAndPassword(Connection connection, String email, String password) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL = ? AND PASSWORD = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("ID"),
                    rs.getInt("ID_PERSONA"),
                    rs.getString("EMAIL"),
                    rs.getString("PASSWORD"),
                    rs.getBoolean("ACEPTA_TERMINOS")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean emailExiste(Connection connection, String email) {
        String sql = "SELECT 1 FROM USUARIO WHERE EMAIL = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar el email (posible tabla no existente): " + e.getMessage());
        }
        return false;
    }

}

