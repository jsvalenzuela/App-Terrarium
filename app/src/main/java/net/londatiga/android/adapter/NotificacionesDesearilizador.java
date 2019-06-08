package net.londatiga.android.adapter;



import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.londatiga.android.entities.Notification;
import net.londatiga.android.entities.NotificationResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NotificacionesDesearilizador implements JsonDeserializer<NotificationResponse> {


    @Override
    public NotificationResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject notificationsResponseData = json.getAsJsonObject();
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setNotifications(desearilizadorNotifications(notificationsResponseData));
        return notificationResponse;
    }

    private Notification desearilizadorNotifications(JsonObject notificacionesResponseData) {


            JsonObject jsonObject = notificacionesResponseData.getAsJsonObject();

            Notification notification = new Notification();



            notification.setUserId(jsonObject.get("userId").getAsInt());
            notification.setId(jsonObject.get("id").getAsInt());
            notification.setTitle(jsonObject.get("title").getAsString());
           notification.setCompleted(jsonObject.get("completed").getAsBoolean());




        return notification;

    }
}
