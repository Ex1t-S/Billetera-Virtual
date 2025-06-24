package clasesformato;

public class Criptomoneda extends Moneda {
	public Criptomoneda(String nombre, String nomenclatura, double valorDolar, double volatilidad, double stock) {
		super(nombre,nomenclatura,valorDolar,volatilidad,stock);
	}
	public Criptomoneda(String nomenclatura, Double Stock ) {
		super(nomenclatura,Stock);
	}
	 public String toString() {
	        return String.format("Tipo: %s | Nombre: %s | Nomenclatura: %s | Valor en Dólares: %.20f | Stock: %.20f",
	                "CRYPTO", getNombre(), getNomenclatura(), getValorDolar(), getStock());
	    }
	 public String mostrarStock() {
		 return String.format(" %s |  Stock: %.20f", getNomenclatura(),getStock());
	 }
}
