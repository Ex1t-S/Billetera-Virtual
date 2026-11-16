# Billetera Virtual

Aplicación de escritorio en **Java 17 y Swing** para gestionar activos FIAT y criptomonedas, consultar balances y registrar operaciones. Proyecto académico de **Taller de Lenguajes II**, desarrollado por German Arroyo.

[Instalación](#instalación-y-ejecución) · [Arquitectura](#arquitectura) · [Limitaciones](#limitaciones-y-próximas-mejoras) · [Licencia MIT](LICENSE)

## Funcionalidades

- Registro e inicio de sesión.
- Gestión de activos FIAT y cripto.
- Consulta de cotizaciones de criptomonedas mediante una API externa.
- Compra, venta, transferencias e historial de transacciones.
- Consulta del balance en distintas monedas.

## Tecnologías

| Capa | Tecnología |
| --- | --- |
| Lenguaje | Java 17 |
| Interfaz | Swing |
| Persistencia | SQLite y JDBC |
| Integraciones | Cliente HTTP de Java y JSON |
| Organización | MVC, DAO y programación orientada a objetos |

## Instalación y ejecución

### Requisitos

- JDK 17, versión configurada en el proyecto.
- Un IDE con soporte para Java, por ejemplo Eclipse o IntelliJ IDEA.
- Conexión a internet para consultar cotizaciones externas.

### Preparar el proyecto

```bash
git clone https://github.com/Ex1t-S/Billetera-Virtual.git
cd Billetera-Virtual/BilleteraProject
```

1. Importar `BilleteraProject` como proyecto Java en el IDE.
2. Seleccionar JDK 17.
3. Configurar las dependencias con los archivos incluidos en esa carpeta:
   - `sqlite-jdbc-3.47.0.0.jar`
   - `json-20240303.jar`
4. El archivo `.classpath` original contiene rutas absolutas de la computadora de desarrollo. Reemplazarlas en el IDE por los JAR locales. El descriptor `src/module-info.java` requiere `org.json`; configurar ese JAR en el module path cuando el IDE lo solicite.
5. Establecer `BilleteraProject` como directorio de trabajo. La conexión SQLite usa la ruta relativa `jdbc:sqlite:walletDB.db`.
6. Ejecutar la clase principal [`Wallet.Wallet`](BilleteraProject/src/Wallet/Wallet.java).

La aplicación inicializa la base mediante `DataBaseManager` al arrancar. Para probarla, usar una copia local y crear un usuario desde el registro.

## Arquitectura

| Directorio | Responsabilidad |
| --- | --- |
| [`src/View`](BilleteraProject/src/View) | Pantallas Swing |
| [`src/Controller`](BilleteraProject/src/Controller) | Controladores de acceso y registro |
| [`src/Wallet`](BilleteraProject/src/Wallet) | Inicio y coordinación de la aplicación |
| [`src/Model`](BilleteraProject/src/Model) | Conexión, inicialización y consulta de precios |
| [`src/dao`](BilleteraProject/src/dao) | Acceso a datos |
| [`src/clasesformato`](BilleteraProject/src/clasesformato) | Entidades del dominio |

## Limitaciones y próximas mejoras

- El proyecto tiene fines educativos; no constituye una billetera financiera de producción.
- Si una operación no se refleja en el balance, usar “Actualizar” o volver a iniciar sesión.
- La configuración de dependencias todavía requiere pasos manuales en el IDE.
- Próximas mejoras: automatizar la compilación, agregar pruebas unitarias y mejorar la actualización del balance y la interfaz.

## Autor y licencia

German Arroyo · [Perfil de GitHub](https://github.com/Ex1t-S)  
Distribuido bajo [licencia MIT](LICENSE).
