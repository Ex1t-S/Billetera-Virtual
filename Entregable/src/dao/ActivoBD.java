package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import clasesformato.Activo;
import clasesformato.ActivosCripto;
import clasesformato.ActivosFiat;

public class ActivoBD {
	public void create(Connection connection, String option, String nomenclatura, double cantidad) {
        String sqlConsulta = "SELECT COUNT(*) FROM MONEDA WHERE NOMENCLATURA = ?";
        String tabla = option.equalsIgnoreCase("CRIPTO") ? "ACTIVO_CRIPTO" : "ACTIVO_FIAT";
        try (PreparedStatement pstmtConsulta = connection.prepareStatement(sqlConsulta)) {
            pstmtConsulta.setString(1, nomenclatura);
            ResultSet rs = pstmtConsulta.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                String sqlInsertar = "INSERT INTO "+tabla+" (NOMENCLATURA, CANTIDAD) VALUES (?, ?)";
                try (PreparedStatement pstmtInsertar = connection.prepareStatement(sqlInsertar)) {
                    pstmtInsertar.setString(1, nomenclatura);
                    pstmtInsertar.setDouble(2, cantidad);
                    pstmtInsertar.executeUpdate();
                    System.out.println("Activo creado exitosamente.");
                }
            } else {
                System.out.println("Error: La nomenclatura ingresada no existe en la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al generar activos: " + e.getMessage());
        }
	}
	public List<Activo> read(Connection connection, String option, String tabla) {
        List<Activo> activos = new ArrayList<>();
        tabla = (tabla.equalsIgnoreCase("Cripto"))?"CRIPTO":"FIAT";
        String sql = "SELECT NOMENCLATURA, CANTIDAD FROM ACTIVO_"+tabla+" ORDER BY ";
        if (option.equalsIgnoreCase("cantidad")) {
            sql += "CANTIDAD DESC";
        } else if (option.equalsIgnoreCase("nomenclatura")) {
            sql += "NOMENCLATURA";
        }
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String nomenclatura = rs.getString("NOMENCLATURA");
                double cantidad = rs.getDouble("CANTIDAD");
                
                Activo activo;
                if (tabla.equalsIgnoreCase("CRIPTO")) {
                    activo = new ActivosCripto(nomenclatura, cantidad);
                } else {
                    activo = new ActivosFiat(nomenclatura, cantidad);
                }
                activos.add(activo);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los activos: " + e.getMessage());
        }
        
        return activos;
    }
    public void update(Connection connection, String option, String nomenclatura, double cantidad) {
        String tabla = option.equalsIgnoreCase("CRIPTO") ? "ACTIVO_CRIPTO" : "ACTIVO_FIAT";
        String sql = "UPDATE " + tabla + " SET CANTIDAD = CANTIDAD + ? WHERE NOMENCLATURA = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, cantidad);
            pstmt.setString(2, nomenclatura);
            
            int filasActualizadas = pstmt.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Activo actualizado exitosamente en la tabla " + tabla + ".");
            } else {
                System.out.println("No se encontró el activo con la nomenclatura especificada en la tabla " + tabla + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el activo: " + e.getMessage());
        }
    }
    public boolean verificarActivo(Connection connection, String option, String nomenclatura) {
    	String tabla = (option.equalsIgnoreCase("Cripto"))? "CRIPTO":"FIAT";
    	String sql = "SELECT 1 FROM ACTIVO_"+tabla+" WHERE nomenclatura = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nomenclatura);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                System.out.println("El activo con nomenclatura " + nomenclatura + " no se encuentra registrado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar activo: " + e.getMessage());
        }
        return false;
    }
    public void delete(Connection connection, String tipo) {
    	String tabla = (tipo.equalsIgnoreCase("Cripto"))? "CRIPTO":"FIAT";
        String sql = "DELETE FROM ACTIVO_"+tabla;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Tabla Activo "+tabla+" limpiada.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar Activo "+tabla+": " + e.getMessage());
        }
    }
}