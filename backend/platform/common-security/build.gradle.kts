plugins {
    id("alliedpayroll.platform-library")
}

description = "Authenticated principal, permission checks, channel policy and the shared security filter chain"

dependencies {
    api(project(":platform:common-core"))
    api(project(":platform:common-tenancy"))
    api(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.oauth2.client)
    testImplementation(libs.spring.boot.starter.security.test)
}
