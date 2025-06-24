package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import org.json.JSONObject;

import clasesformato.Moneda;

public interface MonedaDAO {
	public void create(Connection connection, String option, Moneda moneda);
	public ArrayList<Moneda> read(Connection connection, String option);
	public void delete(Connection connection);
	public void updateStockAleatorio(Connection connection);
	public ArrayList<Moneda> readStock(Connection connection, String option);
	public double readValorEnDolar(Connection connection, String nomenclatura);
	public double readStockCriptomoneda(Connection connection, String nomenclatura);
	public void updateStockCriptomoneda(Connection connection, String nomenclaturaCripto, double cantidadCripto);
	public void actualizarValorDolar(Connection connection,String nombre, double valorDolar) throws SQLException;
	public Map<String, Double> obtenerValoresDolar() throws SQLException;
	public double obtenerPrecioMoneda(Connection connection, int idMoneda) throws SQLException;
    public void actualizarStockMoneda(Connection connection, int idMoneda, double cantidadComprada) throws SQLException;
}