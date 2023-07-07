package DEV.nuevos_nombres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;

public class SensorTermico {
    private static final int PUERTO = 23457;


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
        } else if (comando.equals("OBTENER_ESTADO_SENSOR_TEMP")) {
            return "CONECTADO";
        } else {
            // Si no se reconoce el comando, devuelve una respuesta de error
            return "Error: Comando no válido";
        }
    }

    private int obtenerTemperatura() {
        LocalTime tiempo = LocalTime.now().withNano(0);
        String[] formato = tiempo.toString().split(":");
        int hora = Integer.parseInt(formato[1]);
        if (hora > 3 && hora <= 6) {
            return 3;
        }
        if (hora > 6 && hora <= 9) {
            return 4;
        }
        if (hora > 10 && hora <= 11) {
            return 5;
        }
        if (hora > 12 && hora <= 14) {
            return 7;
        }
        if (hora > 15 && hora <= 17) {
            return 8;
        }
        if (hora > 18 && hora <= 20) {
            return 9;
        }
        if (hora > 21 && hora <= 23) {
            return 8;
        }
        if (hora > 00 && hora <= 3) {
            return 7;
        }
        return 0;
    }
}
