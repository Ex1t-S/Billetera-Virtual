package Model;
import java.sql.*;

public class ConexionBD {
    protected static Connection c=null;
    private static final String BD_URL="jdbc:sqlite:walletDB.db";

    public static Connection getConnection() {
        try {
            if (c == null || c.isClosed()) {
                c = DriverManager.getConnection(BD_URL);
                System.out.println("Conexión realizada");
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
  }

    public static void closeConnection() {
        try {
            if((c!= null) && !(c.isClosed())) {
                c.close();
                System.out.println("Conexion cerrada");
            }
        }
        catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}