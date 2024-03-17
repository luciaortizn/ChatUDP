package ejercicio1;

import java.io.IOException;

public class Main {
    //red de tu movil esta no es

    ConexionTelnet conexionTelnet =  new ConexionTelnet("10.32.74.134");
    String prompt= "lucia@lucia-VirtualBox:~$ ";

    public Main() throws IOException {
        conexionTelnet.enviarComando("ls -l", prompt);

    }
       // conexionTelnet.enviarComando("ls -l",prompt); //así uso los dos métodos

    /*  /*
        *
- ls o dir: Lista los archivos y directorios en el directorio actual.
- get <nombre_archivo>: Descarga el archivo especificado del servidor FTP al directorio local.
- put <nombre_archivo>: Sube el archivo especificado desde el directorio local al servidor FTP.
- quit: Cierra la sesión y desconéctate del servidor FTP.
*/


    /*¿?
     * Uso como cliente fpt mi servidor vsftpd*/
}
