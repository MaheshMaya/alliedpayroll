plugins {
    id("alliedpayroll.java-conventions")
    alias(libs.plugins.spring.boot)
}

description = "The bootable application: assembles every module, profiles api and batch, one image"

dependencies {
    implementation(project(":platform:common-core"))
    implementation(project(":platform:common-tenancy"))
    implementation(project(":platform:common-security"))
    implementation(project(":platform:common-web"))
    implementation(project(":platform:common-events"))
    implementation(project(":modules:tenant"))
    implementation(project(":modules:identity"))
    implementation(project(":channels:web-channel"))
    implementation(project(":channels:mobile-channel"))

    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.modulith.starter.core)
    implementation(libs.spring.modulith.actuator)
    implementation(libs.spring.modulith.observability)
    implementation(libs.springdoc.openapi)
    runtimeOnly(libs.flyway.postgresql)
    runtimeOnly(libs.postgresql)
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(project(":platform:common-test"))
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.starter.security.test)
    testImplementation(libs.spring.boot.starter.oauth2.resource.server)
    testImplementation(libs.spring.boot.starter.flyway.test)
}

springBoot {
    mainClass = "com.alliedpayroll.BackendApplication"
    buildInfo()
}

tasks.bootJar {
    archiveFileName = "backend.jar"
}
