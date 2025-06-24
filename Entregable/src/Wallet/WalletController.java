package Wallet;

import View.*;
import dao.*;
import Model.ConexionBD;
import Model.ConsultarPrecioCripto;
import Model.DataBaseManager;
import clasesformato.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import org.json.*;
import javax.swing.JOptionPane;
public class WalletController {
    private LoginView loginView;
    private BalanceView balanceView;
    private Usuario usuarioActual; // Usuario autenticado
    private Connection connection;
    private ActivoCriptoDAO activosCriptoDAO; // DAO para activos cripto
    private ActivoFiatDAO activoFiatDAO;     // DAO para activos fiat
    private UsuarioDAO usuarioDAO;           // DAO para usuarios
    private RegisterView registerView;
    private TransaccionDAO transaccionDAO;
    private MonedaDAO monedaDAO;
    public WalletController() {
        // Obtener conexión a la base de datos
        connection = ConexionBD.getConnection();
        
        // Inicializar los DAOs
        activosCriptoDAO = new ActivoCriptoBD(connection);
        activoFiatDAO = new ActivoFiatBD(connection);
        usuarioDAO = new UsuarioBD(connection);
        transaccionDAO = new TransaccionBD(connection); 
        monedaDAO = new MonedaBD(connection);
    }

    public void startApp() {
        showLoginView(); // Mostrar la ventana de Login
    }
    public void eliminarDuplicadosMoneda() {
        String sql = "DELETE FROM MONEDA WHERE ROWID NOT IN (SELECT MIN(ROWID) FROM MONEDA GROUP BY NOMBRE)";

        try (Statement stmt = connection.createStatement()) {
            int filasEliminadas = stmt.executeUpdate(sql);
            System.out.println("Duplicados eliminados: " + filasEliminadas);
        } catch (SQLException e) {
            System.err.println("Error al eliminar duplicados: " + e.getMessage());
        }
    }
    private void showLoginView() {
        loginView = new LoginView();
        loginView.setVisible(true);

        // Listener para el botón de Iniciar Sesión
        loginView.addLoginListener(e -> {
            String email = loginView.getEmail();
            String password = loginView.getPassword();

            // Verificar usuario en la base de datos
            usuarioActual = usuarioDAO.readByEmailAndPassword(connection, email, password);

            if (usuarioActual != null) {
                loginView.mostrarMensaje("Inicio de sesión exitoso.");
                loginView.dispose(); // Cerrar la ventana de Login
                showBalanceView();   // Ir a BalanceView
            } else {
                loginView.mostrarMensaje("Usuario o contraseña incorrectos.");
            }
        });

        // Listener para el botón de Registrarse
        loginView.addRegisterListener(e -> {
            loginView.dispose(); // Cerrar la ventana de Login
            showRegisterView();  // Mostrar la ventana de Registro
        });
    }


