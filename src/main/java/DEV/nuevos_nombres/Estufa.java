package DEV.nuevos_nombres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Estufa {
    private static final int PUERTO = 12347;

    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(PUERTO);

            while (true) {
                Socket socket = servidor.accept();

                // Crea un nuevo hilo para manejar la comunicación con el controlador
                Thread t = new Thread(new ManejadorControladorDesdeEstufa(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ManejadorControladorDesdeEstufa implements Runnable {
    private Socket socket;

    public ManejadorControladorDesdeEstufa(Socket socket) {
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

            // Lógica de la estufa para procesar el comando
            String respuesta = procesarComando(comando);

            // Envía la respuesta al controlador
            salida.println(respuesta);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String procesarComando(String comando) {
        if (comando.equals("ENCENDER")) {
            // Aquí implementa la lógica para encender la estufa
            return "La estufa ha sido encendida";
        } else if (comando.equals("APAGAR")) {
            // Aquí implementa la lógica para apagar la estufa
            return "La estufa ha sido apagada";
        } else {
            // Si no se reconoce el comando, devuelve una respuesta de error
            return "Error: Comando no válido";
        }
    }
}
