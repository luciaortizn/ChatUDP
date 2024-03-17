package chat;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Cliente {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1122);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader br1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ReentrantLock rl = new ReentrantLock();
        OutputStream os = socket.getOutputStream();

        System.out.println("Cómo te llamas");
        enviarServer(br.readLine(), os);

        System.out.println("A quién se lo envías");
        enviarServer(br.readLine(), os);

        while(true){
            System.out.println("Qué quieres");
            enviarServer(br.readLine(), os);

            Thread thread = new Thread(()->{
                while(true){

                    try {
                        String linea;
                        while((linea = br1.readLine())!= null){
                            System.out.println(linea);
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }
    }
    public static void enviarServer(String comando, OutputStream os) throws IOException {
        os.write((comando+"\n").getBytes());
        os.flush();
    }
}
