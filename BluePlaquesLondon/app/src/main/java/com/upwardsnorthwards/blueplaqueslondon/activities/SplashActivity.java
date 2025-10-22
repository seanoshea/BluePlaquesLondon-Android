package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * Splash screen activity that displays the app logo and transitions to MainActivity.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        
        super.onCreate(savedInstanceState);
        
        // Start MainActivity immediately
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}