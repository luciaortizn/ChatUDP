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
        getKeys();
        ds = new DatagramSocket();
        /*creo dos hilos
        uno gestiona el envío de mensajes y otro los recibe

        * */
        Thread recibirMensaje = new Thread(() -> {
            try {
                while (true) {
                    // creo un DatagramPacket para recibir datos
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    ds.receive(packet);
                    String receivedData = new String(packet.getData(), 0, packet.getLength());
                    //desencripto lo recibido
                    String decryptedData = desencriptar(receivedData);

                    if (decryptedData.contains("{")) {
                        System.out.print(decryptedData);
                    } else {
                        System.out.println("Sin leer:\n" + decryptedData);
                    }
                }
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        });

        Thread enviarMensaje = new Thread(() -> {
            try {
                while (true) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String msg = br.readLine();

                    // envio a servidor
                    sendMsg(msg);
                    if (msg.equals("salir")) {
                        System.out.println("Te has desconectado del servidor.");
                        //usuario se sale del programa
                        System.exit(0);
                    } else {
                        System.out.println("Enviado.");
                    }
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        enviarMensaje.start();
        recibirMensaje.start();
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // escribo la pk
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(publicKey);
        }
        byte[] publicKeyBytes = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(publicKeyBytes, publicKeyBytes.length, InetAddress.getLocalHost(), 1111);
        // envío el paquete
        ds.send(packet);
    }

    //uso base64 para desencriptar
    public static String desencriptar(String recibido) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cypher = Cipher.getInstance("RSA");
        cypher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] desencriptadoBytes = cypher.doFinal(Base64.getDecoder().decode(recibido));
        return new String(desencriptadoBytes);
    }
}
