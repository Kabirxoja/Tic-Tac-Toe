# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Save annotation
-keepattributes *Annotation*

# Parcelable implementation
-keepclassmembers class * implements android.os.Parcelable{
    static ** CREATOR;
}

# Navigation Component
-keepclassmembers class * {
    @androidx.navigation.NavArgs <fields>;
}
-keepclassmembers class **NavDirections {
    <methods>;
}
-keepclassmembers class **NavArgs {
    <methods>;
}

# Lottie animation
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# Google Play In-App Update
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**

# Admob Ads
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Yandex Ads
-keep class com.yandex.mobile.ads.** { *; }
-dontwarn com.yandex.mobile.ads.**

# Yandex AdMob adapte
-keep class com.yandex.ads.adapter.admob.** { *; }
-dontwarn com.yandex.ads.adapter.admob.**

# Yandex metrica (AppMetrica)
-keep class com.yandex.metrica.** { *; }
-dontwarn com.yandex.metrica.**


