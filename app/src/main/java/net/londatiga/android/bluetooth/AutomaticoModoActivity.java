package net.londatiga.android.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.londatiga.android.utils.RestUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AutomaticoModoActivity extends Activity implements AdapterView.OnItemSelectedListener {

    TextView tvMensaje, tvApi,tvApi2, tvApi3;
    Spinner spinner ;
    Button btnButton;
    public static final String BROADCAST_ACTION = "net.londatiga.android.bluetooth";
    String baseUrl = "http://restapisoa.dx.am/restapi/v1";
    private final String TAG = "AutomaticoModoActivity";
    private Integer indiceLang ;
    private String localidadObtenida;
    protected LocationManager locationManager;
    protected  Localizacion localizacion;
    private  Integer probabilidad,huMax,tempMax;

    private ArrayList<BluetoothDevice> mDeviceList;
    private String modoElegido;

    HiloApi hiloApi;
    public void setLocalidadObtenida(String parametro){
        this.localidadObtenida = parametro;
        hiloApi.start();
        locationManager.removeUpdates(localizacion);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //obtengo por medio de un Bundle del intent la lista de dispositivos encontrados
        mDeviceList = getIntent().getExtras().getParcelableArrayList("device.list");

        //Obtengo el modo que me servira para iniciar el activity correspondiente
        modoElegido = getIntent().getExtras().getString("modoElegido");

        hiloApi = new HiloApi();
        setContentView(R.layout.automatico_modo);

        tvMensaje = findViewById(R.id.tvMensaje);
        tvApi = findViewById(R.id.tvApi);
        tvApi2 = findViewById(R.id.tvApi2);
        tvApi3 = findViewById(R.id.tvApi3);

        spinner = findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.plantas,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        btnButton = findViewById(R.id.button);
        btnButton.setOnClickListener(btnButtonListener);
        if(!checkLocationPermission("android.permission.ACCESS_COARSE_LOCATION")){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, 1020);
        } else {

            iniciarLocalizacion();
        }


    }

    public boolean checkLocationPermission(String permission)
    {
        //String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }



    public void onResume() {
        super.onResume();


    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AutomaticoModoActivity.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " no esta activado. Desea activarlo?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settingsIntent);
                        //AutomaticoModoActivity.this.startActivity(intent);
                    }
                });
    }

    private void iniciarLocalizacion(){


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         localizacion = new Localizacion();

        localizacion.setAutomaticoModoActivity(this, tvMensaje);

        final boolean gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnable) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if(!checkLocationPermission("android.permission.ACCESS_FINE_LOCATION")){
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {*/
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1001);

        }

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,localizacion,null);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,localizacion,null);
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



    private class HiloApi extends Thread {
        public void run()
        {
            setEstadistica(localidadObtenida);
            setClima(localidadObtenida);
        }

    }

    public void setRangoTemperaturas(){

        String urlApiClima = this.baseUrl + "/" + localidadObtenida + "/Humedo"+ "/Tomate";
        RestUtils restApiClima = new RestUtils(urlApiClima);
        try {
            JSONObject jsonObject = restApiClima.consumirApi();
            this.huMax = jsonObject.getJSONObject("suelo").getInt("humax");
            this.tempMax = jsonObject.getJSONObject("suelo").getInt("tempmax");

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e1) {
            e1.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setEstadistica(String localidadRecibida) {
        String urlApiClima = this.baseUrl + "/" + localidadRecibida + "/estadistica";
        RestUtils restApiClima = new RestUtils(urlApiClima);
        try {
            JSONObject jsonObject = restApiClima.consumirApi();
            Integer precipitacion = jsonObject.getJSONObject("estadistica").getInt("precipitacionAnual");
            Integer temperaturaAnual = jsonObject.getJSONObject("estadistica").getInt("temperaturaAnual");

            indiceLang = precipitacion / temperaturaAnual ;
            tvApi3.setText("indiceLang: "+indiceLang) ;
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
             this.probabilidad = jsonObject.getJSONObject("clima").getInt("probabilidad");

            tvApi.setText(nombre +" " + temperatura + " " + probabilidad);

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e1) {
            e1.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener btnButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //setRangoTemperaturas();
            Intent intent = new Intent(AutomaticoModoActivity.this, DeviceListActivity.class);

            intent.putParcelableArrayListExtra("device.list", mDeviceList);
            intent.putExtra("modoElegido", "automatico");
            startActivity(intent);

        }

    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //String text = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_LONG).show();
        
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}



