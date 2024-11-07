    plugins {
        alias(libs.plugins.android.application)
    }

    android {
        namespace = "com.example.itsav_conect"
        compileSdk = 34

        defaultConfig {
            applicationId = "com.example.itsav_conect"
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
    }

    dependencies {

        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)

        implementation("com.android.volley:volley:1.2.1")
        implementation ("androidx.appcompat:appcompat:1.3.0")
        implementation ("com.google.android.material:material:1.8.0")
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.squareup.okhttp3:okhttp:4.9.0")
        implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
        implementation ("androidx.recyclerview:recyclerview:1.2.1")
        implementation ("com.github.bumptech.glide:glide:4.12.0")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
        implementation ("com.google.android.exoplayer:exoplayer:2.18.1") // Reemplaza con la última versión


    }