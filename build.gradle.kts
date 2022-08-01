plugins {
    id("com.google.protobuf") version "0.8.15" apply false
    kotlin("jvm") version "1.5.31" apply false
    id("com.android.application") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
}

ext["grpcVersion"] = "1.46.0"
ext["grpcKotlinVersion"] = "1.3.0" // CURRENT_GRPC_KOTLIN_VERSION
ext["protobufVersion"] = "3.20.1"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}