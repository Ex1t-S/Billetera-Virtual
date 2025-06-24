package clasesformato;
public enum MonedaEnum {
    // Criptomonedas
    BITCOIN("Bitcoin", "BTC", 1),
    ETHEREUM("Ethereum", "ETH", 2),
    TETHER("Tether", "USDT", 3),
    USD_COIN("USD Coin", "USDC", 4),
    DOGECOIN("Dogecoin", "DOGE", 5),

    // Monedas Fiat
    PESO("Peso Argentino", "ARS", 6),
    DOLAR("Dólar Estadounidense", "USD", 7);

    private final String nombre;   // Nombre de la moneda
    private final String codigo;   // Código o símbolo de la moneda
    private final int idMoneda;    // ID de la moneda en la base de datos

    // Constructor del ENUM
    MonedaEnum(String nombre, String codigo, int idMoneda) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.idMoneda = idMoneda;
    }

    // Métodos para acceder a los valores
    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getIdMoneda() {
        return idMoneda;
    }

    // Método para listar todas las monedas
    public static void listarMonedas() {
        System.out.println("Listado de Monedas:");
        for (MonedaEnum moneda : MonedaEnum.values()) {
            System.out.println(moneda.getNombre() + " (" + moneda.getCodigo() + ")");
        }
    }
}
