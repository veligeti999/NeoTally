package com.newtally.core.service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import com.blockcypher.utils.gson.GsonFactory;
import com.google.gson.Gson;
import com.newtally.core.dto.MobileNotification;
import com.newtally.core.dto.Notification;

public class MobileNotificationService {

   
 
// Method to send Notifications from server to client end.

    public final static String AUTH_KEY_FCM = "AAAAqdFLnhA:APA91bHTADbLMqh3pxoG_caBNorzQ0bqbzIPWE1k_6cSStMniYzJ9QWoc3D1EGlONDZ9mlb6zBf2aHpQFJdnJiEeejog0zQ78sG9S5wZEcKs9rJvNBpDEDbg0004irM8mv3vtoy_9bVI";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    public final static Gson gson = GsonFactory.getGson();

    // userDeviceIdKey is the device id you will query from your database

    public static void pushFCMNotification(String userDeviceIdKey, Notification notification) throws Exception{

       String authKey = AUTH_KEY_FCM; // You FCM AUTH key
       String FMCurl = API_URL_FCM; 

       URL url = new URL(FMCurl);
       HttpURLConnection conn = (HttpURLConnection) url.openConnection();

       conn.setUseCaches(false);
       conn.setDoInput(true);
       conn.setDoOutput(true);

       conn.setRequestMethod("POST");
       conn.setRequestProperty("Authorization","key="+authKey);
       conn.setRequestProperty("Content-Type","application/json");

       MobileNotification mobileNotification = new MobileNotification();
       mobileNotification.setTo(userDeviceIdKey.trim());
       mobileNotification.setNotification(notification);

       OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
       System.out.println(gson.toJson(mobileNotification));
       wr.write(gson.toJson(mobileNotification));
       wr.flush();
       conn.getInputStream();
    }
}
