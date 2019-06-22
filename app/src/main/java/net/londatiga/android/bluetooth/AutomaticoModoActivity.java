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

import static android.content.ContentValues.TAG;

public class AutomaticoModoActivity extends Activity {
    TextView tvMensaje,tvApi;
    public static final String BROADCAST_ACTION = "net.londatiga.android.bluetooth";
    BroadcastReceiverRest myBroadCastReceiver;
    private final String TAG = "AutomaticoModoActivity";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.automatico_modo);

        tvMensaje = findViewById(R.id.tvMensaje);
        tvApi = findViewById(R.id.tvApi);
       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, 1020);
        }
        else{
            iniciarLocalizacion();
        }


       /*
        //AGREGADO BROADCAST
        myBroadCastReceiver = new BroadcastReceiverRest();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(myBroadCastReceiver, intentFilter);

        //Invoco al api
        Intent intentApi = new Intent(AutomaticoModoActivity.this, BackgroundRestService.class);
        startService(intentApi);
        */
        //FIN AGREGADO

    }
    private  void iniciarLocalizacion(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion localizacion = new Localizacion();

        localizacion.setAutomaticoModoActivity(this,tvMensaje);
        final boolean gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!gpsEnable){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1001);

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, localizacion);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,localizacion);

        tvMensaje.setText("Localizacion agregada");
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] grantResults)
    {
        if(requestCode == 1000){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarLocalizacion();
                return;
            }

        }
    }

    class BroadcastReceiverRest extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Log.d(TAG, "onReceive() called");


                // uncomment this line if you had sent some data
                String data = intent.getStringExtra("nombre"); // data is a key specified to intent while sending broadcast
                tvApi.setText(data);
                // Log.e(TAG, "data=="+data);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}



