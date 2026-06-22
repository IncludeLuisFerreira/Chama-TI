import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Carrega chaves do secrets.properties (ignorado pelo git)
val secretsFile = rootProject.file("secrets.properties")
val secrets = Properties().apply {
    if (secretsFile.exists()) {
        load(secretsFile.inputStream())
    }
}

android {
    namespace = "com.example.chamati"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chamati"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "PARSE_APPLICATION_ID",
            "\"${secrets.getProperty("PARSE_APPLICATION_ID", "SEU_APPLICATION_ID")}\"")
        buildConfigField("String", "PARSE_CLIENT_KEY",
            "\"${secrets.getProperty("PARSE_CLIENT_KEY", "SEU_CLIENT_KEY")}\"")
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.parse.android) {
        exclude(group = "com.android.support")
    }

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
