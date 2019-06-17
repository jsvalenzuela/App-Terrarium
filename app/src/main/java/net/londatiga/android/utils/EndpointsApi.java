package net.londatiga.android.utils;

import net.londatiga.android.entities.NotificationResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static net.londatiga.android.utils.ConstantesRestApi.URL_GET_NOTIFICACIONES;

public interface EndpointsApi {


    @GET(URL_GET_NOTIFICACIONES)
    Call<NotificationResponse> getNotificaciones();
}
