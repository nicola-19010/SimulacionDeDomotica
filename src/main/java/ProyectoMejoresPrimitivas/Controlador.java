package ProyectoMejoresPrimitivas;

import java.io.*;
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

                // Crea un nuevo hilo para manejar la comunicación con el cliente
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

            // Lee la solicitud del cliente
            String solicitud = entrada.readLine();
            System.out.println("Solicitud recibida: " + solicitud);

            // Lógica del controlador para procesar la solicitud
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
        switch (solicitud) {
            case "SOLICITAR_ESTADO":
                return obtenerEstado();
            case "ENCENDER_ESTUFA":
                return enviarComandoEstufa("ENCENDER");
            case "APAGAR_ESTUFA":
                return enviarComandoEstufa("APAGAR");
            case "SOLICITAR_TEMPERATURA":
                return enviarComandoSensorTermico("OBTENER_TEMPERATURA");
            default:
                return "Error: Solicitud no válida";
        }
    }

    private String obtenerEstado() {
        String estadoEstufa = enviarComandoEstufa("OBTENER_ESTADO");
        String estadoSensorTermico = enviarComandoSensorTermico("OBTENER_ESTADO");
        return "Estado actual del sistema:\n" + estadoEstufa + "\n" + estadoSensorTermico;
    }

    private String enviarComandoEstufa(String comando) {
        try (Socket estufaSocket = new Socket("localhost", 12347);
             PrintWriter estufaSalida = new PrintWriter(estufaSocket.getOutputStream(), true);
             BufferedReader estufaEntrada = new BufferedReader(new InputStreamReader(estufaSocket.getInputStream()))) {

            estufaSalida.println(comando);
            return estufaEntrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error en la comunicación con la estufa";
        }
    }

    private String enviarComandoSensorTermico(String comando) {
        try (Socket sensorSocket = new Socket("localhost", 12346);
             PrintWriter sensorSalida = new PrintWriter(sensorSocket.getOutputStream(), true);
             BufferedReader sensorEntrada = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()))) {

            sensorSalida.println(comando);
            return sensorEntrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error en la comunicación con el sensor térmico";
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
