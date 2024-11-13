package co.ahorcadochiviado2.preparcial3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteGUI extends Application {

    private TextArea respuestaTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Campos de entrada
        TextField clienteIdField = new TextField();
        clienteIdField.setPromptText("Número de identificación del cliente");

        TextField tipoVehiculoField = new TextField();
        tipoVehiculoField.setPromptText("Tipo de vehículo");

        TextField cantidadGasolinaField = new TextField();
        cantidadGasolinaField.setPromptText("Cantidad de gasolina (en galones)");

        Button enviarButton = new Button("Enviar Registro");

        respuestaTextArea = new TextArea();
        respuestaTextArea.setEditable(false);

        enviarButton.setOnAction(event -> {
            String clienteId = clienteIdField.getText();
            String tipoVehiculo = tipoVehiculoField.getText();
            double cantidadGasolina = Double.parseDouble(cantidadGasolinaField.getText());

            // Obtener la fecha y hora actual
            String fechaTanqueo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String datosTanqueo = String.format("{\"cliente_id\": \"%s\", \"tipo_vehiculo\": \"%s\", \"cantidad_gasolina\": %.2f, \"fecha_tanqueo\": \"%s\"}",
                    clienteId, tipoVehiculo, cantidadGasolina, fechaTanqueo);

            // Enviar los datos al servidor
            enviarDatosAlServidor(datosTanqueo);
        });

        VBox vbox = new VBox(10, clienteIdField, tipoVehiculoField, cantidadGasolinaField, enviarButton, respuestaTextArea);
        Scene scene = new Scene(vbox, 400, 400);

        primaryStage.setTitle("Cliente - Estación de Servicio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void enviarDatosAlServidor(String datosTanqueo) {
        try (Socket socket = new Socket("localhost", 8080);
             OutputStream output = socket.getOutputStream();
             InputStream input = socket.getInputStream();
             PrintWriter writer = new PrintWriter(output, true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            // Enviar los datos al servidor
            writer.println(datosTanqueo);
            System.out.println("Datos enviados al servidor.");

            // Recibir la respuesta del servidor
            String respuesta = reader.readLine();
            respuestaTextArea.appendText("Respuesta del servidor: " + respuesta + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
