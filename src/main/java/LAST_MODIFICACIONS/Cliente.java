package LAST_MODIFICACIONS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        try {
            // Establecer conexión con el controlador
            Socket socket = new Socket("localhost", 1234);

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                // Enviar solicitud al controlador
                outputWriter.println(userInput);

                // Recibir temperatura del controlador
                String temperature = inputReader.readLine();
                System.out.println("Temperatura recibida: " + temperature);
            }

            // Cerrar conexiones
            outputWriter.close();
            inputReader.close();
            consoleReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
