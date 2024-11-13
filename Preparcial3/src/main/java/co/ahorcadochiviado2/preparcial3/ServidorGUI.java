package co.ahorcadochiviado2.preparcial3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorGUI extends Application {

    private TextArea textArea;

    // Usamos un pool de hilos para manejar múltiples clientes concurrentes
    private static final int PUERTO = 8080;
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Crear la interfaz gráfica
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefSize(600, 400);

        Button startButton = new Button("Iniciar Servidor");
        startButton.setOnAction(event -> iniciarServidor());

        VBox vbox = new VBox(10, startButton, textArea);
        Scene scene = new Scene(vbox, 650, 500);

        primaryStage.setTitle("Servidor - Estación de Servicio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void iniciarServidor() {
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
                appendText("Servidor escuchando en el puerto " + PUERTO);

                while (true) {
                    Socket clienteSocket = serverSocket.accept();
                    appendText("Nuevo cliente conectado: " + clienteSocket.getInetAddress());
                    pool.submit(new HiloCliente2(clienteSocket, textArea));
                }
            } catch (IOException e) {
                appendText("Error al iniciar el servidor: " + e.getMessage());
            }
        });
        serverThread.start();
    }

    private void appendText(String text) {
        // Usar el hilo de la interfaz gráfica para actualizar el TextArea
        javafx.application.Platform.runLater(() -> textArea.appendText(text + "\n"));
    }
}

class HiloCliente2 implements Runnable {
    private Socket clienteSocket;
    private TextArea textArea;

    public HiloCliente2(Socket socket, TextArea textArea) {
        this.clienteSocket = socket;
        this.textArea = textArea;
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
            appendText("Datos recibidos: " + datosTanqueo);

            // Procesar y almacenar los datos
            guardarDatosTanqueo(datosTanqueo);

            // Enviar una respuesta al cliente
            writer.println("Registro de tanqueo exitoso.");
        } catch (IOException e) {
            appendText("Error al procesar la conexión: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void guardarDatosTanqueo(String datos) {
        try (FileWriter fw = new FileWriter("registros_tanqueo.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(datos);
            bw.newLine();
        } catch (IOException e) {
            appendText("Error al guardar los datos: " + e.getMessage());
        }
    }

    private void appendText(String text) {
        javafx.application.Platform.runLater(() -> textArea.appendText(text + "\n"));
    }
}
