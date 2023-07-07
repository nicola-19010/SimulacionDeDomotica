package DEV.nuevos_nombres;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class Appjava {
    private static final String HOST = "localhost";
    private static final int PUERTO_CONTROLADOR = 23456;
    private static final int PUERTO_ESCUCHANDO = 12401;

    public static void main(String[] args) {

        EjecutarMenu hiloEjecutarMenu = new EjecutarMenu(HOST, PUERTO_CONTROLADOR);
        IniciarAlertaTCP hiloRecepcion = new IniciarAlertaTCP(PUERTO_ESCUCHANDO);
        hiloEjecutarMenu.start();
        hiloRecepcion.start();
    }
}

    class EjecutarMenu extends Thread {
        private static String HOST;
        private static int PUERTO;
        public EjecutarMenu (String HOST, int PUERTO) {
            this.HOST = HOST;
            this.PUERTO = PUERTO;
        }
        @Override
        public void run() {
            Scanner teclado = new Scanner(System.in);
            boolean ejecucionActiva = true;

            while (ejecucionActiva) {
                mostrarMenu();
                String opcion = pedirOpcion(teclado);

                switch (opcion) {
                    case "1" -> realizarSolicitud("SOLICITAR_ESTADO_ESTUFA");
                    case "2" -> realizarSolicitud("SOLICITAR_ESTADO_SENSOR_TEMP");
                    case "3" -> realizarSolicitud("ENCENDER_ESTUFA");
                    case "4" -> realizarSolicitud("APAGAR_ESTUFA");
                    case "5" -> realizarSolicitud("SOLICITAR_TEMPERATURA");
                    case "0" -> {
                        ejecucionActiva = false;

                        System.out.println("Saliendo...");
                    }
                    default -> System.out.println("Opción inválida. Por favor, ingrese una opción válida.");
                }
            }
        }


        private void realizarSolicitud(String solicitud) {

            try (Socket socket = conectarSocket();
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                enviarSolicitud(output, solicitud);
              //  recibirRespuesta(entrada); //debe imprimir
                entrada.readLine();
                System.out.println(entrada.readLine());

            }catch (BindException e) {

            } catch (IOException e) {
                imprimirError("Error de E/S: controlador inalcanzable, " + e.getMessage());
            }
        }

        private String pedirOpcion(Scanner teclado) {
            return teclado.nextLine();
        }

        private void mostrarMenu() {
            System.out.println("------- MENÚ -------");
            System.out.println("1. Solicitar estado estufa");
            System.out.println("2. Solicitar estado sensor de temperatura");
            System.out.println("3. Encender estufa");
            System.out.println("4. Apagar estufa");
            System.out.println("5. Solicitar temperatura");
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
                    System.out.println("Respuesta del controlador: " + respuesta);
                }if (respuesta ==null) {
                    System.out.println("vacio");
                }
            }
        }

        private void imprimirError(String mensaje) {
            System.err.println(mensaje);
        }
    }
class IniciarAlertaTCP extends Thread {
    private int PUERTO;
    public IniciarAlertaTCP (int PUERTO) {
        this.PUERTO = PUERTO;
    }

    @Override
    public void run() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            while (true) {
                Socket socket = servidor.accept();

                // Crea un nuevo hilo para manejar la comunicación con el cliente
                ManejadorAlertaMovimiento manejadorAlerta = new ManejadorAlertaMovimiento(socket);
                manejadorAlerta.start();
            }
        } catch (BindException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ManejadorAlertaMovimiento extends Thread {
        private Socket socket;

        public ManejadorAlertaMovimiento(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

                //Lee la solicitud del cliente
                String solicitud = entrada.readLine();

                //Logica del controlador para procesar la solicitud y procesamiento
                String respuesta = ("CLIENTE: "+ solicitud);
                salida.println(respuesta);
                procesarSolicitud(solicitud);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cerrarSocket();
            }
        }
        private void procesarSolicitud (String mensaje) {
            String [] cadenas = mensaje.replaceAll(" ", "").split(",");
            if (cadenas[0].toUpperCase(Locale.ROOT).equals("SI_MOVIMIENTO")) {
                System.out.println("ALERTA: Movimiento detectado a: "+cadenas[1]+ "hrs.");
            }
            // primer espacio antes de coma = comado --- segundo espacio --- hora
        }


        private void cerrarSocket() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



