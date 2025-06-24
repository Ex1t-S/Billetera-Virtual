package dao;

import clasesformato.Usuario;
import java.sql.Connection;

public interface UsuarioDAO {
    boolean create(Connection connection, Usuario usuario);
    Usuario readByEmailAndPassword(Connection connection, String email, String password);
    boolean emailExiste(Connection connection, String email);
}
