package Model;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import clasesformato.Activo;
import clasesformato.ActivosCripto;
import clasesformato.ActivosFiat;
import clasesformato.Criptomoneda;
import clasesformato.Fiat;
import clasesformato.Moneda;
import dao.ActivoBD;
import dao.MonedaBD;
import dao.TransaccionBD;

public class DataBaseManager {
    private Connection connection;

    public DataBaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:walletDB.db");
            creaciónDeTablasEnBD(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
    * Este método se encarga de la creación de las tablas.
    *
    * @param connection objeto conexion a la base de datos SQLite
    * @throws SQLException
    */
    private static void creaciónDeTablasEnBD(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();

        // Tabla PERSONA
        String sql = """
            CREATE TABLE IF NOT EXISTS PERSONA (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                NOMBRES VARCHAR(50) NOT NULL,
                APELLIDOS VARCHAR(50) NOT NULL
            );
            """;
        stmt.executeUpdate(sql);

        // Tabla USUARIO
        sql = """
            CREATE TABLE IF NOT EXISTS USUARIO (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                ID_PERSONA INTEGER NOT NULL,
                EMAIL VARCHAR(50) UNIQUE NOT NULL,
                PASSWORD VARCHAR(50) NOT NULL,
                ACEPTA_TERMINOS BOOLEAN NOT NULL,
                FOREIGN KEY (ID_PERSONA) REFERENCES PERSONA(ID)
            );
            """;
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE  IF NOT EXISTS MONEDA "
    			+ "("
    			+ " ID       INTEGER   PRIMARY KEY AUTOINCREMENT NOT NULL , "
    			+ " TIPO       VARCHAR(1)    NOT NULL, "
    			+ " NOMBRE       VARCHAR(50)    NOT NULL, "
    			+ " NOMENCLATURA VARCHAR(10)  NOT NULL, "
    			+ " VALOR_DOLAR	REAL     NOT NULL, "
    			+ " VOLATILIDAD	REAL     NULL, "
    			+ " STOCK	REAL     NULL, "
    			+ " NOMBRE_ICONO       VARCHAR(50)    NOT NULL "
    			+ ")";
    	stmt.executeUpdate(sql);
    	agregarColumnaSiNoExiste(connection, "MONEDA", "STOCK", "REAL");
        // Tabla ACTIVO_CRIPTO
        sql = """
            CREATE TABLE IF NOT EXISTS ACTIVO_CRIPTO (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                ID_USUARIO INTEGER NOT NULL,
                ID_MONEDA INTEGER NOT NULL,
                CANTIDAD REAL NOT NULL,
                FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID),
                FOREIGN KEY (ID_MONEDA) REFERENCES MONEDA(ID)
            );
            """;
        stmt.executeUpdate(sql);

        // Tabla ACTIVO_FIAT
        sql = """
            CREATE TABLE IF NOT EXISTS ACTIVO_FIAT (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                ID_USUARIO INTEGER NOT NULL,
                ID_MONEDA INTEGER NOT NULL,
                CANTIDAD REAL NOT NULL,
                FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID),
                FOREIGN KEY (ID_MONEDA) REFERENCES MONEDA(ID)
            );
            """;
        stmt.executeUpdate(sql);

        // Tabla TRANSACCION
        sql = """
            CREATE TABLE IF NOT EXISTS TRANSACCION (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                ID_USUARIO INTEGER NOT NULL,
                RESUMEN VARCHAR(1000) NOT NULL,
                FECHA_HORA DATETIME NOT NULL,
                FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID)
            );
            """;
        stmt.executeUpdate(sql);

        System.out.println("Tablas creadas/verificadas correctamente.");
        stmt.close();
    }

 // Método auxiliar para agregar una columna si no existe
    private static void agregarColumnaSiNoExiste(Connection connection, String tabla, String columna, String tipo) throws SQLException {
        String sql = "PRAGMA table_info(" + tabla + ");";
        boolean columnaExiste = false;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombreColumna = rs.getString("name");
                if (columna.equalsIgnoreCase(nombreColumna)) {
                    columnaExiste = true;
                    break;
                }
            }
        }

