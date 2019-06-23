package net.londatiga.android.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import net.londatiga.android.utils.RestUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

import static android.content.ContentValues.TAG;

public class AutomaticoModoActivity extends Activity {
    TextView tvMensaje, tvApi,tvApi2;
    public static final String BROADCAST_ACTION = "net.londatiga.android.bluetooth";
    String baseUrl = "http://restapisoa.dx.am/restapi/v1";

    private final String TAG = "AutomaticoModoActivity";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.automatico_modo);

        tvMensaje = findViewById(R.id.tvMensaje);
        tvApi = findViewById(R.id.tvApi);
        tvApi2 = findViewById(R.id.tvApi2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, 1020);
        } else {

            iniciarLocalizacion();
        }


    }

    private void iniciarLocalizacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion localizacion = new Localizacion();

        localizacion.setAutomaticoModoActivity(this, tvMensaje);

        final boolean gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnable) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1001);

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, localizacion);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, localizacion);

        tvMensaje.setText("Obteniendo ubicacion ...");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarLocalizacion();
                return;
            }

        }
    }

    public void setEstadistica(String localidadRecibida) {
        String urlApiClima = this.baseUrl + "/" + localidadRecibida + "/estadistica";
        RestUtils restApiClima = new RestUtils(urlApiClima);
        try {
            JSONObject jsonObject = restApiClima.consumirApi();
            Integer precipitacion = jsonObject.getJSONObject("estadistica").getInt("precipitacionAnual");
            Integer temperaturaAnual = jsonObject.getJSONObject("estadistica").getInt("temperaturaAnual");
            tvApi2.setText(precipitacion +" " + temperaturaAnual);
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e1) {
            e1.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setClima(String localidadRecibida) {
        String urlApiClima = this.baseUrl + "/" + localidadRecibida + "/clima";
        RestUtils restApiClima = new RestUtils(urlApiClima);
        try {
            JSONObject jsonObject = restApiClima.consumirApi();
            String nombre = jsonObject.getJSONObject("clima").getString("nombre");
            Integer temperatura = jsonObject.getJSONObject("clima").getInt("temperatura");
            Integer probabilidad = jsonObject.getJSONObject("clima").getInt("probabilidad");
            tvApi.setText(nombre +" " + temperatura + " " + probabilidad);
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e1) {
            e1.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}



