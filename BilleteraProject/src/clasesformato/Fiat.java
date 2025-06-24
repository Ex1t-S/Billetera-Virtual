package clasesformato;

public class Fiat extends Moneda{
	public Fiat(String nombre, String nomenclatura, double valorDolar, double volatilidad, double Stock) {
		super(nombre,nomenclatura,valorDolar,volatilidad,Stock);
	}
	public Fiat(String nomenclatura, Double Stock ) {
		super(nomenclatura,Stock);
	}
	public String toString() {
        return String.format("Tipo: %s | Nombre: %s | Nomenclatura: %s | Valor en Dólares: %.2f | Stock: %.2f",
                "FIAT", getNombre(), getNomenclatura(), getValorDolar(), getStock());
    }
	public String mostrarStock() {
		 return String.format(" %s |  Stock: %.2f", getNomenclatura(),getStock());
	 }
}
