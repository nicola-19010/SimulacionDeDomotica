package ProyectoMejoresPrimitivas;

import java.io.IOException;
import java.net.*;

public class SensorMovimiento {
    private static final int PUERTO = 12399;

    public static void main(String[] args) {

        byte[] buffer;
        buffer = new byte[16];
        try {
            System.out.println("Iniciado el sensor de movimiento");
            //Creacion del socket
            DatagramSocket socketUDP = new DatagramSocket(PUERTO);

            //Siempre atendera peticiones
            while (true) {

                //Preparo la respuesta
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

                //Recibo el datagrama
                socketUDP.receive(peticion);
                byte[] data = new byte[peticion.getLength()];
                System.arraycopy(peticion.getData(), peticion.getOffset(), data, 0, peticion.getLength());

                //Convierto lo recibido y mostrar el mensaje
                String mensaje = new String(data);
                System.out.println("Se recibio: " + mensaje);

                //Obtengo el puerto y la direccion de origen
                //Si no se quiere responder, no es necesario
                int puertoCliente = peticion.getPort();
                InetAddress direccion = peticion.getAddress();

                mensaje = procesarComando(detectarMovimiento());
                buffer = mensaje.trim().getBytes();

                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);

                socketUDP.send(respuesta);

                Thread.sleep(3000);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //de caracter aleatorio
    public static String procesarComando(int movimiento) {
        switch (movimiento) {
            case 0:
                String noExiste = "NO_MOVIMIENTO";
                return noExiste;
            case 1:
                String existe = "SI_MOVIMIENTO";
                return existe;
            default:
                return "ERROR_DESCONOCIDO";
        }
    }


    private static int detectarMovimiento () {
        int [] movimiento = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1};
        int eleccion = movimiento[(int)(Math.random()* movimiento.length)];
        return eleccion;
    }
}