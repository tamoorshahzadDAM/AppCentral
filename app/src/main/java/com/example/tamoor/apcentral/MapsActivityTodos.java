package com.example.tamoor.apcentral;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
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

public class MapsActivityTodos extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    obtenerUltimaPosBuses posBus = new obtenerUltimaPosBuses();
    LatLng[] arrayPosiciones;
    String matricula;
    String fecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_todos);
        posBus.execute();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void dibujarLineaMapa(ArrayList<LatLng> localizacion) {
        mMap.addPolyline(new PolylineOptions().addAll(localizacion).color(Color.BLUE));
    }


    /**
     * Methodo para obtener ultima posicion de bus, este metodo se ejecuta en segundo plano, y se
     * conecta con el servidor (Web services). Crea una lista de posiciones, con jsonarray recojo los valores
     * desde base de datos y lo guardo en un array. Luego hay un bucle que se va recorriendo hasta
     * que tiene valores. Con ayuda de jsonobject recojo posicion desde array y lo guardo en pos.
     * Saco los valores de columna y voy guardando en un variable. Asi al final en lista de arrays guardo
     * latitud y longitud.
     */
    private class obtenerUltimaPosBuses extends AsyncTask<Void, Void, Boolean> {

        public obtenerUltimaPosBuses() {
        }

        protected Boolean doInBackground(Void... params) {

            //Hace conexion a servidor
            boolean hecho = true;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://192.168.1.6:8080/AutobusNetBeans/webresources/generic/localizacion");
            get.setHeader("content-type", "application/json");

            try {
                HttpResponse resp = httpClient.execute(get);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray arrayPos = new JSONArray(respStr);
                //Arrays de posiciones
                arrayPosiciones = new LatLng[arrayPos.length()];
                //Mientras hay valores
                for (int i = 0; i < arrayPos.length(); i++) {

                    //Vou sacando y lo guardo en pos
                    JSONObject pos = arrayPos.getJSONObject(i);

                    //Saco las columnes de table y guardo cada uno en un varibale.
                    int idLoc = pos.getInt("ID_LOC");
                    double latitud = pos.getDouble("LATITUD");
                    double longitud = pos.getDouble("LONGITUD");
                    fecha = pos.getString("FECHA");
                    matricula = pos.getString("MATRICULA");

                    //latitud y longitud lo guardo en array de posiciones.
                    arrayPosiciones[i] = new LatLng(latitud, longitud);

                }
                if (!respStr.equals("true")) {
                    hecho = true;
                }
            } catch (Exception ex) {
                //En el caso de fallo.
                Log.e("ServicioRest", "Error!", ex);
                hecho = false;
            }
            return hecho;
        }

        /**
         * Metodo que se ejecuta despues de ultimo metodo para decir un resultado. Nos indica el
         * resultado de proceso de ejecucion que estaban en segundo plano.
         * @param result
         */
        protected void onPostExecute(Boolean result) {

            if (result) {
                //En caso de obtenido muestra este toast
                Toast.makeText(MapsActivityTodos.this, "Obtenido los posiciones", Toast.LENGTH_SHORT).show();
                //Mientras hay valores en arrays de posiciones.
                for (int i = 0; i < arrayPosiciones.length; i++) {
                    mMap.addMarker(new MarkerOptions().position(arrayPosiciones[i]).title(matricula).snippet(fecha));
                }
            } else {
                //En caso de fallo muestra este toast.
                Toast.makeText(MapsActivityTodos.this, "Posiciones NO obtenidas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}