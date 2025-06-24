package dao;

import clasesformato.Persona;
import java.sql.Connection;

public interface PersonaDAO {
    /**
     * Crea una nueva persona.
     * @param connection Conexión a la base de datos.
     * @param persona Objeto Persona a registrar.
     * @return true si se creó exitosamente, false en caso contrario.
     */
    boolean create(Connection connection, Persona persona);
}

