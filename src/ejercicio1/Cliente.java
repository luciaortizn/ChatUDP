package ejercicio1;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class Cliente {
    public static void main(String[] args) throws IOException {
        FTPClient ftpClient  = new FTPClient();

        //otra vez vuelve a ser la del movil ipv4  192.168.10.7
        ftpClient.connect("192.168.10.7");
        ftpClient.login("lucia", "12345");
        if(ftpClient.isConnected()){
            System.out.println("Usuario conectado");
        }
        //requisitos del ejercicio
      }

}
