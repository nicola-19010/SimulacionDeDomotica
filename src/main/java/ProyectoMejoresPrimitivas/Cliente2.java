package ProyectoMejoresPrimitivas;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente2 {
    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    private boolean ejecucionActiva;

    public static void main(String[] args) {
        Cliente2 cliente = new Cliente2();
        cliente.ejecutar();
    }

    private void ejecutar() {
        Scanner teclado = new Scanner(System.in);

        try {
            ejecucionActiva = true;

            while (ejecucionActiva) {
                mostrarMenu();
                String opcion = pedirOpcion(teclado);

                switch (opcion) {
                    case "1":
                        realizarSolicitud("SOLICITAR_ESTADO");
                        break;
                    case "2":
                        realizarSolicitud("ENCENDER_ESTUFA");
                        break;
                    case "3":
                        realizarSolicitud("APAGAR_ESTUFA");
                        break;
                    case "4":
                        realizarSolicitud("SOLICITAR_TEMPERATURA");
                        break;
                    case "0":
                        ejecucionActiva = false;
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción inválida. Por favor, ingrese una opción válida.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void realizarSolicitud(String solicitud) throws IOException {
        Socket socket = conectarSocket();
        Scanner input = new Scanner(socket.getInputStream());
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

        try {
            enviarSolicitud(output, solicitud);
            recibirRespuesta(input);
        } finally {
            input.close();
            output.close();
            socket.close();
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
}
