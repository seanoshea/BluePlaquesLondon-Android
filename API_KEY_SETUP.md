# Google Maps API Key Configuration Guide

## Current Status
The application is running and displaying the map UI. Key issues have been addressed:
- ✅ NavController IllegalStateException - Fixed with lazy initialization
- ✅ RxJava threading issues - Fixed with proper scheduler configuration
- ✅ OnBackInvokedCallback warning - Fixed with manifest configuration
- ⚠️ Google Maps API authorization may still need verification

## Current Configuration
- **API Key**: `AIzaSyBYmrcXv36JnYS7Jth29_nWpL88xjU9GJ8`
- **Package Name**: `com.upwardsnorthwards.blueplaqueslondon`
- **Debug SHA-1 Fingerprint**: `1F:07:71:29:F9:30:9D:EB:4C:76:8A:89:8C:B4:51:68:B8:C2:08:A6`
- **Location**: Configured in `app/src/main/AndroidManifest.xml` under `com.google.android.geo.API_KEY`
- **Status**: Updated to match Google Cloud Console API key (2024-10-22)

## Steps to Resolve API Key Authorization

### 1. Verify API Key in Google Cloud Console
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select the project associated with your API key
3. Navigate to **APIs & Services** > **Credentials**
4. Find the API key: `AIzaSyBYmrcXv36JnYS7Jth29_nWpL88xjU9GJ8`

### 2. Enable Maps SDK for Android
1. In the Credentials page, click on the API key
2. Under **API restrictions**, ensure **Maps SDK for Android** is enabled
3. If not, click **Edit** and add **Maps SDK for Android** to the list of restricted APIs

### 3. Configure Android Certificate Restrictions
1. In the API key details page, scroll to **Application restrictions**
2. Select **Android apps** from the dropdown
3. Click **+ Add package name and fingerprint**
4. Add the following entries:

   **For Debug Builds:**
   - Package name: `com.upwardsnorthwards.blueplaqueslondon`
   - SHA-1 certificate fingerprint: `1F:07:71:29:F9:30:9D:EB:4C:76:8A:89:8C:B4:51:68:B8:C2:08:A6`

   **For Release Builds:**
   - Package name: `com.upwardsnorthwards.blueplaqueslondon`
   - SHA-1 certificate fingerprint: (get from your release keystore)
     ```bash
     keytool -list -v -keystore path/to/production.keystore -alias your_alias | grep SHA1
     ```

### 4. Save Changes
- Click **Save** in the API key details page
- Changes may take a few minutes to propagate

## Getting Release Keystore SHA-1 Fingerprint
If you have a production keystore file, get its SHA-1 fingerprint with:
```bash
keytool -list -v -keystore ~/production.keystore -storepass your_password | grep SHA1
```

## Testing the Fix
1. Rebuild and run the app:
   ```bash
   ./gradlew assembleDebug
   adb install -r build/outputs/apk/debug/app-debug.apk
   ```
2. Check logcat for Google Maps API messages:
   ```bash
   adb logcat | grep -i "google\|maps\|authorization"
   ```
3. If authorization succeeds, you should see the map render properly without the "Not on main thread" exceptions (which were fixed separately)

## Troubleshooting
- **Still seeing "Authorization failure"**: Check that Maps SDK for Android is explicitly enabled in API restrictions
- **Certificate fingerprint mismatch**: Ensure the exact SHA-1 from your keystore matches what's in the Console
- **Package name mismatch**: Verify `com.upwardsnorthwards.blueplaqueslondon` matches exactly (case-sensitive)
- **Changes not taking effect**: Wait 5-10 minutes for Google Cloud to propagate the changes

## Related Issues Fixed
- ✅ NavController IllegalStateException - Fixed with proper lazy initialization and view hierarchy checks
- ✅ RxJava threading issues - Fixed by adding proper `.subscribeOn(Schedulers.io())` and `.observeOn(AndroidSchedulers.mainThread())` to all RxJava chains
- ✅ OnBackInvokedCallback warning - Fixed by adding `android:enableOnBackInvokedCallback="true"` to AndroidManifest.xml
- ✅ Google Maps null pointer protection - Added null checks before map operations
- ⚠️ WorkManager Hilt integration - Disabled pending proper Configuration.Provider implementation
