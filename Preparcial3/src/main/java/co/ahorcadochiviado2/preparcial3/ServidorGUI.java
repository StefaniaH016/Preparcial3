package co.ahorcadochiviado2.preparcial3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ServidorGUI implements Runnable{

    private JFrame frame;
    private JLabel jLabel;
    private ServerSocket serverSocket;
    private ArrayList<ClienteHandler> clientesConectados;

    public ServidorGUI() {
        frame = new JFrame("Servidor-Registro de tanqueo");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        jLabel = new JLabel("Registro: ");
        frame.add(jLabel);

        frame.setVisible(true);
    }

    public void conectarCliente() {
        try {
            serverSocket = new ServerSocket(12345);
            JOptionPane.showMessageDialog(frame, "Esperando Cliente...");

            while (true) {
                // Aceptar una nueva conexión de cliente
                Socket clientSocket = serverSocket.accept();
                JOptionPane.showMessageDialog(frame, "Cliente Conectado!");

                // Crear un nuevo manejador de cliente y agregarlo a la lista
                ClienteHandler clienteHandler = new ClienteHandler(clientSocket);
                clientesConectados.add(clienteHandler);

                // Iniciar un nuevo hilo para el cliente
                Thread clienteThread = new Thread(clienteHandler);
                clienteThread.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void guardarDatosTanqueo(String datos) {
        try (FileWriter fw = new FileWriter("registros_tanqueo.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(datos);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClienteHandler implements Runnable{

        private Socket clientSocket;
        private BufferedReader input;
        private PrintWriter output;

        public ClienteHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;

            try{
                this.input= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.output = new PrintWriter(clientSocket.getOutputStream(),true);

                String datosTanqueo = input.readLine();


            }catch(IOException e){
                e.printStackTrace();
            }

        }

        public void run() {



        }


        public void cerrarConexion() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void enviarMensaje(String s) {
            output.println(s);
        }
    }



    @Override
    public void run() {
        conectarCliente();
    }

    public static void main(String[] args) {
        ServidorGUI servidor = new ServidorGUI();

        // Iniciar el servidor en un hilo aparte
        Thread serverThread = new Thread(servidor);
        serverThread.start();
    }
}
