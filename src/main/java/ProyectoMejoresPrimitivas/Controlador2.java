package ProyectoMejoresPrimitivas;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Controlador2 {
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        Controlador2 controlador = new Controlador2();
        controlador.ejecutar();
    }

    private void ejecutar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            while (true) {
                Socket socket = servidor.accept();

                // Crea un nuevo hilo para manejar la comunicación con el cliente
                ManejadorCliente2 manejadorCliente = new ManejadorCliente2(socket);
                manejadorCliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ManejadorCliente2 extends Thread {
    private Socket socket;

    public ManejadorCliente2(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

            //Lee la solicitud del cliente
            String solicitud = entrada.readLine();
            System.out.println("Solicitud recibida: " + solicitud);

            //Logica del controlador para procesar la solicitud y procesamiento
            String respuesta = procesarSolicitud(solicitud);

            // Envía la respuesta al cliente
            salida.println(respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarSocket();
        }
    }

    private String procesarSolicitud(String solicitud) {
        return switch (solicitud) {
            case "SOLICITAR_ESTADO" -> obtenerEstado();
            case "ENCENDER_ESTUFA" -> enviarComando("ENCENDER", "localhost", 12347);
            case "APAGAR_ESTUFA" -> enviarComando("APAGAR", "localhost", 12347);
            case "SOLICITAR_TEMPERATURA" -> enviarComando("OBTENER_TEMPERATURA", "localhost", 12346);
            default -> "Error: Solicitud no válida";
        };
    }

    private String obtenerEstado() {
        String estadoEstufa = enviarComando("OBTENER_ESTADO", "localhost", 12347);
        String estadoSensorTermico = enviarComando("OBTENER_ESTADO", "localhost", 12346);
        return "Estado actual del sistema:\n" + estadoEstufa + "\n" + estadoSensorTermico;
    }

    private String enviarComando(String comando, String host, int puerto) {
        try (Socket socket = new Socket(host, puerto);
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            salida.println(comando);
            return entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error en la comunicación con el componente";
        }
    }

    private void cerrarSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
