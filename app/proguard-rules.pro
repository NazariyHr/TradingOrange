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

# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature

# To support retracing of your application's stack traces, you should ensure the build
# retains sufficient information to retrace with by adding the following rules
-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile

-dontwarn okhttp3.internal.platform.**
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
-keep class retrofit2.* {*;}

-keep @kotlinx.parcelize.Parcelize class *
-keepclassmembers class * {
    @kotlinx.parcelize.Parcelize *;
}

-keep public class io.appwrite.** {*;}

-keep public class p3.internal.connection.* {*;}

-keep public class com.trading.orange.presentation.navigation.** {*;}

-keep public class com.trading.orange.domain.model.** {*;}

-keep public class com.trading.orange.data.server.dto.** {*;}

-keep public class com.trading.orange.data.local_db.entity.** {*;}

-keep public class com.trading.orange.data.local_db.dao.** {*;}

-keep public class com.trading.orange.data.local_assets.dto.** {*;}
