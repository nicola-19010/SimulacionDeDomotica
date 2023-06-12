package ProyectoMejoresPrimitivas;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente3 {
    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        Cliente3 cliente = new Cliente3();
        cliente.ejecutar();
    }

    private void ejecutar() {
        Scanner teclado = new Scanner(System.in);

        boolean ejecucionActiva = true;

        while (ejecucionActiva) {
            mostrarMenu();
            String opcion = pedirOpcion(teclado);

            switch (opcion) {
                case "1" -> realizarSolicitud("SOLICITAR_ESTADO");
                case "2" -> realizarSolicitud("ENCENDER_ESTUFA");
                case "3" -> realizarSolicitud("APAGAR_ESTUFA");
                case "4" -> realizarSolicitud("SOLICITAR_TEMPERATURA");
                case "0" -> {
                    ejecucionActiva = false;
                    System.out.println("Saliendo...");
                }
                default -> System.out.println("Opción inválida. Por favor, ingrese una opción válida.");
            }
        }
    }

    private void realizarSolicitud(String solicitud) {
        // En try-with-resources los recursos (Socket, Scanner y PrintWriter) se declaran e inicializan dentro del bloque
        //try. Separados por punto y coma (;). el cierre del Socket, Scanner y PrintWriter se realiza automáticamente al
        // finalizar el bloque try. No es necesario utilizar bloques finally ni llamar manualmente a los métodos close()
        // para cada recurso.
        try (Socket socket = conectarSocket();
             Scanner input = new Scanner(socket.getInputStream());
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            enviarSolicitud(output, solicitud);
            recibirRespuesta(input);

        } catch (IOException e) {
            imprimirError("Error de E/S: " + e.getMessage());
        }
    }

    private String pedirOpcion(Scanner teclado) {
        return teclado.nextLine();
    }

    private void mostrarMenu() {
        System.out.println("------- MENÚ -------");
        System.out.println("1. Solicitar estado");
        System.out.println("2. Encender estufa");
        System.out.println("3. Apagar estufa");
        System.out.println("4. Solicitar temperatura");
        System.out.println("0. Salir");
        System.out.println("--------------------");
        System.out.print("Ingrese una opción: ");
    }

    private Socket conectarSocket() throws IOException {
        Socket socket = new Socket(HOST, PUERTO);
        System.out.println("Conectado al servidor");
        return socket;
    }

    private void enviarSolicitud(PrintWriter salida, String solicitud) {
        salida.println(solicitud);
        System.out.println("Solicitud enviada: " + solicitud);
    }

    private void recibirRespuesta(Scanner input) {
        while (input.hasNextLine()) {
            String respuesta = input.nextLine();
            if (respuesta != null) {
                System.out.println("Respuesta del servidor: " + respuesta);
            }
        }
    }

    private void imprimirError(String mensaje) {
        System.err.println(mensaje);
    }
}
