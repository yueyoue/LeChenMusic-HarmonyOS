# Add project specific ProGuard rules here.
-keep class com.lechen.music.model.** { *; }
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
