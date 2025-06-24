package dao;

import java.sql.Connection;
import java.util.List;

import clasesformato.Activo;

public interface ActivoDAO {
	public void create(Connection connection, String option, String nomenclatura, double cantidad);
	public List<Activo> read(Connection connection, String option, String tabla);
	public void update(Connection connection, String tipo, String nomenclatura, double cantidad);
	public boolean verificarActivo(Connection connection, String option, String nomenclaturaCripto);
	void delete(Connection connection);

}