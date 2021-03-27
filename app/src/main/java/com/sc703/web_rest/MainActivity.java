package com.sc703.web_rest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button btnAgregar, btnSincronizar;
    private EditText edtID, edtNombre;
    private String respuesta;
    private StringBuffer respuestaWeb;
    private URL url;
    private Activity activity;
    private ArrayList<Pais> paises = new ArrayList<Pais>();
    private ProgressDialog dialog;
    private ListView lvLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        btnSincronizar = (Button) findViewById(R.id.btn_Sincronizar);
        btnAgregar = (Button) findViewById(R.id.btn_Agregar);
        edtID = (EditText) findViewById(R.id.edt_ID);
        edtNombre = (EditText) findViewById(R.id.edt_Nombre);
        lvLista = (ListView) findViewById(R.id.Lv_Lista);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarDatos()){
                    if(validarID()){
                        if (validarNombre()) {
                            new PostDatosPaises().execute();
                        }
                    }
                }

            }
        });

        btnSincronizar.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paises.clear();
                new ObtenerDatosPais().execute();
            }
        }));
    }

    class ObtenerDatosPais extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage(("Sincronizando datos..."));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try{
                url = new URL("https://6049666bfb5dcc0017969ef4.mockapi.io/Paises");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");

                int codigoRespuesta = connection.getResponseCode();

                if(codigoRespuesta == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String stringSalida;
                    respuestaWeb = new StringBuffer();

                    while((stringSalida = reader.readLine())!=null){
                        respuestaWeb.append(stringSalida);
                    }
                    reader.close();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo obtener datos del servidor", Toast.LENGTH_SHORT).show();
                }

                respuesta = respuestaWeb.toString();

                try{
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int ID = jsonObject.getInt("ID");
                        String nombre = jsonObject.getString("Nombre");
                        Log.d("1", "ID: " + ID);
                        Log.d("1", "Nombre: " + nombre);

                        Pais pais = new Pais(ID, nombre);

                        paises.add(pais);

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (dialog.isShowing()){
                dialog.dismiss();
            }
            Adaptador adaptador = new Adaptador(activity,paises);
            lvLista.setAdapter(adaptador);


        }
    }

    class PostDatosPaises extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Enviando datos....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String resultado = null;
            try{
                url = new URL("https://6049666bfb5dcc0017969ef4.mockapi.io/Paises");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                Integer ID = Integer.parseInt(edtID.getText().toString());
                String nombre = edtNombre.getText().toString();
                JSONObject parametrosPOST = new JSONObject();
                parametrosPOST.put("ID", ID);
                parametrosPOST.put("Nombre", nombre);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(obtenerStringPost(parametrosPOST));
                writer.flush();
                writer.close();
                outputStream.close();

                int codigoRespuesta = connection.getResponseCode();

                if(codigoRespuesta == HttpURLConnection.HTTP_ACCEPTED){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer("");
                    String linea = "";

                    while((linea = reader.readLine())!=null){
                        stringBuffer.append(linea);
                        break;
                    }
                    reader.close();
                    resultado = stringBuffer.toString();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
    public String obtenerStringPost(JSONObject parametros) throws Exception{
        StringBuilder resultado = new StringBuilder();

        boolean primero = true;

        Iterator<String> iterator = parametros.keys();
        while (iterator.hasNext()){
            String llave = iterator.next();
            Object valor = parametros.get(llave);

            if (primero){
                primero = false;
            }else{
                resultado.append("&");
            }

            resultado.append(URLEncoder.encode(llave, "UTF-8"));
            resultado.append("=");
            resultado.append(URLEncoder.encode(valor.toString(), "UTF-8"));
        }
        return resultado.toString();
    }

    public boolean validarDatos(){
        boolean validacion = true;

        String ID = edtID.getText().toString();
        String nombre = edtNombre.getText().toString();

        if (ID.isEmpty()){
            edtID.setError("El ID es obligatorio");
            validacion = false;
        }
        if (nombre.isEmpty()){
            edtNombre.setError("El Nombre del País es obligatorio");
            validacion = false;
        }
        return validacion;
    }
    public boolean validarID(){
        boolean validacion = true;

        String ID = edtID.getText().toString();

        Pattern pID = Pattern.compile("(?=.*[a-zA-Z]+)");
        if ((pID.matcher(ID)).matches()){
            edtID.setError("El ID es debe ser un valor númerico");
            validacion = false;
        }
        return validacion;
    }

    public boolean validarNombre(){
        boolean validacion = true;
        String nombre = edtNombre.getText().toString();

        Pattern pNombre = Pattern.compile("(?=.*[0-9])");
        if (pNombre.matcher(nombre).find()){
            edtNombre.setError("El nombre del país debe contener sólo letras");
            validacion = false;
        }
        return validacion;
    }
}