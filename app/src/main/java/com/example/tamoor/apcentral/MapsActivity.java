package com.example.tamoor.apcentral;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    MapView mapView;
    ArrayList<LatLng> arrayPosiciones;
    String matricula;


    /**
     * Metodo on create, en este metodo se inicializa el mapView, obtiene el suprte de fragmentos y
     * eso notifica cuando el mapa esta listo. Se inicializa el intent y recibo el valor de matricula
     * enviado desde otro intent.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Recibo el valor.
        Intent intent = getIntent();
        matricula = intent.getStringExtra("matricula");

        //Metodo que se ejecuta en segundo plano, para dibujar el mapa. Primero lo instancio
        ObtenerLocalizacion obLoc = new ObtenerLocalizacion();
        //Lo ejecuto
        obLoc.execute();


    }

    /**
     * Metodo que pinta las lineas en el mapa, por parametros le pasamos localizacion.
     * @param localizacion
     */
    public void dibujarLineaMapa(ArrayList<LatLng> localizacion) {
        mMap.addPolyline(new PolylineOptions().addAll(localizacion).color(Color.BLUE));
    }


    /**
     * MEtodo onMapready, cuando el mapa esta listo, lo guardo en mMap.
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * Methodo para obtener localizaciones, este metodo se ejecuta en segundo plano, y se conecta con
     * el servidor (Web services). Crea una lista de posiciones, con jsonarray recojo los valores
     * desde base de datos y lo guardo en este array. Luego hay un bucle que se va recorriendo hasta
     * que tiene valores. Con ayuda de jsonobject recojo posicion desde array y lo guardo en pos.
     * Saco los valores de columna y voy guardando en un variable. Asi al final en lista de arrays guardo
     * latitud y longitud.
     */
    private class ObtenerLocalizacion extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... params) {

            boolean result = true;

            HttpClient httpClient = new DefaultHttpClient();

            //Hace conexion con servidor
            HttpGet get =
                    new HttpGet("http://192.168.1.6:8080/AutobusNetBeans/webresources/generic/ultimaPos/" + matricula);

            get.setHeader("content-type", "application/json");

            try {
                HttpResponse resp = httpClient.execute(get);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray arrayDePos = new JSONArray(respStr);

                //Se crea una lista
                ArrayList<LatLng> arrayPosiciones = new ArrayList<>();

                for (int i = 0; i < arrayDePos.length(); i++) {

                    //Guardo en variable pos
                    JSONObject pos = arrayDePos.getJSONObject(i);

                    //Sacamos las columnes desde base de datos
                    int idLoc = pos.getInt("ID_LOC");
                    double latitud = pos.getDouble("LATITUD");
                    double longitud = pos.getDouble("LONGITUD");
                    String fecha = pos.getString("FECHA");
                    matricula = pos.getString("MATRICULA");
                    //Latitud y longitud lo guarda en la lista de posiciones.
                    arrayPosiciones.add(new LatLng(latitud, longitud));
                }
                if (!respStr.equals("true")) {
                    result = true;
                }
            } catch (Exception ex) {
                //En caso de fallo muestra error.
                Log.e("ServicioRest", "Error!", ex);
                result = false;
            }
            return result;
        }

        /**
         * Metodo que se ejecuta despues de ultimo metodo para decir un resultado. Nos indica el
         * resultado de proceso de ejecucion que estaban en segundo plano . con ayuda de otro metodo
         * lo pinto en mapa.
         * @param result
         */
        protected void onPostExecute(Boolean result) {
            if (result) {
                //En caso de tenerlos muestra este toast
                Toast.makeText(MapsActivity.this, arrayPosiciones.get(0).toString() + matricula, Toast.LENGTH_SHORT).show();
                //mientras hay valores voy marcando en la mapa
                for (int i = 0; i < arrayPosiciones.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(arrayPosiciones.get(i)));
                }
                //Al final metodo que pita las lineas.
                dibujarLineaMapa(arrayPosiciones);
            } else {
                //En el caso de fallo muestra este error.
                Toast.makeText(MapsActivity.this, "No es possible", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
