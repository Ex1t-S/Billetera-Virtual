package dao;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import clasesformato.*;

public class MonedaBD implements MonedaDAO{
	private Connection connection;
	public MonedaBD(Connection connection) {
		this.connection=connection;
	}
	public void create(Connection connection, String tipo, Moneda moneda) {
        String sql = "INSERT INTO MONEDA (TIPO, NOMBRE, NOMENCLATURA, VALOR_DOLAR, VOLATILIDAD, STOCK) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            pstmt.setString(2, moneda.getNombre());
            pstmt.setString(3, moneda.getNomenclatura());
            pstmt.setDouble(4, moneda.getValorDolar());
            pstmt.setObject(5, moneda.getVolatilidad());
            pstmt.setObject(6, moneda.getStock());
            pstmt.executeUpdate();
            System.out.println("Moneda guardada en la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al insertar moneda: " + e.getMessage());
        }
    }
	public ArrayList<Moneda> read(Connection connection, String option) {
	    ArrayList<Moneda> monedas = new ArrayList<>();
	    String sql = "SELECT * FROM MONEDA ORDER BY ";

	    if (option.equalsIgnoreCase("valordolar")) {
	        sql += "VALOR_DOLAR";
	    } else if (option.equalsIgnoreCase("nomenclatura")) {
	        sql += "NOMENCLATURA";
	    } else {
	    	System.out.println("Opción inválida para ordenar. Usando 'NOMENCLATURA' como predeterminado.");
	        sql += "NOMENCLATURA";
	    }

	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            String tipo = rs.getString("TIPO");
	            String nombre = rs.getString("NOMBRE");
	            String nomenclatura = rs.getString("NOMENCLATURA");
	            double valorEnDolar = rs.getDouble("VALOR_DOLAR");
	            double stock = rs.getDouble("STOCK");

	            Moneda moneda = tipo.equalsIgnoreCase("Cripto")
	                ? new Criptomoneda(nombre, nomenclatura, valorEnDolar, 0, stock)
	                : new Fiat(nombre, nomenclatura, valorEnDolar, 0, stock);

	            monedas.add(moneda);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error al listar monedas: " + e.getMessage());
	    }
	    return monedas;
	}
	public void delete(Connection connection) {
        String sql = "DELETE FROM MONEDA";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar moneda: " + e.getMessage());
        }
    }
	public void updateStockAleatorio(Connection connection) {
        String sql = "UPDATE MONEDA SET STOCK = ? WHERE NOMENCLATURA = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ArrayList<Moneda> monedas = read(connection, "nomenclatura");

            for (Moneda moneda : monedas) {
                double stockAleatorio = Math.random() * 1000;
                pstmt.setDouble(1, stockAleatorio);
                pstmt.setString(2, moneda.getNomenclatura());
                pstmt.executeUpdate();
            }
            System.out.println("Stock generado aleatoriamente.");
        } catch (SQLException e) {
            System.out.println("Error al generar stock: " + e.getMessage());
        }
    }
	public ArrayList<Moneda> readStock(Connection connection, String option) {         
        ArrayList<Moneda> monedas = new ArrayList<>();
	    String sql = "SELECT * FROM MONEDA ORDER BY ";

	    if (option.equalsIgnoreCase("stock")) {
	        sql += "STOCK";
	    } else if (option.equalsIgnoreCase("nomenclatura")) {
	        sql += "NOMENCLATURA";
	    } else {
	    	System.out.println("Opción inválida para ordenar. Usando 'NOMENCLATURA' como predeterminado.");
	        sql += "NOMENCLATURA";
	    }

	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            String tipo = rs.getString("TIPO");
	            String nombre = rs.getString("NOMBRE");
	            String nomenclatura = rs.getString("NOMENCLATURA");
	            double valorEnDolar = rs.getDouble("VALOR_DOLAR");
	            double stock = rs.getDouble("STOCK");

	            Moneda moneda = tipo.equalsIgnoreCase("Cripto")
	                ? new Criptomoneda(nombre, nomenclatura, valorEnDolar, 0, stock)
	                : new Fiat(nombre, nomenclatura, valorEnDolar, 0, stock);

	            monedas.add(moneda);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error al listar stock: " + e.getMessage());
	    }
	    return monedas;
    } 
    public double readValorEnDolar(Connection connection, String nomenclatura) {
        String sql = "SELECT VALOR_DOLAR FROM MONEDA WHERE NOMENCLATURA = ? ";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nomenclatura);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("VALOR_DOLAR");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener valor de criptomoneda: " + e.getMessage());
        }
        return -1; //la criptomoneda no existe
    }
    public double readStockCriptomoneda(Connection connection, String nomenclatura) {
        String sql = "SELECT STOCK FROM MONEDA WHERE NOMENCLATURA = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nomenclatura);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("STOCK");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener stock de criptomoneda: " + e.getMessage());
        }
        return -1; //no se pudo obtener el stock
    }
    public void updateStockCriptomoneda(Connection connection, String nomenclaturaCripto, double cantidadCripto) {
    	String sql = "UPDATE MONEDA SET STOCK = STOCK - ? WHERE NOMENCLATURA = ? AND STOCK >= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, cantidadCripto);
            pstmt.setString(2, nomenclaturaCripto);
            pstmt.setDouble(3, cantidadCripto);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Stock actualizado exitosamente.");
            } else {
                System.out.println("No hay suficiente stock o criptomoneda no encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el stock: " + e.getMessage());
        }
    }
    public void actualizarValorDolar(Connection connection,String nombre, double valorDolar) throws SQLException {
        String sql = "UPDATE MONEDA SET VALOR_DOLAR = ? WHERE NOMBRE = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, valorDolar);
            stmt.setString(2, nombre);
            stmt.executeUpdate();
        }
    }
    // Obtener los valores de dólar de todas las monedas en la base de datos
    public Map<String, Double> obtenerValoresDolar() throws SQLException {
        Map<String, Double> valoresDolar = new HashMap<>();

        String sql = "SELECT NOMBRE, VALOR_DOLAR FROM MONEDA";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("NOMBRE");
                double valorDolar = rs.getDouble("VALOR_DOLAR");
                valoresDolar.put(nombre, valorDolar);
            }
        }
        return valoresDolar;
    }
    public void actualizarStockMoneda(Connection connection, int idMoneda, double cantidadComprada) throws SQLException {
        String sql = "UPDATE MONEDA SET STOCK = STOCK - ? WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidadComprada);
            stmt.setInt(2, idMoneda);
            stmt.executeUpdate();
        }
    }
    public double obtenerPrecioMoneda(Connection connection, int idMoneda) throws SQLException {
        String sql = "SELECT VALOR_DOLAR FROM MONEDA WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMoneda);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("VALOR_DOLAR");
            }
        }
        return 0.0; // Valor por defecto si no se encuentra
    }
}
