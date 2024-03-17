package telnet_http;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ClienteFTP {
    public static void main(String[] args) throws IOException {
        FTPClient ftpClient  = new FTPClient();
        //otra vez vuelve a ser la del movil ipv4
        ftpClient.connect("192.168.10.76");
        ftpClient.login("lucia", "12345");
        if(ftpClient.isConnected()){
            System.out.println("Usuario conectado");
        }

        //subo un archivo a uno de los directorios que acabo de listar -> obtengo ruta y paso archivo con inputStream bytes
        //C:\Users\Usuario\IdeaProjects\PSP_Linux_1\src\telnet_http.ConexionTelnet.java
        File archivo = new File("C:\\Users\\Usuario\\IdeaProjects\\PSP_Linux_1\\src\\telnet_http.ConexionTelnet.java");
        FileInputStream fileInputStream = new FileInputStream(archivo);
        //lo meto en la carpeta
        //Stores a file on the server using the given name and taking input from the given InputStream.
        ftpClient.storeFile(archivo.getName(), fileInputStream);

        //se ve lo que hay dentro en la carpeta fpt2 de antonio (ls -l), ya tengo lista
        String[] nombres = ftpClient.listNames();
        for(String nombre : nombres){
            System.out.println(nombre);
        }

    }
}
