package chat;

import javax.naming.AuthenticationNotSupportedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1122);
        ReentrantLock rl = new ReentrantLock();
        Map<String, Socket> conexiones = new HashMap<>();

        while(true){
            Thread thread = new Thread(()->{
                try{
                    //modifico los datos del usuario al enviarlo pero el mapeo no lo cambio
                    //no pongo el rl cuando envío el mensaje porque si no hay mensaje se queda pensando.

                    Socket socket = ss.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String user = br.readLine();
                    rl.lock();

                    if(conexiones.get(user)==null){
                        conexiones.put(user, socket);
                        System.out.println("user: "+ user);

                    }
                    rl.unlock();
                    String remitente = br.readLine();
                    Socket socket1 = conexiones.get(remitente);
                    if(socket1!=null){
                        String mensaje = br.readLine();
                        OutputStream os = socket1.getOutputStream();
                        os.write((mensaje +"\n").getBytes());
                        os.flush();
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

            });

            thread.start();
        }

    }


}
