package telnet_http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HTTP {
  //esto es para conectarse a una api pública desde java
    public static void main(String[] args) throws IOException, JSONException {

        HttpURLConnection connection =(HttpURLConnection) new URL("https://jsonplaceholder.typicode.com/users").openConnection();
        //aqui le ponemos el tipo de llamada a api
        connection.setRequestMethod("GET");
        InputStream inputStream  = connection.getInputStream();

        //devuelve un proceso
        int c;
        String cadena="";
        //lo suyo es hacerlo con string builder
        while((c= inputStream.read())!=-1){
            //esto lo añade a la cadena
            cadena += (char) c;
            //esto lo pinta
            //System.out.println((char) c);
        }
        JSONArray jsonArray= new JSONArray(cadena);

        /*
        *  System.out.println(jsonArray.toString());
        * */
        //aqui lo paso a un objeto soloel del indice 1, luego lo meto en un Objeto de Clase
        JSONObject  objeto = jsonArray.getJSONObject(0);
        System.out.println(objeto);

        //lo diferencio con CLAVE-> VALOR
        System.out.println(objeto.get("id"));


    }
}
