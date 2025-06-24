package Model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class ConsultarPrecioCripto {
    private static final String URL_API = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,usd-coin,tether,dogecoin&vs_currencies=usd";

    // Método reutilizable que obtiene y devuelve las cotizaciones como String formateado
    public static String obtenerPrecios() throws IOException, InterruptedException {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(URL_API))
                .GET()
                .build();

        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() == 200) {
            return parsearPrecios(respuesta.body());
        } else {
            throw new IOException("Error en la API: " + respuesta.statusCode());
        }
    }

    // Nuevo método para devolver el JSON crudo
    public static JSONObject obtenerCotizacionesJSON() throws IOException, InterruptedException {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(URL_API))
                .GET()
                .build();

        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() == 200) {
            return new JSONObject(respuesta.body());
        } else {
            throw new IOException("Error en la API: " + respuesta.statusCode());
        }
    }

    // Método privado que parsea y devuelve un String formateado
    private static String parsearPrecios(String cuerpoRespuesta) {
        JSONObject json = new JSONObject(cuerpoRespuesta);
        StringBuilder builder = new StringBuilder();
        builder.append("Precios de Criptomonedas (USD):\n");
        builder.append("Bitcoin (BTC): $").append(json.getJSONObject("bitcoin").getDouble("usd")).append("\n");
        builder.append("Ethereum (ETH): $").append(json.getJSONObject("ethereum").getDouble("usd")).append("\n");
        builder.append("USD Coin (USDC): $").append(json.getJSONObject("usd-coin").getDouble("usd")).append("\n");
        builder.append("Tether (USDT): $").append(json.getJSONObject("tether").getDouble("usd")).append("\n");
        builder.append("Dogecoin (DOGE): $").append(json.getJSONObject("dogecoin").getDouble("usd")).append("\n");

        return builder.toString();
    }
}
