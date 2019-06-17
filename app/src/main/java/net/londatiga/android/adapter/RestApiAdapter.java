package net.londatiga.android.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.londatiga.android.entities.NotificationResponse;
import net.londatiga.android.utils.ConstantesRestApi;
import net.londatiga.android.utils.EndpointsApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiAdapter {

    public EndpointsApi establecerConexionRestApi(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantesRestApi.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(EndpointsApi.class);
    }

    public Gson convierteGsonDesearilizadorNotificaciones() {
        GsonBuilder gsonBuldier = new GsonBuilder();
        gsonBuldier.registerTypeAdapter(NotificationResponse.class, new NotificacionesDesearilizador());

        return gsonBuldier.create();
    }
}
