package com.example.android_notepad.screens.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.android_notepad.MainActivity;
import com.example.android_notepad.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 секунди

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Сховати статус бар
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Затримка перед переходом до другої активності
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Запустити головну активності
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // Закритти Splash Activity
                finish();
            }
        }, SPLASH_DELAY);

    }
}