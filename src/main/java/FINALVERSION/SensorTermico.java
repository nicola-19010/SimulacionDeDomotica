package FINALVERSION;

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

                //se crea un nuevo hilo para manejar la comunicación con el controlador
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

            //lee el comando enviado por el controlador
            String comando = entrada.readLine();
            System.out.println("Comando recibido: " + comando);

            //logica del sensor térmico para procesar el comando
            String respuesta = procesarComando(comando);

            //envia la respuesta al controlador
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
            return "Error: Comando no válido";
        }
    }

    private double obtenerTemperatura() {
        //genera una temperatura entre 20 y 30
        Random random = new Random();
        return 20.0 + random.nextDouble() * 10.0;
    }
}
