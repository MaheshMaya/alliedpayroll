plugins {
    id("alliedpayroll.java-conventions")
    `java-library`
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    api(project(":platform:common-core"))
    implementation(project(":platform:common-tenancy"))
    implementation(project(":platform:common-security"))
    implementation(project(":platform:common-web"))
    implementation(project(":platform:common-events"))

    implementation(libs.findLibrary("spring-boot-starter-webmvc").get())
    implementation(libs.findLibrary("spring-boot-starter-validation").get())
    implementation(libs.findLibrary("spring-boot-starter-data-jpa").get())
    implementation(libs.findLibrary("spring-modulith-starter-core").get())
    implementation(libs.findLibrary("springdoc-openapi").get())
    annotationProcessor(libs.findLibrary("spring-boot-configuration-processor").get())

    testImplementation(project(":platform:common-test"))
    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    testImplementation(libs.findLibrary("spring-boot-starter-webmvc-test").get())
    testImplementation(libs.findLibrary("spring-boot-starter-security-test").get())
    testImplementation(libs.findLibrary("spring-boot-starter-data-jpa-test").get())
    testImplementation(libs.findLibrary("spring-modulith-starter-test").get())
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
