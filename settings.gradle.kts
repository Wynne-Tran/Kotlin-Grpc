pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "KotlinGrpc"
include(":androidApp")
include(":shared")
include(":protos")
include(":stub")
include(":Server")
include(":stub-android")
include(":stub-android")
