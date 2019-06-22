package net.londatiga.android.bluetooth;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BackgroundRestService extends IntentService {

    private final String TAG = "BackgroundRestService";



    String baseUrl = "http://restapisoa.dx.am/restapi/v1";  // This is the API base URL (GitHub API)
    String url;

    public BackgroundRestService(){

        super("BackgroundRestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        this.url = this.baseUrl + "/Lomas de Zamora"+ "/clima";

        OkHttpClient client= new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;


        try {
            response = client.newCall(request).execute();
            String rs = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
