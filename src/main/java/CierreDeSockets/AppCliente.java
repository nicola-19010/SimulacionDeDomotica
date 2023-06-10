package CierreDeSockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AppCliente {
    public static void main(String[] args) {
        try {
            // Establecer conexión con el controlador
            Socket socket = new Socket("localhost", 8080);

            // Configurar flujos de entrada y salida
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Interacción con el usuario para obtener la temperatura
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Presione Enter para solicitar la temperatura actual o ingrese 'exit' para salir.");
                String userInput = consoleInput.readLine();

                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }

                // Enviar solicitud al controlador
                output.println("GET_TEMPERATURE");

                // Recibir respuesta del controlador
                String response = input.readLine();
                System.out.println("La temperatura actual es: " + response);
            }

            // Cerrar sockets
            output.close();
            input.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
