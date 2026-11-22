pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "alliedpayroll-backend"

include(
    "app",
    "platform:common-core",
    "platform:common-tenancy",
    "platform:common-security",
    "platform:common-web",
    "platform:common-events",
    "platform:common-test",
    "modules:tenant",
    "modules:identity",
    "channels:web-channel",
    "channels:mobile-channel",
)
