package CierreDeSockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SensorTemperatura {
    public static void main(String[] args) {
        try {
            // Crear socket del sensor de temperatura y esperar conexiones
            ServerSocket serverSocket = new ServerSocket(9090);
            System.out.println("Sensor de temperatura esperando conexiones...");

            while (true) {
                // Aceptar conexión del controlador
                Socket controladorSocket = serverSocket.accept();
                System.out.println("Conexión establecida con el controlador.");

                // Configurar flujos de entrada y salida
                BufferedReader input = new BufferedReader(new InputStreamReader(controladorSocket.getInputStream()));
                PrintWriter output = new PrintWriter(controladorSocket.getOutputStream(), true);

                // Esperar solicitud del controlador
                String request = input.readLine();

                if (request.equals("GET_TEMPERATURE")) {
                    // Simular obtención de la temperatura actual
                    String temperature = obtenerTemperatura();

                    // Enviar respuesta al controlador
                    output.println(temperature);
                }

                // Cerrar sockets del controlador y el sensor de temperatura
                output.close();
                input.close();
                controladorSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String obtenerTemperatura() {
        // Simulación de obtención de la temperatura actual
        return "25°C";
    }
}
