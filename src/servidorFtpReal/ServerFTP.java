package servidorFtpReal;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFTP {
    public static void main(String[] args) throws IOException {
        //get property para obtener rutas de mi ordenador
        System.out.println(System.getProperty("user.home"));

        String ruta ="C:\\Users\\Usuario\\Desktop\\pruebaArchivo.java";
        System.out.println(ruta);
        //lo crea si no existe, fos permite escribir bytes en un fichero binario (necesita cast)
        FileOutputStream fos = new FileOutputStream(ruta);

        ServerSocket ss = new ServerSocket(1235);
        Socket socket = ss.accept();
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        int size = Integer.parseInt(br.readLine());


        //pillar bytes y mandarlos al otro proceso
        int c;
        int cont=0;
        //contador de posiciones que sea menor al tamaño del archivo
        /*(c = is.read())!=-1*/
        while(cont<size ){
            cont++;
            c = is.read();
          //  System.out.print((char)c );
            //al mismo tiempo que lo recibo
            fos.write(c);

        }

    }
}
