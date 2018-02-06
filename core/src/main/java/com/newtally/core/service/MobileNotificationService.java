package com.newtally.core.service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import com.blockcypher.utils.gson.GsonFactory;
import com.google.gson.Gson;
import com.newtally.core.dto.MobileNotification;
import com.newtally.core.dto.Notification;

public class MobileNotificationService{

   
 
// Method to send Notifications from server to client end.

    public final static String AUTH_KEY_FCM = "xyxyxyxxyxyxyxyxyxyxyxy";
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
       System.out.println(gson.toJson(mobileNotification).toString());
       wr.write(gson.toJson(mobileNotification));
       wr.flush();
       conn.getInputStream();
    }
}
