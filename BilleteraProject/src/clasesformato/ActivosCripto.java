package clasesformato;

public class ActivosCripto extends Activo{
	private Criptomoneda criptomoneda;
	private String direccion;
	
	 // Constructor
    public ActivosCripto(String nomenclatura, Double stock) {
        this.criptomoneda = new Criptomoneda(nomenclatura, stock);
    }

    // Getters
    public String getNomenclatura() {
        return criptomoneda.getNomenclatura();
    }

    public Double getStock() {
        return criptomoneda.getStock();
    }

    // Setters
    public void setNomenclatura(String nomenclatura) {
        criptomoneda.setNomenclatura(nomenclatura);
    }

    public void setStock(Double stock) {
        criptomoneda.setStock(stock);
    }

    @Override
    public String toString() {
        return String.format("%s | Stock: %.2f", getNomenclatura(), getStock());
    }
}
	
