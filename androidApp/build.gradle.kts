plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    sourceSets["main"].java.srcDir("src/main/java")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    packagingOptions {
        resources.excludes += "META-INF/kotlinx_coroutines_core.version"
    }

    compileSdk = 32

    defaultConfig {
        applicationId = "dandelion.mobile.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        val serverUrl: String? by project
        if (serverUrl != null) {
            resValue("string", "server_url", serverUrl!!)
        } else {
            resValue("string", "server_url", "http://10.0.2.2:8080/")
        }

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
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
}

val composeVersion = "1.1.0"

dependencies {

    implementation(project(":stub"))
    implementation(kotlin("stdlib"))
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")
    implementation("androidx.compose.foundation:foundation-layout:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    runtimeOnly("io.grpc:grpc-okhttp:${rootProject.ext["grpcVersion"]}")


}