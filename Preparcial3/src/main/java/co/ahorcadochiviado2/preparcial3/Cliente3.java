package co.ahorcadochiviado2.preparcial3;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Cliente3 {

    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar datos del tanqueo
        System.out.println("Ingrese el número de identificación del cliente:");
        String clienteId = scanner.nextLine();

        System.out.println("Ingrese el tipo de vehículo (automóvil, motocicleta, camioneta, etc.):");
        String tipoVehiculo = scanner.nextLine();

        System.out.println("Ingrese la cantidad de gasolina cargada (en galones):");
        double cantidadGasolina = scanner.nextDouble();

        // Obtener la fecha y hora actual
        String fechaTanqueo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Crear un objeto de datos para el tanqueo
        String datosTanqueo = String.format("{\"cliente_id\": \"%s\", \"tipo_vehiculo\": \"%s\", \"cantidad_gasolina\": %.2f, \"fecha_tanqueo\": \"%s\"}",
                clienteId, tipoVehiculo, cantidadGasolina, fechaTanqueo);

        // Conectar al servidor
        try (Socket socket = new Socket(SERVIDOR, PUERTO);
             OutputStream output = socket.getOutputStream();
             InputStream input = socket.getInputStream();
             PrintWriter writer = new PrintWriter(output, true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            // Enviar los datos al servidor
            writer.println(datosTanqueo);
            System.out.println("Datos enviados al servidor.");

            // Recibir la respuesta del servidor
            String respuesta = reader.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
