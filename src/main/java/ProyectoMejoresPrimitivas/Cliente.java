package ProyectoMejoresPrimitivas;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    private boolean ejecucionActiva;

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.ejecutar();
    }

    private void ejecutar() {
        Socket socket = null;
        Scanner input = null;
        PrintWriter output = null;
        Scanner teclado = new Scanner(System.in);

        try {
            socket = conectarSocket();
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);

            ejecucionActiva = true;

            while (ejecucionActiva) {
                mostrarMenu();
                String opcion = pedirOpcion(teclado);

                switch (opcion) {
                    case "1":
                        enviarSolicitud(output, "SOLICITAR_ESTADO");
                        recibirRespuesta(input);
                        break;
                    case "2":
                        enviarSolicitud(output, "ENCENDER_ESTUFA");
                        recibirRespuesta(input);
                        break;
                    case "3":
                        enviarSolicitud(output, "APAGAR_ESTUFA");
                        recibirRespuesta(input);
                        break;
                    case "4":
                        enviarSolicitud(output, "SOLICITAR_TEMPERATURA");
                        recibirRespuesta(input);
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
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
