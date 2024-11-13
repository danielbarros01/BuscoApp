import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.practica.buscov2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.practica.buscov2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //Para obtener el gpcId para login con Google
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "GOOGLE_OAUTH_ID", properties.getProperty("GOOGLE_AUTH_GPC_ID"))
        buildConfigField("String", "API_KEY_MAPS", properties.getProperty("API_KEY_MAPS"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
//  Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.46.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation(libs.androidx.room.ktx)

    kapt("com.google.dagger:hilt-compiler:2.46.1")

    //Navigation
    val navVersion = "2.6.0"
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Datastore preferences
    val dataStoreVersion = "1.0.0"
    implementation("androidx.datastore:datastore-preferences:$dataStoreVersion")

    //accompanist
    //-- OnBoarding
    implementation("com.google.accompanist:accompanist-pager:0.15.0")

    implementation("androidx.compose.material3:material3:1.3.0-beta01")

    //auth google
    implementation("com.google.android.gms:play-services-auth:20.3.0")

    //Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    //Permisos
    implementation("com.google.accompanist:accompanist-permissions:0.35.1-alpha")


    //Camara
    // CameraX core library using the camera2 implementation
    val cameraxVersion = "1.4.0-beta02"
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")

    //paging
    val pagingVersion = "3.3.0"
    implementation("androidx.paging:paging-runtime:$pagingVersion")
    implementation("androidx.paging:paging-compose:$pagingVersion")

    implementation("com.microsoft.signalr:signalr:8.0.7")

    val workVersion = "2.9.0"
    implementation("androidx.work:work-runtime:$workVersion")

    //Maps
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation(libs.play.services.maps)

    //Places
    runtimeOnly(libs.places)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}