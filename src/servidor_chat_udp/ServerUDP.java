package servidor_chat_udp;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class ServerUDP {
    //para los usuarios
    public static Map<String, SocketAddress> connected;
    private static final ArrayList<String> randomUsers = new ArrayList<>();
    private static int cont = 0;
    //sockets distintos para cada acción
    private static DatagramSocket msgServer;
    private static DatagramSocket filesServer;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        connected = new HashMap<>();
       Map<SocketAddress, PublicKey> kp = new HashMap<>();
        ReentrantLock lock = new ReentrantLock();
        randomUsers.add("sofía");
        randomUsers.add("jorge");
        randomUsers.add("david");
        randomUsers.add("michel");
        randomUsers.add("sara");
        Thread hiloMsgs = new Thread(() -> {

            try {
                msgServer = new DatagramSocket(1111);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                String nombre;
                //creamos un paquete para el mensaje que va a llegar
                byte[] mensaje = new byte[1024];
                DatagramPacket dp = new DatagramPacket(mensaje, mensaje.length);
                //recibimos el paquete

                try {

                    msgServer.receive(dp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //lo bloqueo para acceder al mensaje de forma segura
                lock.lock();
                String paqueteString = new String(dp.getData(), 0, dp.getLength());
                //ignore case por si acaso
                if (new String(dp.getData(), 0, dp.getLength()).equalsIgnoreCase("salir")) {
                    exitUser(kp,dp.getSocketAddress());
                }else{
                    SocketAddress userAddress = dp.getSocketAddress();
                    if (clienteExists(userAddress)) {
                        nombre = getName(userAddress);
                        connected.forEach((cliente, socket) -> {

                            //recorro lista para enviar mensaje a todos
                            String encriptado = encriptar(kp,paqueteString, socket);
                            try {
                                if (!nombre.equals(cliente)) {
                                    //no necesito el nombre de cada cliente
                                    sendMsgs(encriptado, socket);
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        //obtengo el array de bytes del paquete y los convierto en objeto -> public key
                        try {
                            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dp.getData()));
                            PublicKey pk = (PublicKey) ois.readObject();
                            //añado user
                            kp.put(userAddress, pk);
                            //se le asigna usuario
                            nombre = defaultName();
                            connected.put(nombre, userAddress);
                            //muestro en servidor
                            System.out.println( nombre +" dice: " + paqueteString);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    lock.unlock();
                }
            }
        });
       hiloMsgs.start();
       //ahora creo un hilo paralelo para gestionar los archivos
        Thread hiloArchivos = new Thread(() -> {
            try {
                filesServer= new DatagramSocket(1000);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                //igual que antes
                String nombre;
                //creamos un paquete para el mensaje que va a llegar
                byte[] mensaje = new byte[1024];
                DatagramPacket dp = new DatagramPacket(mensaje, mensaje.length);
                try {
                    filesServer.receive(dp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                lock.lock();

                try {
                    //manejo de bytes para los archivos
                    SocketAddress userAddress = dp.getSocketAddress();
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dp.getData()));
                    File recibido = (File) ois.readObject();
                    nombre = getName(userAddress);
                    connected.forEach((cliente, socket) -> {
                        //reenviamos los mensajes (archivos) a todos los de la lista
                        if (!nombre.equals(cliente)) {
                            try {
                                sendFiles(kp, recibido, socket);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                lock.unlock();
            }
        });
        hiloArchivos.start();
    }

    //obtiene el nombre de usuario de la persona conectada
    private static String getName(SocketAddress userAddress) {
        //necesito que sea atómico por la lambda
        AtomicReference<String> nombre = new AtomicReference<>("");
        connected.forEach((key, value) -> {
            if (userAddress.equals(value)) {
                nombre.set(key);
            }
        });
        return nombre.get();
    }

    //elige un nombre aleatorio de usuario si hay disponibles si no da uno por defecto
    public static String defaultName() {
        if (randomUsers.isEmpty()) {
            return "user";
        }
        return randomUsers.remove(cont++);
    }

    public static boolean clienteExists(SocketAddress userAddress) {
        return connected.containsValue(userAddress);
    }

    //crea el paquete y lo manda
    public static void sendMsgs(String msg, SocketAddress userAddress) throws IOException {
        DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.getBytes().length, userAddress);
        msgServer.send(dp);
    }
    //obtiene el usuario desconectado y su mensaje de desconexión
    public static void exitUser(Map<SocketAddress, PublicKey> kp,SocketAddress userAddress) {

        String msg = getName(userAddress) + " se ha desconectado.";

        connected.forEach((key, value) -> {
            //mando al todos los usuarios que se ha desconectado x user
            String encriptado = encriptar( kp,msg, value);
            try {
                sendMsgs(encriptado, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        connected.remove(getName(userAddress));
    }

    public static void sendFiles(Map<SocketAddress, PublicKey> kp, File recibido, SocketAddress userAddress) throws IOException {
        // Crear un flujo de entrada de bytes desde el archivo
        FileInputStream stream = new FileInputStream(recibido);

        // Crear un buffer para almacenar temporalmente los bytes del archivo
        byte[] buffer = new byte[1024];
        int bytesRead;

        String ruta = "Ruta enviada: [ " + recibido.getPath() + " ]";
        // envio los bytes del archivo de 100 en 100 caracteres
        while ((bytesRead = stream.read(buffer)) != -1) {
            String str = new String(buffer, 0, bytesRead);
            byte[] bytes = encriptar(kp, str, userAddress).getBytes();
            DatagramPacket paquete = new DatagramPacket(bytes, bytes.length, userAddress);
            filesServer.send(paquete);
        }
    }

    //encriptación asimétrica
    public static String encriptar(Map<SocketAddress, PublicKey> kp,String msg, SocketAddress socket) {

        byte[] bytes;
        PublicKey publicKey = kp.get(socket);
        try {
            Cipher cypher= Cipher.getInstance("RSA");
            cypher.init(Cipher.ENCRYPT_MODE, publicKey);
            bytes = cypher.doFinal(msg.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

}