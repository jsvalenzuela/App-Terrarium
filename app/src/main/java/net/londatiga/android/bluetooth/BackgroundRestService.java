package net.londatiga.android.bluetooth;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import net.londatiga.android.adapter.RestApiAdapter;
import net.londatiga.android.entities.Notification;
import net.londatiga.android.entities.NotificationResponse;
import net.londatiga.android.utils.EndpointsApi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundRestService extends IntentService {

    private final String TAG = "BackgroundRestService";
    ;
    public BackgroundRestService(){

        super("BackgroundRestService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        /*SE CREA METODO PARA CONSUMIR REST*/
            Notification notifications = null;
            RestApiAdapter restApiAdapter = new RestApiAdapter();
            Gson gson = restApiAdapter.convierteGsonDesearilizadorNotificaciones();
            EndpointsApi endpointsApi = restApiAdapter.establecerConexionRestApi(gson);

            Call<NotificationResponse> responseCall;
            responseCall = endpointsApi.getNotificaciones();


       try {
            //Response<NotificationResponse> response= responseCall.execute();
            NotificationResponse notificationResponse = responseCall.execute().body();

            if (notificationResponse != null) {
               notifications = notificationResponse.getNotifications();
            }
            Log.d(TAG, "Service Start");
           sendMyBroadCast(notifications);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*FIN METODO*/
    }


    private void sendMyBroadCast(Notification notification)
    {
        try
        {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(MainActivity.BROADCAST_ACTION);
            broadCastIntent.putExtra("userId",notification.getUserId());
            broadCastIntent.putExtra("id", notification.getId());
            broadCastIntent.putExtra("title",notification.getTitle());
            broadCastIntent.putExtra("completed",notification.isCompleted());
            ///uncomment this line if you want to send data
         // broadCastIntent.putExtra(

            sendBroadcast(broadCastIntent);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
