package com.hakodzhak.god;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText etToken = findViewById(R.id.et_token);
        EditText etChatId = findViewById(R.id.et_chat_id);
        Button btnLogin = findViewById(R.id.btn_login);
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        etToken.setText(prefs.getString("botToken", ""));
        etChatId.setText(prefs.getString("chatId", ""));
        btnLogin.setOnClickListener(v -> {
            String token = etToken.getText().toString().trim();
            String chatId = etChatId.getText().toString().trim();
            if (token.isEmpty() || chatId.isEmpty()) {
                Toast.makeText(this, "أدخل البيانات", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("botToken", token).putString("chatId", chatId).apply();
            startActivity(new Intent(this, DashboardActivity.class));
        });
    }
}
