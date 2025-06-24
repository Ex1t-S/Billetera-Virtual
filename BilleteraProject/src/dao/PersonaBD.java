package dao;

import clasesformato.Persona;
import java.sql.*;

public class PersonaBD implements PersonaDAO {
	

    @Override
    public boolean create(Connection connection, Persona persona) {
        String sql = "INSERT INTO PERSONA (NOMBRES, APELLIDOS) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, persona.getNombres());
            stmt.setString(2, persona.getApellidos());
            stmt.executeUpdate();

            // Obtener el ID generado automáticamente
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                persona.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar la persona: " + e.getMessage());
        }
        return false;
    }
}
