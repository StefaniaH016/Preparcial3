package co.ahorcadochiviado2.preparcial3;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteGUI implements Runnable{

    private JFrame frame;
    private JTextField idCliente;
    private JTextField tipoVehiculo;
    private JTextField galones;
    private JLabel jlabel;
    private JButton JbuttonRegistro;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;


    public ClienteGUI() {
        frame = new JFrame();
        frame.setTitle("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setLayout(new FlowLayout());


        //PENDIENTE : CAMBIO DE POSICION (?)
        idCliente = new JTextfield();
        frame.add(idCliente);

        tipoVehiculo = new JTextField();
        frame.add(tipoVehiculo);

        galones = new JTextField();
        frame.add(galones);

        JbuttonRegistro = new JButton("Registro");
        frame.add(JbuttonRegistro);  //CAMBIO DE POSICION REQUERIDO(?)

        frame.setVisible(true);
    }

    public void conectarConServidor() {
        try {
            socket = new Socket("localhost", 12345);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
      conectarConServidor();
    }

    public static void main(String[] args) {
        ClienteGUI cliente = new ClienteGUI();
        ClienteGUI cliente2= new ClienteGUI();


        // Iniciar el hilo para ejecutar la conexión y comunicación
        Thread clienteThread = new Thread(cliente);
        clienteThread.start();

        Thread clienteThread2 = new Thread(cliente2);
        clienteThread2.start();
    }
}
