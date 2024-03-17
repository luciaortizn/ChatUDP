package ejercicio1;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConexionTelnet {

    TelnetClient telnetClient ;
    InputStream inputStream;
    OutputStream outputStream;
    String host;
    public ConexionTelnet(String host) throws IOException {

        telnetClient= new TelnetClient();
        telnetClient.connect(host);
        this.inputStream = telnetClient.getInputStream();
        this.outputStream = telnetClient.getOutputStream();
        /* leerSalida("login:");
        leerSalida("lucia:");
        leerSalida("Password:");
        leerSalida("12345:");
        leerSalida("lucia@lucia-VirtualBox:~$ ");
        * */

        //ahora contendría esta línea lucia@lucia-VirtualBox:~$
    }
    public void escribirComando(String comando) throws IOException {
        outputStream.write((comando +"\n").getBytes());
        outputStream.flush();
    }
    public void leerSalida(String parada) throws IOException {
        String cadena="";
        int c;
        while(!cadena.contains(parada)){
            c = inputStream.read();
            System.out.println((char)c);
        }

    }
    public void enviarComando(String comando, String comando1) throws IOException {
        escribirComando(comando);
        leerSalida(comando1);
    }
}
