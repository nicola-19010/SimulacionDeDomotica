package FINALVERSION;

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

                //se crea un nuevo hilo para manejar la comunicacion con el controlador
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

            //lee el comando enviado por el controlador
            String comando = entrada.readLine();
            System.out.println("Comando recibido: " + comando);

            //logica de la estufa para procesar el comando
            String respuesta = procesarComando(comando);

            //se envia la respuesta al controlador
            salida.println(respuesta);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String procesarComando(String comando) {
        if (comando.equals("ENCENDER")) {
            return "La estufa ha sido encendida";
        } else if (comando.equals("APAGAR")) {
            return "La estufa ha sido apagada";
        } else {
            return "Error: Comando no válido";
        }
    }
}
