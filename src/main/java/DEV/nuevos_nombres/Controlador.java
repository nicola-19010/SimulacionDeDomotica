package DEV.nuevos_nombres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Scanner;

public class Controlador {

    public static final String HOST = "localhost";
    private static final int PUERTO_APP = 23456;
    private static final int PUERTO_APP_ESCUCHANDO = 12401;
    private static final int PUERTO_SENSOR_MOV = 12399;




    public static void main(String[] args) {

        EjecutarTCP comunicacionTCP = new EjecutarTCP(PUERTO_APP);
        ManejoRecepcionSensorMov comunicacionUDP = new ManejoRecepcionSensorMov(PUERTO_SENSOR_MOV, HOST, PUERTO_APP_ESCUCHANDO);
        comunicacionTCP.start();
        comunicacionUDP.start();
    }
}

    class EjecutarTCP extends Thread {
        private int PUERTO;
        public EjecutarTCP (int PUERTO) {
            this.PUERTO = PUERTO;
        }

        @Override
        public void run() {
            try (ServerSocket servidor = new ServerSocket(PUERTO)) {

                while (true) {
                    Socket socket = servidor.accept();

                    // Crea un nuevo hilo para manejar la comunicación con el cliente
                    ManejadorCliente manejadorCliente = new ManejadorCliente(socket);
                    manejadorCliente.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        class ManejadorCliente extends Thread {
            private Socket socket;

            public ManejadorCliente(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run()  {
                try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter salida = new PrintWriter(socket.getOutputStream(), false)) {

                    //Lee la solicitud del cliente
                    String solicitud = entrada.readLine();
                    System.out.println("Solicitud recibida: " + solicitud);

                    //Logica del controlador para procesar la solicitud y procesamiento
                    String respuesta = procesarSolicitud(solicitud);
                    System.out.println(respuesta); //para saber
                    // Envía la respuesta al cliente
                    salida.println(respuesta);

                }catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    cerrarSocket();
                }
            }

            private String procesarSolicitud(String solicitud) {
                return switch (solicitud) {
                    case "SOLICITAR_ESTADO_ESTUFA" -> enviarComando("OBTENER_ESTADO", "localhost", 12447);
                    case "SOLICITAR_ESTADO_SENSOR_TEMP" -> enviarComando("OBTENER_ESTADO", "localhost", 23457);
                    case "ENCENDER_ESTUFA" -> enviarComando("ENCENDER", "localhost", 12447);
                    case "APAGAR_ESTUFA" -> enviarComando("APAGAR", "localhost", 12447);
                    case "SOLICITAR_TEMPERATURA" -> enviarComando("OBTENER_TEMPERATURA", "localhost", 23457);
                    default -> "Error: Solicitud no válida";
                };
            }

            private String enviarComando(String comando, String host, int puerto) {
                try (Socket socket = new Socket(host, puerto);
                     PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    salida.println(comando);
                    String deVuelta = entrada.readLine().toString();
                    return deVuelta;
                } catch (IOException e) {
                    return imprimirError("Error: componente no conectado: "+ identificarComponente(puerto) +", "+ e.getMessage());
                }finally {
                    cerrarSocket();
                }
            }

            private String identificarComponente (int puerto) {
                if (puerto == 12447) return "ESTUFA";
                if (puerto == 23457) return "SENSOR_TERMICO";
                return "";
            }

            private void cerrarSocket() {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            private String imprimirError(String mensaje) {
                System.err.println(mensaje);
                return mensaje;
            }
        }
    }
    class ManejoRecepcionSensorMov extends Thread {
        private int PUERTO_SENSOR;
        private String HOST;
        private int PUERTO_CLIENTE;

        public ManejoRecepcionSensorMov (int PUERTO_SENSOR, String HOST, int PUERTO_CLIENTE) {
            this.PUERTO_SENSOR = PUERTO_SENSOR;
            this.HOST = HOST;
            this.PUERTO_CLIENTE = PUERTO_CLIENTE;
        }
        @Override
        public void run() {
            byte[] buffer;
            boolean procesoActivo = true;
            while (true) {
                try {
                    InetAddress direccionServidor = InetAddress.getByName("localhost");
                    DatagramSocket socketUDP = new DatagramSocket();

                    while (procesoActivo) {

                        //Obtengo la localizacion de localhost
                        String mensaje = "ENVIAR_ESTADO";
                        buffer = mensaje.trim().getBytes(StandardCharsets.UTF_8);


                        DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SENSOR);
                        socketUDP.send(pregunta);

                        DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

                        socketUDP.receive(peticion);
                        byte[] data = new byte[peticion.getLength()];
                        System.arraycopy(peticion.getData(), peticion.getOffset(), data, 0, peticion.getLength());

                        mensaje = new String(peticion.getData());
                        evaluarMensaje(mensaje);


                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void evaluarMensaje (String mensaje) {
            LocalTime time = LocalTime.now().withNano(0);
            System.out.println(mensaje);
            if (mensaje.contains("SI_MOVIMIENTO")){
                String alerta = mensaje + ", "+ time.toString();
                System.out.println(alerta);
                EjecutarAlertaACliente enviarAlertaACliente = new EjecutarAlertaACliente(HOST, PUERTO_CLIENTE, (alerta));
                enviarAlertaACliente.start();
            }
        }
}
        class EjecutarAlertaACliente extends Thread {
            private static String HOST;
            private static int PUERTO;
            private String mensajeAlerta;
            public EjecutarAlertaACliente (String HOST, int PUERTO, String mensajeAlerta) {
                this.HOST = HOST;
                this.PUERTO = PUERTO;
                this.mensajeAlerta = mensajeAlerta;
            }
            @Override
            public void run() {
                    String opcion = mensajeAlerta;
                    realizarSolicitud(opcion);
                }

            private void realizarSolicitud(String solicitud) {

                try (Socket socket = conectarSocket();
                     Scanner input = new Scanner(socket.getInputStream());
                     PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                    enviarSolicitud(output, solicitud);
                    recibirRespuesta(input);

                } catch (IOException e) {
                    imprimirError("Revise su aplicacion. Error de E/S: " + e.getMessage());
                }
            }
        private Socket conectarSocket() throws IOException {
            Socket socket = new Socket(HOST, PUERTO);
            //System.out.println("Conectado al dispositivo CONTROL");
            return socket;
        }

        private void enviarSolicitud(PrintWriter salida, String solicitud) {
            salida.println(solicitud);
            //para revisar lo que envia
            //System.out.println("Solicitud enviada: " + solicitud);
        }

        private void recibirRespuesta(Scanner input) {
            while (input.hasNextLine()) {
                String respuesta = input.nextLine();
                if (respuesta != null) {
                    System.out.println(respuesta);
                }
            }
        }

        private void imprimirError(String mensaje) {
            System.err.println(mensaje);
        }
    }


