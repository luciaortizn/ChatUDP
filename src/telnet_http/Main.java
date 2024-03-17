package telnet_http;

import telnet_http.ConexionTelnet;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //red de tu movil esta no es

        ConexionTelnet conexionTelnet =  new ConexionTelnet("10.32.74.134");
        String prompt= "lucia@lucia-VirtualBox:~$ ";
        conexionTelnet.enviarComando("ls -l",prompt); //así uso los dos métodos

        /*¿?
        * Uso como cliente fpt mi servidor vsftpd*/

    }
}
