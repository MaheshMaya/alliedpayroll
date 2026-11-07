plugins {
    id("alliedpayroll.platform-library")
}

description = "problem+json error handling, API version constants, list and page contracts"

dependencies {
    api(project(":platform:common-core"))
    implementation(project(":platform:common-tenancy"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi)
}
