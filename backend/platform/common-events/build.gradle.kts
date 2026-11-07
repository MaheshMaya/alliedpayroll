plugins {
    id("alliedpayroll.platform-library")
}

description = "Event metadata, the Modulith event publication registry as outbox, idempotent listener support"

dependencies {
    api(project(":platform:common-core"))
    api(libs.spring.modulith.starter.jdbc)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.modulith.events.jackson)
}
