package servidor_chat_udp;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class ClientUDP {
    private static DatagramSocket ds;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ds = new DatagramSocket();
        getKeys();
        /*creo dos hilos
        uno gestiona el envío de mensajes y otro los recibe
        * */
        Thread enviarMensaje = new Thread(() -> {
            try {
                while (true) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String msg = br.readLine();
                    if (!msg.equals("salir")) {

                        sendMsg(msg);
                        System.out.println("Enviado.");

                    } else {

                        sendMsg(msg);
                        System.out.println("Te has desconectado del servidor.");
                        //cerramos
                        System.exit(0);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Thread recibirMensaje = new Thread(() -> {
            try {
                while (true) {
                    byte[] recibidoBytes = new byte[1024];
                    DatagramPacket dpRecibido = new DatagramPacket(recibidoBytes, recibidoBytes.length);
                    //obtengo el mensaje del datagram packet y lo convierto a formato string
                    ds.receive(dpRecibido);
                    String dpString = new String(dpRecibido.getData(), 0, dpRecibido.getLength());
                    String desencriptado = desencriptar(dpString);
                    if (desencriptado.contains("{")) {
                        System.out.print(desencriptado);
                    } else {
                        System.out.println("Sin leer: \n" + desencriptado);
                    }
                }
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        });

        recibirMensaje.start();
        enviarMensaje.start();
    }


    //se envían los bytes de mi string msg pasado por parámetro
    public static void sendMsg(String msg) throws IOException {

        byte[] bytes = msg.getBytes();
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 1111);

        ds.send(dp);
    }

    public static void getKeys() throws NoSuchAlgorithmException, IOException {

        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        generador.initialize(2048);
        //necesito un generador de claves
        KeyPair kp = generador.generateKeyPair();

        privateKey = kp.getPrivate();
        publicKey = kp.getPublic();
        sendKey();
    }

    //usa un paquete que contiene bytes de la clave pública
    public static void sendKey() throws IOException {

        //para contener los bytes y mandarlos
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oas = new ObjectOutputStream(baos);
        oas.writeObject(publicKey);
        byte[] publicBytes = baos.toByteArray();
        DatagramPacket publicData = new DatagramPacket(publicBytes, publicBytes.length, InetAddress.getLocalHost(), 1111);
        //envío los bytes
        ds.send(publicData);
    }
    //uso base64 para desencriptar
    public static String desencriptar(String recibido) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cypher = Cipher.getInstance("RSA");
        cypher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] desencriptadoBytes = cypher.doFinal(Base64.getDecoder().decode(recibido));
        return new String(desencriptadoBytes);
    }
}
