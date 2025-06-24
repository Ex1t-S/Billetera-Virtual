package clasesformato;

public class ActivosFiat extends Activo{
	private Fiat fiat;
	private String id;
	
	 // Constructor
    public ActivosFiat(String nomenclatura, Double stock) {
        this.fiat = new Fiat(nomenclatura, stock);
    }

    // Getters
    public String getNomenclatura() {
        return fiat.getNomenclatura();
    }

    public Double getStock() {
        return fiat.getStock();
    }

    // Setters
    public void setNomenclatura(String nomenclatura) {
    	fiat.setNomenclatura(nomenclatura);
    }

    public void setStock(Double stock) {
    	fiat.setStock(stock);
    }

    @Override
    public String toString() {
        return String.format("%s | Stock: %.2f", getNomenclatura(), getStock());
    }
}

