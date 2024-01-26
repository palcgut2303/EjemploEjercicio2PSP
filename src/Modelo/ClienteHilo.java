/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.Cliente;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DAM
 */
public class ClienteHilo extends Thread {

    private static int intentos = 0;
    private static int premios = 0;
    Cliente clienteVista;
    private String mensajeUser;
    Socket socketCliente = null;
    BufferedReader entrada = null;
    PrintWriter salida = null;
    String idRecibido = "";
    ObjectInputStream objectInputStream = null;
    boolean register = false;

    public ClienteHilo(Cliente clienteVista) {
        this.clienteVista = clienteVista;
        try {
            // Establecer la conexión con el servidor
            socketCliente = new Socket("localhost", 4444);

            // Inicializar los canales de entrada y salida
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(socketCliente.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            try {
                empezarCliente(entrada,salida);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void empezarCliente(BufferedReader entrada,PrintWriter salida) throws IOException, ClassNotFoundException, InterruptedException {

        // Creamos un socket en el lado cliente, enlazado con un
        // servidor que está en la misma máquina que el cliente
        // y que escucha en el puerto 4444
        while (true) {
            try {
                
                String idRecibido = "";
                // La envia al servidor por el OutputStream
                if (!register) {

                    String conectado = "REGISTER";
                    salida.println(conectado);
                    register = true;
                    // Recibe la respuesta del servidor por el InputStream
                    idRecibido = entrada.readLine();
                    // Envía a la salida estándar la respuesta del servidor
                    System.out.println("Respuesta servidor: " + idRecibido);
                    clienteVista.escribirId(idRecibido);

                }
                String mensajeDelServidor = entrada.readLine();
                if (mensajeDelServidor != null) {

                    String[] partes = obtenerArrayDesdeMensaje(mensajeDelServidor);
                    clienteVista.appendTextArea(partes[0], partes[1], partes[2]);

                    
                }
            } catch (IOException e) {
                System.err.println("No puede establecer canales de E/S para la conexión" + e.getMessage());
                System.exit(-1);
            }
        }

        // Método que devuelve un array de strings a partir de un mensaje
    }

    private static String[] obtenerArrayDesdeMensaje(String mensaje) {
        // Dividir el mensaje utilizando la coma como delimitador
        return mensaje.split(",");
    }

    public void enviarMensajeAlServidor(String mensaje) {
        PrintWriter salida;
        try {
            salida = new PrintWriter(socketCliente.getOutputStream(), true);
            if (salida != null) {
            salida.println(mensaje);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
