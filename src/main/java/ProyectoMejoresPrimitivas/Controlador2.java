package ProyectoMejoresPrimitivas;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Controlador2 {

    private static final int PUERTO = 12345;
    private static final int PUERTO_SENSOR = 12399;
    public static final String HOST = "localhost";
    private static final int PUERTO_CLIENTE = 12400;

    public static void main(String[] args) {
        Controlador2 controlador = new Controlador2();
        //controlador.ejecutar();  //accion principal
        EjecutarTCP comunicacionTCP = new EjecutarTCP(PUERTO);
        ManejoRecepcionSensorMov comunicacionUDP = new ManejoRecepcionSensorMov(PUERTO_SENSOR, HOST, PUERTO_CLIENTE);
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
                    ManejadorCliente2 manejadorCliente = new ManejadorCliente2(socket);
                    manejadorCliente.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        class ManejadorCliente2 extends Thread {
            private Socket socket;

            public ManejadorCliente2(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

                    //Lee la solicitud del cliente
                    String solicitud = entrada.readLine();
                    System.out.println("Solicitud recibida: " + solicitud);

                    //Logica del controlador para procesar la solicitud y procesamiento
                    String respuesta = procesarSolicitud(solicitud);

                    // Envía la respuesta al cliente
                    salida.println(respuesta);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    cerrarSocket();
                }
            }

            private String procesarSolicitud(String solicitud) {
                return switch (solicitud) {
                    case "SOLICITAR_ESTADO" -> obtenerEstado();
                    case "ENCENDER_ESTUFA" -> enviarComando("ENCENDER", "localhost", 12347);
                    case "APAGAR_ESTUFA" -> enviarComando("APAGAR", "localhost", 12347);
                    case "SOLICITAR_TEMPERATURA" -> enviarComando("OBTENER_TEMPERATURA", "localhost", 12346);
                    default -> "Error: Solicitud no válida";
                };
            }

            private String obtenerEstado() {
                String estadoEstufa = enviarComando("OBTENER_ESTADO", "localhost", 12347);
                String estadoSensorTermico = enviarComando("OBTENER_ESTADO", "localhost", 12346);
                return "Estado actual del sistema:\n" + estadoEstufa + "\n" + estadoSensorTermico;
            }

            private String enviarComando(String comando, String host, int puerto) {
                try (Socket socket = new Socket(host, puerto);
                     PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    salida.println(comando);
                    return entrada.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error en la comunicación con el componente";
                }
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
            try {

                while (procesoActivo) {

                    //Obtengo la localizacion de localhost
                    InetAddress direccionServidor = InetAddress.getByName("localhost");
                    DatagramSocket socketUDP = new DatagramSocket();

                    String mensaje = "ENVIAR_ESTADO";
                    buffer = mensaje.trim().getBytes(StandardCharsets.UTF_8);

                    //Solicitud por localhost
                    DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SENSOR);
                    //Se envia por SOCKET
                    socketUDP.send(pregunta);

                    DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

                    socketUDP.receive(peticion);
                    byte[] data = new byte[peticion.getLength()];
                    System.arraycopy(peticion.getData(), peticion.getOffset(), data, 0, peticion.getLength());

                    mensaje = new String(peticion.getData());
                    evaluarMensaje(mensaje);

                    socketUDP.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void evaluarMensaje (String mensaje) {
            if (mensaje.equals("SI_MOVIMIENTO")){
                System.out.println("Alerta recibida: "+ mensaje);


                enviarComando(mensaje, HOST,PUERTO_CLIENTE);
            }
        }

        private String enviarComando(String comando, String host, int puerto) {
            try (Socket socket = new Socket(host, puerto);
                 PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                salida.println(comando);
                return entrada.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error en la comunicación con el componente";
            }

        }
        /*
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

                while (true) {
                    Socket socket = servidor.accept();
         */
}

