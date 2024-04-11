plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.cristiano.ongviverfeliz"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cristiano.ongviverfeliz"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    //Firebase analytics
    implementation("com.google.firebase:firebase-analytics")
    //Autenticação
    implementation("com.google.firebase:firebase-auth-ktx")
    //Cloud storage para armazenar
    implementation("com.google.firebase:firebase-storage-ktx")
    //Banco de dados firestore
    implementation("com.google.firebase:firebase-firestore-ktx")
    //Dependencias firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}