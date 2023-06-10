package ProyectoAMano;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Controlador {
    public static void main(String[] args) {
        try {
            // Crear socket del controlador y esperar conexiones
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Controlador esperando conexiones...");

            while (true) {
                // Aceptar conexión de la aplicación cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexión establecida con la aplicación cliente.");

                // Configurar flujos de entrada y salida
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                // Esperar y procesar solicitudes de la aplicación cliente
                String request;
                while ((request = input.readLine()) != null) {
                    if (request.equals("GET_TEMPERATURE")) {
                        // Enviar solicitud al sensor de temperatura
                        Socket sensorSocket = new Socket("localhost", 9090);
                        BufferedReader sensorInput = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()));
                        PrintWriter sensorOutput = new PrintWriter(sensorSocket.getOutputStream(), true);

                        sensorOutput.println("GET_TEMPERATURE");

                        // Recibir respuesta del sensor
                        String response = sensorInput.readLine();

                        // Enviar respuesta a la aplicación cliente
                        output.println(response);

                        // Cerrar sockets del sensor
                        sensorOutput.close();
                        sensorInput.close();
                        sensorSocket.close();
                    }
                }

                // Cerrar sockets de la aplicación cliente y el controlador
                output.close();
                input.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
