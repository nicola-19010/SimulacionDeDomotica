package FINALVERSION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Controlador {
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        controlador.ejecutar();
    }

    private void ejecutar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            while (true) {
                Socket socket = servidor.accept();

                //se crea un nuevo hilo para manejar la comunicación con el cliente
                ManejadorCliente manejadorCliente = new ManejadorCliente(socket);
                manejadorCliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ManejadorCliente extends Thread {
    private Socket socket;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

            //se lee la solicitud del cliente
            String solicitud = entrada.readLine();
            System.out.println("Solicitud recibida: " + solicitud);

            //logica del controlador para procesar la solicitud
            String respuesta = procesarSolicitud(solicitud);

            //se envia la respuesta al cliente
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
            case "ENCENDER_ESTUFA" -> enviarComando("ENCENDER", "estufa", 12347);
            case "APAGAR_ESTUFA" -> enviarComando("APAGAR", "estufa", 12347);
            case "SOLICITAR_TEMPERATURA" -> enviarComando("OBTENER_TEMPERATURA", "sensor", 12346);
            default -> "Error: Solicitud no válida";
        };
    }

    private String obtenerEstado() {
        String estadoEstufa = enviarComando("OBTENER_ESTADO", "estufa", 12347);
        String estadoSensorTermico = enviarComando("OBTENER_ESTADO", "sensor", 12346);
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
