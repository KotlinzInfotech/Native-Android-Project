# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/jdeffibaugh/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
#-dontskipnonpubliclibraryclasses
#-dontobfuscate
#-forceprocessing
#-optimizationpasses 5
##
#-keep class javax.** { *; }
#-keep class org.** { *; }
#-keep class okhttp3.** { *; }
#-keep class com.squareup.picasso.** { *; }

 -dontwarn okhttp3.**
  -dontwarn okio.**
 -dontwarn com.squareup.picasso.**

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

 -dontwarn com.intel.inde.mp.**

-keep public class com.google.android.gms.* { public *; }
-keep class com.google.firebase.** { *; }

-keep class com.arthenica.mobileffmpeg.Config {
    native <methods>;
    void log(long, int, byte[]);
    void statistics(long, int, float, float, long , int, double, double);
}

-keep class com.arthenica.mobileffmpeg.AbiDetect {
    native <methods>;
}

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:



#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-verbose

#-injars release/AndroidPlugin.jar
-verbose
-repackageclasses ''
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
## Below are the suggested rules from the developer documentation:
## https://developers.optimizely.com/x/solutions/sdks/reference/index.html?language=android&platform=mobile#installation

# Optimizely

-ignorewarnings
-keep class * {
    public private *;
}