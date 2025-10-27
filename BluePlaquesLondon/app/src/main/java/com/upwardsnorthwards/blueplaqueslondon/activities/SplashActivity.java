package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * Splash screen activity implementing Android 12+ Splash Screen API.
 * 
 * <p>This activity serves as the application's entry point, providing a branded
 * splash screen experience while the app initializes. It uses the modern Android 12+
 * Splash Screen API for system-integrated splash screen behavior.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Modern Android 12+ Splash Screen API integration</li>
 *   <li>Custom blue plaque vector icon with app branding</li>
 *   <li>Smooth transition to {@link MainActivity}</li>
 *   <li>System-integrated animation and theming</li>
 *   <li>Backward compatibility with Android 11+ devices</li>
 * </ul>
 * 
 * <h3>Implementation Details:</h3>
 * <p>The splash screen is configured through the SplashTheme in styles.xml with:</p>
 * <ul>
 *   <li>Custom background color matching app theme</li>
 *   <li>Animated vector icon representing blue plaques</li>
 *   <li>1-second animation duration for optimal user experience</li>
 *   <li>Automatic transition to main app theme</li>
 * </ul>
 * 
 * <h3>Usage:</h3>
 * <p>This activity is automatically launched as the main launcher activity.
 * No direct instantiation is required - the Android system handles the lifecycle.</p>
 * 
 * @see MainActivity
 * @see androidx.core.splashscreen.SplashScreen
 * 
 * @author Blue Plaques London Team
 * @since 3.0
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Initializes the splash screen and transitions to the main application.
     * 
     * <p>This method implements the Android 12+ Splash Screen API workflow:</p>
     * <ol>
     *   <li>Installs the splash screen using {@link SplashScreen#installSplashScreen(Activity)}</li>
     *   <li>Allows the system to handle splash screen animation and timing</li>
     *   <li>Immediately transitions to {@link MainActivity}</li>
     *   <li>Finishes this activity to prevent back navigation to splash</li>
     * </ol>
     * 
     * <p>The splash screen appearance and behavior are controlled by the SplashTheme
     * defined in styles.xml, including the custom blue plaque icon and animation duration.</p>
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this Bundle contains the most recent data.
     *                          Otherwise it is null.
     * 
     * @see SplashScreen#installSplashScreen(Activity)
     * @see MainActivity
     */
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