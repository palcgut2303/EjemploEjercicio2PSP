package Modelo;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DAM
 */
class HiloServidor implements Runnable {

    private final static String COD_TEXTO = "UTF-8";
    private final Socket socketComunicacion;
    private final Tablero tablero;
    private int id = 1;

    HiloServidor(Socket socketComunicacion, Tablero miTablero) {
        this.socketComunicacion = socketComunicacion;
        this.tablero = miTablero;
    }

    @Override
    public void run() {
        try {
            empezarServidor();
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void empezarServidor() throws InterruptedException {
        while (true) {
            try ( InputStream isDeCliente = this.socketComunicacion.getInputStream();  OutputStream osACliente = this.socketComunicacion.getOutputStream();  InputStreamReader isrDeCliente = new InputStreamReader(isDeCliente, COD_TEXTO);  BufferedReader brDeCliente = new BufferedReader(isrDeCliente);  OutputStreamWriter oswACliente = new OutputStreamWriter(osACliente, COD_TEXTO);  BufferedWriter bwACliente = new BufferedWriter(oswACliente)) {

                String lineaRecibida;
                while ((lineaRecibida = brDeCliente.readLine()) != null && lineaRecibida.length() > 0) {

                    if (lineaRecibida.equals("REGISTER")) {
                        enviarMensajeAlCliente(bwACliente, id++);

                    } else {
                        System.out.println("Recibido: " + lineaRecibida);
                        //Comprobar en el tablero si hay premio
                        int[] coordenadas = obtenerCoordenadasDesdeMensaje(lineaRecibida);
                        int fila = 0;
                        int columna = 0;
                        if (coordenadas != null) {
                            fila = coordenadas[0];
                            columna = coordenadas[1];
                        } else {
                            System.out.println("El mensaje no tiene el formato correcto.");
                        }
                        String premio = tablero.obtenerPremio(fila, columna);
                        if (!premio.equalsIgnoreCase("")) {
                            bwACliente.write(fila + "," + columna + "," + premio);

                            bwACliente.newLine();
                            bwACliente.flush();
                            //enviarMensajeAlClienteString(bwACliente, fila + "," + columna + "," + premio);
                        } else {
                            bwACliente.write(fila + "," + columna + "," + "SIN PREMIO");

                            bwACliente.newLine();
                            bwACliente.flush();
                            //enviarMensajeAlClienteString(bwACliente, fila + "," + columna + "," + premio);
                        }

                    }
                }
            } catch (IOException ex) {
                System.out.println("Excepción de E/S");
                ex.printStackTrace();
                System.exit(1);
            }
        }

    }

    private static void enviarMensajeAlCliente(BufferedWriter bwACliente, int mensaje) {
        try {
            bwACliente.write(String.valueOf(mensaje));
            bwACliente.newLine();
            bwACliente.flush();
            System.out.println("Mensaje enviado al cliente: " + mensaje);
        } catch (IOException ex) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void enviarMensajeAlClienteString(BufferedWriter bwACliente, String mensaje) {
        try {
            bwACliente.write(mensaje);
            bwACliente.newLine();
            bwACliente.flush();
            System.out.println("Mensaje enviado al cliente: " + mensaje);
        } catch (IOException ex) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para obtener las coordenadas desde un mensaje
    private static int[] obtenerCoordenadasDesdeMensaje(String mensaje) {
        try {
            // Dividir el mensaje en partes usando el espacio como delimitador
            String[] partes = mensaje.split(" ");

            // Verificar si hay dos partes en el mensaje (fila y columna)
            if (partes.length == 2) {
                // Convertir las partes a enteros
                int fila = Integer.parseInt(partes[0]);
                int columna = Integer.parseInt(partes[1]);

                // Devolver las coordenadas como un array
                return new int[]{fila, columna};
            }
        } catch (NumberFormatException e) {
            // Manejar la excepción si las partes no son números enteros
            e.printStackTrace();
        }

        // Devolver null si el mensaje no tiene el formato correcto
        return null;
    }
}

class ServidorMultihiloEco {

    public static void main(String[] args) {

        int numPuerto = 4444;
        int idCliente = 1;
        Tablero miTablero = new Tablero();
        try ( ServerSocket socketServidor = new ServerSocket(numPuerto)) {
            System.out.printf("Creado socket de servidor en puerto %d. Esperando conexiones de clientes.\n", numPuerto);

            while (true) {    // Acepta una conexión de cliente tras otra
                Socket socketComNuevoCliente = socketServidor.accept();
                System.out.println("Cliente conectado => " + idCliente);

                Thread hiloSesion = new Thread(new HiloServidor(socketComNuevoCliente, miTablero));
                hiloSesion.start();
            }

        } catch (IOException ex) {
            System.out.println("Excepción de E/S");
            ex.printStackTrace();
            System.exit(1);
        }
    }

}
