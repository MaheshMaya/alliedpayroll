plugins {
    id("com.android.application")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.alliedpayroll.allied_payroll"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        applicationId = "com.alliedpayroll.app"
        // flutter_appauth receives the OIDC redirect on this custom scheme (env/*.json OIDC_REDIRECT_URI).
        manifestPlaceholders["appAuthRedirectScheme"] = "com.alliedpayroll.app"
        // MOBILE_AND_DEVICES.md §1.1: Android 8.0 is the floor for field-staff phones.
        minSdk = 26
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    buildTypes {
        release {
            // Release signing arrives with the first store build; debug keys keep `--release` runnable.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

flutter {
    source = "../.."
}
