package LAST_MODIFICACIONS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Controlador {
    public static void main(String[] args) {
        try {
            // Establecer servidor para aceptar conexiones de clientes
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Controlador esperando conexiones de clientes...");
            
            while (true) {
                // Aceptar conexión de un cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());
                
                // Crear hilo para manejar la comunicación con el cliente
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    public void run() {
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String clientRequest;
            while ((clientRequest = inputReader.readLine()) != null) {
                // Enviar solicitud al sensor de temperatura
                Socket sensorSocket = new Socket("localhost", 5678);
                PrintWriter sensorOutput = new PrintWriter(sensorSocket.getOutputStream(), true);
                BufferedReader sensorInput = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()));
                
                sensorOutput.println(clientRequest);
                
                // Recibir respuesta del sensor
                String temperature = sensorInput.readLine();
                
                // Enviar respuesta al cliente
                outputWriter.println(temperature);
                
                // Cerrar conexiones con el sensor
                sensorOutput.close();
                sensorInput.close();
                sensorSocket.close();
            }
            
            // Cerrar conexiones con el cliente
            outputWriter.close();
            inputReader.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
