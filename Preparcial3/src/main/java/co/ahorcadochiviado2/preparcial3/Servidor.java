package co.ahorcadochiviado2.preparcial3;


import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Servidor {

    // Usamos un pool de hilos para manejar múltiples clientes concurrentes
    private static final int PUERTO = 8080;
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);

            while (true) {
                // Aceptar una nueva conexión de un cliente
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clienteSocket.getInetAddress());

                // Crear un nuevo hilo para manejar la conexión del cliente
                pool.submit(new HiloCliente(clienteSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HiloCliente implements Runnable {
    private Socket clienteSocket;

    public HiloCliente(Socket socket) {
        this.clienteSocket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream input = clienteSocket.getInputStream();
                OutputStream output = clienteSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true)
        ) {
            // Recibir los datos enviados por el cliente
            String datosTanqueo = reader.readLine();
            System.out.println("Datos recibidos: " + datosTanqueo);

            // Procesar y almacenar los datos
            guardarDatosTanqueo(datosTanqueo);

            // Enviar una respuesta al cliente
            writer.println("Registro de tanqueo exitoso.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para guardar los datos del tanqueo en un archivo
    private void guardarDatosTanqueo(String datos) {
        try (FileWriter fw = new FileWriter("registros_tanqueo.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(datos);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
