plugins {
    id("alliedpayroll.platform-library")
}

description = "Testcontainers PostgreSQL with the real roles, tenant test helpers and the shared ArchUnit rules"

dependencies {
    api(project(":platform:common-core"))
    api(project(":platform:common-tenancy"))
    api(platform(libs.testcontainers.bom))
    api(libs.spring.boot.starter.test)
    api(libs.spring.boot.testcontainers)
    api(libs.testcontainers.postgresql)
    api(libs.testcontainers.junit)
    api(libs.archunit.junit5)
    api(libs.spring.modulith.starter.test)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
}

// The role bootstrap is one file shared with docker-compose; tests load it from the classpath.
tasks.processResources {
    from(rootProject.file("db/init")) {
        into("db/init")
    }
}
