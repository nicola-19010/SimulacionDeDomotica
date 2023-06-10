package ProyectoMejoresPrimitivas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SensorTermico {
    private static final int PUERTO = 12346;

    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(PUERTO);

            while (true) {
                Socket socket = servidor.accept();

                // Crea un nuevo hilo para manejar la comunicación con el controlador
                Thread t = new Thread(new ManejadorControladorDesdeSensorTermico(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ManejadorControladorDesdeSensorTermico implements Runnable {
    private Socket socket;

    public ManejadorControladorDesdeSensorTermico(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // Lee el comando/control enviado por el controlador
            String comando = entrada.readLine();
            System.out.println("Comando recibido: " + comando);

            // Lógica del sensor térmico para procesar el comando
            String respuesta = procesarComando(comando);

            // Envía la respuesta al controlador
            salida.println(respuesta);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String procesarComando(String comando) {
        if (comando.equals("OBTENER_TEMPERATURA")) {
            double temperatura = obtenerTemperatura();
            return "Temperatura actual: " + temperatura + " °C";
        } else {
            // Si no se reconoce el comando, devuelve una respuesta de error
            return "Error: Comando no válido";
        }
    }

    private double obtenerTemperatura() {
        // Aquí implementa la lógica para obtener la temperatura actual del sensor térmico
        // Por ejemplo, puedes leer datos de temperatura de un sensor o generar valores simulados
        Random random = new Random();
        return 20.0 + random.nextDouble() * 10.0; // Genera una temperatura simulada entre 20.0 y 30.0
    }
}