    private void showRegisterView() {
        registerView = new RegisterView();
        registerView.setVisible(true);

        registerView.setOnRegisterListener(userData -> {
            if (userData.length < 5) {
                registerView.mostrarError("Error interno: Datos incompletos.");
                return;
            }

            String nombres = userData[0];
            String apellidos = userData[1];
            String email = userData[2];
            String password = userData[3];
            boolean aceptaTerminos = Boolean.parseBoolean(userData[4]);

            if (!aceptaTerminos) {
                registerView.mostrarError("Debe aceptar los términos y condiciones.");
                return;
            }

            if (usuarioDAO.emailExiste(connection, email)) {
                registerView.mostrarError("El email ya está registrado.");
                return;
            }

            Persona nuevaPersona = new Persona(0, nombres, apellidos);
            PersonaDAO personaDAO = new PersonaBD();
            if (personaDAO.create(connection, nuevaPersona)) {
                Usuario nuevoUsuario = new Usuario(0, nuevaPersona.getId(), email, password, aceptaTerminos);
                if (usuarioDAO.create(connection, nuevoUsuario)) {
                    registerView.mostrarExito("Usuario registrado exitosamente.");
                    registerView.dispose(); // Cerrar la ventana de Registro

                    // Crear y mostrar una nueva instancia de LoginView
                    showLoginView();
                } else {
                    registerView.mostrarError("Error al registrar el usuario.");
                }
            } else {
                registerView.mostrarError("Error al registrar la persona.");
            }
        });
    }
    private void showBalanceView() {
        try {
            actualizarCotizacionesEnBaseDeDatos();

            List<Object[]> activosCripto = activosCriptoDAO.getActivosByUsuario(usuarioActual.getId());
            List<Object[]> activosFiat = activoFiatDAO.getActivosByUsuario(usuarioActual.getId());

            Map<String, Double> valoresDolar = monedaDAO.obtenerValoresDolar();

            List<Object[]> balanceCompleto = new ArrayList<>();
            for (Object[] activo : activosCripto) {
                String nombreActivo = (String) activo[0];
                double cantidad = (double) activo[1];
                double valorDolar = valoresDolar.getOrDefault(nombreActivo, 0.0);
                double valorTotal = cantidad * valorDolar;
                balanceCompleto.add(new Object[]{nombreActivo, cantidad, valorTotal});
            }
            for (Object[] activo : activosFiat) {
                String nombreActivo = (String) activo[0];
                double cantidad = (double) activo[1];
                double valorDolar = valoresDolar.getOrDefault(nombreActivo, 0.0);
                double valorTotal = cantidad * valorDolar;
                balanceCompleto.add(new Object[]{nombreActivo, cantidad, valorTotal});
            }

            // Cerrar LoginView si aún está visible
            if (loginView != null && loginView.isVisible()) {
                loginView.dispose();
            }

            // Cerrar cualquier ventana previa de BalanceView si existe
            if (balanceView != null) {
                balanceView.dispose();
            }

            balanceView = new BalanceView(
                    balanceCompleto,
                    this::generarSaldoAleatorio,
                    this::showTransaccionesView,
                    this::showOperacionesView,
                    this::cerrarSesion
            );
            balanceView.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener el balance.");
        }
    }
    private void cerrarSesion() {
        balanceView.dispose(); // Cerrar la ventana de BalanceView
        showLoginView(); // Mostrar nuevamente la ventana de Login
    }
    private void generarSaldoAleatorio() {
        Random random = new Random();
        try {
            for (MonedaEnum moneda : MonedaEnum.values()) {
                double cantidadAleatoria;

                if (moneda == MonedaEnum.PESO || moneda == MonedaEnum.DOLAR) {
                    cantidadAleatoria = 150000 + (5000 * random.nextDouble());
                    if (activoFiatDAO.existeActivoFiat(connection, usuarioActual.getId(), moneda.ordinal())) {
                        activoFiatDAO.actualizarSaldo(connection, usuarioActual.getId(), moneda.ordinal(), cantidadAleatoria);
                    } else {
                        activoFiatDAO.create(connection, usuarioActual.getId(), moneda.ordinal(), cantidadAleatoria);
                    }
                } else {
                    cantidadAleatoria = 1 + (100 * random.nextDouble());
                    if (activosCriptoDAO.existeActivoCripto(connection, usuarioActual.getId(), moneda.ordinal())) {
                        activosCriptoDAO.actualizarSaldo(connection, usuarioActual.getId(), moneda.ordinal(), cantidadAleatoria);
                    } else {
                        activosCriptoDAO.create(connection, usuarioActual.getId(), moneda.ordinal(), cantidadAleatoria);
                    }
                }
            }

            limpiarDuplicadosActivos(); // Eliminar duplicados
            JOptionPane.showMessageDialog(balanceView, "Saldos generados exitosamente.");
            showBalanceView(); // Refrescar la vista
        } catch (Exception e) {
            JOptionPane.showMessageDialog(balanceView, "Error al generar saldos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarDuplicadosActivos() {
        try {
            activoFiatDAO.eliminarDuplicadosFiat(connection);
            activosCriptoDAO.eliminarDuplicadosCripto(connection);
        } catch (SQLException e) {
            System.err.println("Error al eliminar duplicados: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void inicializarMonedas() {
        String sql = "INSERT OR IGNORE INTO MONEDA (TIPO, NOMBRE, NOMENCLATURA, VALOR_DOLAR, STOCK) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (MonedaEnum moneda : MonedaEnum.values()) {
                String tipo = (moneda == MonedaEnum.PESO || moneda == MonedaEnum.DOLAR) ? "FIAT" : "CRYPTO";
                double valorDolar = generarValorDolar(moneda); // Método auxiliar para valores simulados
                double stockInicial = 1_000_000.0;

                stmt.setString(1, tipo);
                stmt.setString(2, moneda.getNombre());
                stmt.setString(3, moneda.getCodigo());
                stmt.setDouble(4, valorDolar);
                stmt.setDouble(5, stockInicial);

                stmt.executeUpdate();
            }
            System.out.println("Monedas inicializadas correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al inicializar monedas: " + e.getMessage());
        }
    }

    // Método auxiliar para generar valores ficticios de las monedas
    private double generarValorDolar(MonedaEnum moneda) {
        return switch (moneda) {
            case BITCOIN -> 66960.0;
            case ETHEREUM -> 2478.33;
            case TETHER, USD_COIN -> 1.0;
            case DOGECOIN -> 0.1359;
            case PESO -> 0.0012;
            case DOLAR -> 1.0;
        };
    }
    private void showTransaccionesView() {
        try {
            // Obtener las transacciones desde la base de datos
            List<String[]> transacciones = transaccionDAO.getTransaccionesByUsuario(connection, usuarioActual.getId());

            // Crear y mostrar la vista de transacciones
            TransaccionView transaccionView = new TransaccionView(transacciones);
            transaccionView.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener las transacciones.");
        }
    }
    private void showOperacionesView() {
        OperacionesView operacionesView = new OperacionesView(() -> {
            balanceView.setVisible(true); // Volver a la ventana de BalanceView
        }, usuarioActual.getId(), transaccionDAO, activoFiatDAO, activosCriptoDAO, monedaDAO, connection);
        operacionesView.setVisible(true);
        balanceView.setVisible(false); // Ocultar la ventana de BalanceView
    }
    private void actualizarCotizacionesEnBaseDeDatos() {
        try {
            // Obtener cotizaciones como JSONObject
            JSONObject cotizaciones = ConsultarPrecioCripto.obtenerCotizacionesJSON();

			// Actualizar los valores en la base de datos
            monedaDAO.actualizarValorDolar(connection, "Bitcoin", cotizaciones.getJSONObject("bitcoin").getDouble("usd"));
            monedaDAO.actualizarValorDolar(connection, "Ethereum", cotizaciones.getJSONObject("ethereum").getDouble("usd"));
            monedaDAO.actualizarValorDolar(connection, "USD Coin", cotizaciones.getJSONObject("usd-coin").getDouble("usd"));
            monedaDAO.actualizarValorDolar(connection, "Tether", cotizaciones.getJSONObject("tether").getDouble("usd"));
            monedaDAO.actualizarValorDolar(connection, "Dogecoin", cotizaciones.getJSONObject("dogecoin").getDouble("usd"));

            System.out.println("Cotizaciones actualizadas correctamente en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar las cotizaciones.");
        }
    }
}
