package DEV.nuevos_nombres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Estufa {
    private String estado;
    private static final int PUERTO = 12447;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

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
    private Estufa estufa;

    public ManejadorControladorDesdeEstufa(Socket socket) {
        this.socket = socket;
        this.estufa = estufa;
    }

    @Override
    public void run() {
        try {
            Estufa estufa = new Estufa();
            estufa.setEstado("APAGADA");
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // Lee el comando/control enviado por el controlador
            String comando = entrada.readLine();
            System.out.println("Comando recibido: " + comando);

            // Lógica de la estufa para procesar el comando
            String respuesta = procesarComando(comando, estufa);

            // Envía la respuesta al controlador
            salida.println(respuesta);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String procesarComando(String comando, Estufa estufa) {
        if (comando.equals("ENCENDER")) {
            // Aquí implementa la lógica para encender la estufa
            estufa.setEstado("ENCENDIDA");
            return "La estufa ha sido encendida";
        } else if (comando.equals("APAGAR")) {
            // Aquí implementa la lógica para apagar la estufa
            estufa.setEstado("APAGADA");
            return "La estufa ha sido apagada";
        }else if (comando.equals("OBTENER_ESTADO")) {
            System.out.println("Estufa: " + estufa.getEstado());
            return "Estufa esta: " + estufa.getEstado();
        }
        else {
            // Si no se reconoce el comando, devuelve una respuesta de error
            return "Error: Comando no válido";
        }
    }

}
