package LAST_MODIFICACIONS;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SensorTemperatura {
    public static void main(String[] args) {
        try {
            // Establecer servidor para aceptar conexiones del controlador
            ServerSocket serverSocket = new ServerSocket(5678);
            System.out.println("Sensor de temperatura esperando conexiones del controlador...");
            
            while (true) {
                // Aceptar conexión del controlador
                Socket controllerSocket = serverSocket.accept();
                System.out.println("Controlador conectado: " + controllerSocket.getInetAddress().getHostAddress());
                
                // Generar temperatura aleatoria
                Random random = new Random();
                int temperature = random.nextInt(100);
                
                // Enviar temperatura al controlador
                PrintWriter outputWriter = new PrintWriter(controllerSocket.getOutputStream(), true);
                outputWriter.println(temperature);
                
                // Cerrar conexión con el controlador
                outputWriter.close();
                controllerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
