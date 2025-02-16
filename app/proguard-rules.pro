# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/dpr/eclipse-adt/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsconfigurationafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------


-dontobfuscate
-keep class * extends com.odoo.core.orm.OModel{*;}
# Searchview v4
-keep class android.support.v7.widget.SearchView { *; }

#Custom
-keepattributes SourceFile,LineNumberTable
-keep class com.parse.*{ *; }
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**
-dontwarn okio.**
-keepclasseswithmembernames class * {
    native <methods>;
}
-dontwarn android.test.**
-dontwarn com.xhaus.**
-dontwarn org.xmlpull.**
-dontwarn org.fusesource.**
-dontwarn jnr.posix.**
-dontwarn jnr.ffi.provider.**
-dontwarn com.kenai.**
-dontwarn com.ziclix.**
-dontwarn android.util.Xml
-dontwarn org.junit.**
-dontwarn com.fasterxml.**
-dontwarn java8.util.**

-keep class android.support.v7.widget.ShareActionProvider { *; }

#-keepattributes *Annotation*
#-keepclassmembers class ** {@org.greenrobot.eventbus.Suscribe <methods>;}
#-keep enum org.greenrobot.eventbus.ThreadMode{*;}
-keepclassmembers class ** {
    public void onEvent*(**);
    }