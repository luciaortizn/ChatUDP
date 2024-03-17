package ejercicio2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Ejercicio2 {

   /**
2) Inicia un nuevo proyecto Java en tu entorno de desarrollo preferido.
    Utiliza las clases del paquete java.net para realizar una solicitud telnet_http.HTTP GET a la siguiente URL: https://jsonplaceholder.typicode.com/posts.
    Recibe la respuesta del servidor y analiza el contenido JSON.
    Almacena en una lista solo aquellos objetos JSON que tengan un ID par.
   **/
    public static void main(String[] args) throws IOException, JSONException {

        HttpURLConnection httpuc = (HttpURLConnection) new URL("https://jsonplaceholder.typicode.com/users").openConnection();

        InputStream is = httpuc.getInputStream();
        String cadena= "";

        int c;
        while((c = is.read())!=-1){
            cadena = cadena + (char)c;

        }
        JSONArray ja = new JSONArray();
        //esto mejor hacerlo en un proyecto con dependencias
        ArrayList<JSONObject> idPares = new ArrayList<>();
      for(int i = 0; i< ja.length(); i++){
        JSONObject jo = ja.getJSONObject(i);
        if(jo.getInt("id")% 2 ==0){
            idPares.add(jo);
            System.out.println(jo);

        }

      }

    }




}
