import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "me.mking.currencywatch"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.ui)
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(Google.Android.material)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.material)
    implementation(Square.retrofit2)
    implementation(Square.okHttp3)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.lifecycle.runtimeKtx)
    implementation(AndroidX.activity.compose)
    testImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.compose.ui.testJunit4)
    debugImplementation(AndroidX.compose.ui.tooling)
    implementation(Google.Dagger.Hilt.android)
    kapt(Google.Dagger.Hilt.compiler)
    implementation(KotlinX.serialization.json)
    implementation(JakeWharton.retrofit2.converter.kotlinxSerialization)
    implementation(Square.okHttp3.loggingInterceptor)

    implementation(AndroidX.room.runtime)
    annotationProcessor(AndroidX.room.compiler)
    kapt(AndroidX.room.compiler)
    implementation(AndroidX.room.ktx)

}

kapt {
    correctErrorTypes = true
}