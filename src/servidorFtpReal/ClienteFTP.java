package servidorFtpReal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClienteFTP {

    public static void main(String[] args) throws IOException {
        String rutaArchivo =  "C:\\Users\\Usuario\\IdeaProjects\\PSP_Linux_1\\src\\servidorFtpReal\\ServerFTP.java";
        System.out.println(rutaArchivo);
        FileInputStream fis = new FileInputStream(rutaArchivo);
        Socket socket = new Socket("localhost",1235);

        OutputStream os = socket.getOutputStream();

        //mando tamaño archivo
        int fileSize = fis.available(); //tamaño del archivo
        System.out.println(fileSize);
        //tamaño ->os
        os.write((fileSize +"\n").getBytes());

        int c;
        String cadena = "";
        //no me interesa mandar info de línea a línea, mejor pillar el String con la salida
        while((c = fis.read())!=-1){

            cadena = cadena+(char)c;
        }

        os.write((cadena+ "\n").getBytes());
        os.flush();
        //se resetea el socket siempre
    }

}
