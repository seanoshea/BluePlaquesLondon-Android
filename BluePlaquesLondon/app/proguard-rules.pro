# Blue Plaques London Android - ProGuard Rules
# Comprehensive obfuscation and optimization rules for release builds

# ================================================================================================
# GENERAL ANDROID RULES
# ================================================================================================

# Keep all annotations
-keepattributes *Annotation*

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable

# Keep generic signatures for reflection
-keepattributes Signature

# Keep inner classes
-keepattributes InnerClasses,EnclosingMethod

# ================================================================================================
# WEBVIEW RULES (for Wikipedia functionality)
# ================================================================================================

# Keep WebView JavaScript interfaces
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep WebView related classes
-keep class android.webkit.** { *; }
-dontwarn android.webkit.**

# Keep resource bundles
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

# Keep SafeParcelable
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

# Keep KeepName annotations
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Keep Parcelable implementations
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ================================================================================================
# SECURITY AND OBFUSCATION ENHANCEMENTS
# ================================================================================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Optimize and obfuscate
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Rename packages to make reverse engineering harder
-repackageclasses 'o'
-flattenpackagehierarchy 'o'

# ================================================================================================
# ANDROIDX AND JETPACK RULES
# ================================================================================================

# Keep AndroidX classes
-keep class androidx.** { *; }
-dontwarn androidx.**

# Navigation Component
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.**

# Lifecycle components
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Room database
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# ================================================================================================
# HILT DEPENDENCY INJECTION RULES
# ================================================================================================

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }
-keep class **_HiltModules { *; }
-keep class **_HiltModules$* { *; }

# Keep Hilt entry points
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ================================================================================================
# RXJAVA RULES
# ================================================================================================

# RxJava 3
-keep class io.reactivex.rxjava3.** { *; }
-dontwarn io.reactivex.rxjava3.**

# Keep RxJava internal classes
-keep class io.reactivex.rxjava3.internal.** { *; }
-dontwarn io.reactivex.rxjava3.internal.**

# ================================================================================================
# GOOGLE PLAY SERVICES AND MAPS RULES
# ================================================================================================

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# ================================================================================================
# FIREBASE RULES
# ================================================================================================

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# ================================================================================================
# RETROFIT AND NETWORKING RULES
# ================================================================================================

# Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ================================================================================================
# APPLICATION-SPECIFIC RULES
# ================================================================================================

# Keep all model classes (for JSON parsing and Parcelable)
-keep class com.upwardsnorthwards.blueplaqueslondon.model.** { *; }
-keep class com.upwardsnorthwards.blueplaqueslondon.data.local.entity.** { *; }

# Keep ViewModels
-keep class com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.** { *; }

# Keep Repository classes
-keep class com.upwardsnorthwards.blueplaqueslondon.data.repository.** { *; }

# Keep custom Application class
-keep class com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication { *; }

# ================================================================================================
# LEGACY RULES (maintained for compatibility)
# ================================================================================================
