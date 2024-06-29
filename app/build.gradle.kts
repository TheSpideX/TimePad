plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.spidex.timepad"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.spidex.timepad"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    implementation(libs.androidx.navigation.compose)
    val roomVersion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // The compose calendar library
    implementation("com.kizitonwose.calendar:compose:2.5.2")

    //Progress Bar
    implementation("com.exyte:animated-navigation-bar:1.0.0")

    //Graph
    implementation ("io.github.ehsannarmani:compose-charts:0.0.6")

    implementation("com.jakewharton.threetenabp:threetenabp:1.4.7")

    //time picker
    implementation ("com.github.commandiron:WheelPickerCompose:1.1.11")

    implementation ("androidx.compose.material3:material3")
    implementation ("androidx.compose.ui:ui-tooling:1.6.8")
    implementation ("androidx.compose.ui:ui")
    implementation ("androidx.compose.material:material-icons-extended")
    implementation ("androidx.compose.material3:material3-window-size-class")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.compose.runtime:runtime-livedata")
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation ("com.airbnb.android:lottie-compose:6.4.1")

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