        if (!columnaExiste) {
            // Agregar la columna si no existe
            sql = "ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + tipo;
            try (PreparedStatement alterStmt = connection.prepareStatement(sql)) {
                alterStmt.executeUpdate();
                System.out.println("Columna agregada: " + columna + " en tabla: " + tabla);
            }
        }
    }
    
    public void crearMoneda(Scanner scanner) {
        System.out.print("Ingrese el tipo de moneda (Cripto o FIAT): ");
        String tipo = scanner.nextLine();
        if (!tipo.equalsIgnoreCase("Cripto") && !tipo.equalsIgnoreCase("FIAT")) {
            System.out.println("Tipo inválido. Solo se permite 'Cripto' o 'FIAT'.");
            return;
        }

        System.out.print("Ingrese el nombre de la moneda: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese la nomenclatura de la moneda: ");
        String nomenclatura = scanner.nextLine();

        System.out.print("Ingrese el valor en dólares: ");
        double valorDolar = scanner.nextDouble();

        System.out.print("Ingrese el stock disponible: ");
        double stock = scanner.nextDouble();

        System.out.println("¿Confirmar la creación de la moneda? (s/n)");
        String confirmacion = scanner.next();
        if (confirmacion.equalsIgnoreCase("s")) {
        	Moneda moneda;
        	if (tipo.equals("Cripto")) {
        		moneda = new Criptomoneda(nombre, nomenclatura, valorDolar, 0, stock);
        	}else {
        		moneda = new Fiat(nombre, nomenclatura, valorDolar, 0, stock);
        	}
            new MonedaBD(connection).create(connection, tipo, moneda);
        } else {
        	System.out.println("Creacion de moneda cancelada");
        }
    }
    public void listarMonedas(Scanner scanner) {
        System.out.println("Ingrese el criterio de listado, valordolar o nomenclatura");
        String option = scanner.nextLine();
        ArrayList<Moneda> monedas = new MonedaBD(connection).read(connection, option);
    	if (!monedas.isEmpty()) {
	        for (Moneda moneda : monedas) {
	            System.out.println(moneda.toString());
	        }
    	}
    }
    public void generarStockAleatorio() {
    	new MonedaBD(connection).updateStockAleatorio(connection);
    }
    public void listarStock(Scanner scanner) {
        System.out.println("Ingrese el criterio de listado, Stock o Nomenclatura");
    	String option = scanner.nextLine();
    	ArrayList<Moneda> monedas = new MonedaBD(connection).readStock(connection, option);
    	if (!monedas.isEmpty()) {
    		if (option.equalsIgnoreCase("stock")) {
                monedas.sort(Comparator.comparingDouble(Moneda::getStock).reversed());
            } else if (option.equalsIgnoreCase("nomenclatura")) {
                monedas.sort(Comparator.comparing(Moneda::getNomenclatura));
            }

            for (Moneda moneda : monedas) {
                System.out.println(moneda.mostrarStock());
            }
    	}
    }
    public void generarMisActivos(Scanner scanner) {
    	String option;
    	boolean continuar = true;
        while (continuar) {
        	System.out.println("Ingrese el tipo de activo");
        	option = scanner.nextLine();
        	System.out.print("Ingrese la nomenclatura de la moneda: ");
            String nomenclatura = scanner.nextLine();
            System.out.print("Ingrese la cantidad de la moneda: ");
            double cantidad = scanner.nextDouble();
            scanner.nextLine();

            option.toUpperCase();
            new ActivoBD().create(connection, option, nomenclatura, cantidad);
            
        	System.out.println("Desea seguir ingresando activos s/n?");
        	option = scanner.nextLine();
        	if (option.equals("n")) {
        		continuar = false;
        	}
        }
    } 
    public void listarMisActivos(Scanner scanner) {
        System.out.println("Ingrese el criterio de listado, cantidad o nomenclatura");
        String option = scanner.nextLine();
    	List<Activo> activos = new ActivoBD().read(connection, option, "FIAT");
    	activos.addAll(new ActivoBD().read(connection, option, "CRIPTO"));
    	if (!activos.isEmpty()) {	    
	        activos.sort((a, b) -> Double.compare(b.getStock(), a.getStock()));

	        for (Activo activo : activos) {
	            System.out.println(activo);
	        }
    	}else {
    		System.out.println("Error al listar activos fiat: ");
    	}
    	
    }
    public void comprarCriptomoneda(Scanner scanner) {        
        System.out.print("Ingrese la nomenclatura de la criptomoneda a comprar: ");
        String nomenclaturaCripto = scanner.nextLine();
        
        System.out.print("Ingrese la nomenclatura de la moneda FIAT: ");
        String nomenclaturaFiat = scanner.nextLine();
        
        System.out.print("Ingrese la cantidad disponible en " + nomenclaturaFiat + ": ");
        double cantidadFiat = scanner.nextDouble();
        
        if (cantidadFiat <= 0) {
            System.out.println("La cantidad debe ser mayor a 0.");
            return;
        }
        
        MonedaBD moneda = new MonedaBD(connection);
        ActivoBD activo = new ActivoBD();
        TransaccionBD transaccion = new TransaccionBD(connection);
        
        
        List<Activo> activosFiat = activo.read(connection, "nomenclatura", "FIAT");
        double cantidadDisponibleFiat = activosFiat.stream()
        	    .filter(activoLambda -> {
        	        if (activoLambda instanceof ActivosFiat) {
        	            ActivosFiat activoFiat = (ActivosFiat) activoLambda;
        	            return activoFiat.getNomenclatura().equals(nomenclaturaFiat);
        	        }
        	        return false;
        	    })
        	    .mapToDouble(Activo::getStock)
        	    .sum();
        if (cantidadFiat > cantidadDisponibleFiat) {
            System.out.println("No tienes suficiente " + nomenclaturaFiat + " para realizar la compra.");
            return;
        }
        double valorDolarFiat = moneda.readValorEnDolar(connection, nomenclaturaFiat);
        double valorCripto = moneda.readValorEnDolar(connection, nomenclaturaCripto);
        if (valorDolarFiat <= 0 || valorCripto <= 0) {
            System.out.println("Valor de la moneda no válido.");
            return;
        }

        double cantidadCripto = cantidadFiat / (valorCripto * valorDolarFiat);

        boolean existeActivoCripto = activo.verificarActivo(connection, "CRIPTO", nomenclaturaCripto);
        if (!existeActivoCripto) {
            activo.create(connection, "CRIPTO", nomenclaturaCripto, cantidadCripto);
        } else {
            activo.update(connection, "CRIPTO", nomenclaturaCripto, cantidadCripto);
        }

        activo.update(connection, "FIAT", nomenclaturaFiat, -cantidadFiat);

        transaccion.create(connection, nomenclaturaCripto, cantidadCripto, nomenclaturaFiat, cantidadFiat);

        System.out.println("Compra realizada exitosamente: " + cantidadCripto + " " + nomenclaturaCripto + " por " + cantidadFiat + " " + nomenclaturaFiat);
    }
    public void swap(Scanner scanner) {
        System.out.print("Ingrese la nomenclatura de la criptomoneda a convertir: ");
        String nomenclaturaOrigen = scanner.nextLine();
        
        System.out.print("Ingrese la cantidad a convertir: ");
        double cantidad = scanner.nextDouble();
        
        System.out.print("Ingrese la nomenclatura de la criptomoneda esperada: ");
        String nomenclaturaDestino = scanner.next();

        MonedaBD moneda = new MonedaBD(connection);
        ActivoBD activo = new ActivoBD();
        TransaccionBD transaccion = new TransaccionBD(connection);
        
        if (!activo.verificarActivo(connection, "CRIPTO", nomenclaturaOrigen)) {
            System.out.println("No tienes la criptomoneda a convertir.");
            return;
        }

        if (!activo.verificarActivo(connection, "CRIPTO", nomenclaturaDestino)) {
            System.out.println("No tienes la criptomoneda esperada.");
            return;
        }

        List<Activo> activosConvertir = activo.read(connection, "nomenclatura", "CRIPTO");
        List<Activo> activosEsperada = activo.read(connection, "nomenclatura", "CRIPTO");

        Activo activoConvertir = activosConvertir.stream()
        	    .filter(activoLambda -> activoLambda instanceof ActivosCripto && ((ActivosCripto) activoLambda).getNomenclatura().equals(nomenclaturaOrigen))
        	    .findFirst()
        	    .orElse(null);

        	Activo activoEsperada = activosEsperada.stream()
        	    .filter(activoLambda -> activoLambda instanceof ActivosCripto && ((ActivosCripto) activoLambda).getNomenclatura().equals(nomenclaturaDestino))
        	    .findFirst()
        	    .orElse(null);


        if (activoConvertir == null || activoEsperada == null) {
            System.out.println("Error al encontrar las criptomonedas en los activos.");
            return;
        }

        double tipoCambio = moneda.readValorEnDolar(connection, nomenclaturaOrigen) / moneda.readValorEnDolar(connection, nomenclaturaDestino);

        if (activoConvertir.getStock() < cantidad) {
            System.out.println("No tienes suficiente cantidad de " + nomenclaturaOrigen + " para realizar el swap.");
            return;
        }

        double cantidadEsperada = cantidad * tipoCambio;

        activo.update(connection, "CRIPTO", nomenclaturaOrigen, -cantidad);
        activo.update(connection, "CRIPTO", nomenclaturaDestino, cantidadEsperada);

        transaccion.create(connection, nomenclaturaOrigen, cantidad, nomenclaturaDestino, cantidadEsperada);

        System.out.println("Swap realizado con éxito: " + cantidad + " " + nomenclaturaOrigen + " por " + cantidadEsperada + " " + nomenclaturaDestino);
    }
    public void limpiarBaseDeDatos() {
    	new MonedaBD(connection).delete(connection);
    	new ActivoBD().delete(connection,"Cripto");
    	new ActivoBD().delete(connection,"Fiat");
    	new TransaccionBD(connection).delete(connection);
    }
}

