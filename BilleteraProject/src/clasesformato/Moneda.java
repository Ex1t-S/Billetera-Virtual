package clasesformato;

public abstract class Moneda {
	private String nombre;
	private String nomenclatura;
	private double valorDolar;
	private double stock;
	private double volatilidad;
	Moneda() {
		
	}
	Moneda (String nomenclatura, Double Stock) {
		this.stock = Stock;
		this.nomenclatura = nomenclatura;
	}
	Moneda(String nombre, String nomenclatura, double valorDolar, double volatilidad, double Stock) {
		this.nombre = nombre;
		this.nomenclatura = nomenclatura;
		this.valorDolar = valorDolar;
		this.volatilidad = volatilidad;
		this.stock = Stock;
	}
	public String getNombre(){
		return nombre;
	}
	public String getNomenclatura() {
		return nomenclatura;
	}
	public double getValorDolar() {
		return valorDolar;
	}
	public double getStock() {
		return stock;
	}
	public double getVolatilidad() {
		return volatilidad;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setNomenclatura(String nomenclatura) {
		this.nomenclatura = nomenclatura;
	}
	public void setValorDolar(double valorDolar) {
		this.valorDolar = valorDolar;
	}
	public void setStock(double stock) {
		this.stock = stock;
	}
	public void setVolatilidad(double volatilidad) {
		this.volatilidad = volatilidad;
	}
	public abstract String mostrarStock();
}
