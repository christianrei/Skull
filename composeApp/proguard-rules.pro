# Keep your Compose code
-keep class androidx.compose.** { *; }

# Keep your ViewModels (if you're using them)
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Firebase (customize as needed)
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep your DI framework (Koin example)
-keep class org.koin.** { *; }

# Keep Jetpack libraries
-keep class androidx.** { *; }
-dontwarn androidx.**

# Prevent obfuscation of generated Kotlin code
-keep class kotlin.** { *; }
-dontwarn kotlin.**
