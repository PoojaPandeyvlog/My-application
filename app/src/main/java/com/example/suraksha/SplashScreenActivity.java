package com.example.suraksha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    Handler handler; Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run()
            {
                SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                String phone = sp.getString("phone", "guest");

                if (phone.equals("guest"))
                {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        handler.postDelayed(runnable, 2000);

    }
}