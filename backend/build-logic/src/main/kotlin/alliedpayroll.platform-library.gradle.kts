plugins {
    id("alliedpayroll.java-conventions")
    `java-library`
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    api(libs.findLibrary("spring-boot-starter").get())
    annotationProcessor(libs.findLibrary("spring-boot-configuration-processor").get())
    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
}
