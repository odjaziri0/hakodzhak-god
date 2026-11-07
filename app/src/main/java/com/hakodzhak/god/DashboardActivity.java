package com.hakodzhak.god;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
public class DashboardActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Button btnCookies = findViewById(R.id.btn_cookies);
        btnCookies.setOnClickListener(v -> startActivity(new Intent(this, CookieStealerActivity.class)));
    }
}
