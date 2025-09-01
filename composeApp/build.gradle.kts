import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
//    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.google.gms.google-services")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    val xcFramework = XCFramework()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            binaryOptions["bundleId"] = "com.dimmaranch.skull"
            linkerOpts(
                "-framework", "GoogleMobileAds",
                "-F${projectDir}/../iOSApp/Frameworks"
            )
            freeCompilerArgs += listOf("-Xobjc-generics")
            isStatic = true
            xcFramework.add(this)
        }
        target.compilations["main"].cinterops {
            val googlemobileads by creating {
                defFile(project.file("src/nativeInterop/cinterop/googlemobileads.def"))
            }
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.driver.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.core)
            implementation(libs.play.services.ads)
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(libs.kotlinx.datetime)
            implementation(libs.coil.compose)
            implementation(libs.accompanist.navigationAnimation)
            implementation(libs.hyperdrive.multiplatformx.api)
            implementation(libs.bundles.androidx.compose)
            implementation(libs.androidx.material3.android)

            // Can these 2 be in commonMain?
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.components.resources)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.voyager.navigator)
            implementation(libs.lifecycle)
            api(libs.kermit)
            api(libs.kermit.crashlytics)
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            api(libs.multiplatformSettings.core)
            api(libs.uuid)

            implementation(libs.bundles.ktor.common)
            implementation(libs.bundles.sqldelight.common)

            implementation(libs.stately.common)
            implementation(libs.koin.core)
            implementation(libs.korio)
            implementation(libs.ui.graphics) // Required for images
            implementation(libs.gitlive.firebase.database)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            implementation(libs.firebase.auth)
            implementation(libs.kotlin.logging)
            implementation(libs.multiplatformSettings.core)
            implementation(libs.kamel.image)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.multiplatform.settings.no.arg)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.driver.ios)
            implementation(libs.sqliter)
            implementation(libs.ktor.client.ios)
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.dimmaranch.skull"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dimmaranch.skull"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

