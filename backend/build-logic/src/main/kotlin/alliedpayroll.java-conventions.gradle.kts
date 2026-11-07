import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway

plugins {
    java
    checkstyle
    jacoco
    id("com.diffplug.spotless")
    id("net.ltgt.errorprone")
    id("net.ltgt.nullaway")
}

val libs = the<VersionCatalogsExtension>().named("libs")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.findVersion("java").get().requiredVersion)
    }
}

group = "com.alliedpayroll"

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-bom").get()))
    implementation(platform(libs.findLibrary("spring-modulith-bom").get()))
    annotationProcessor(platform(libs.findLibrary("spring-boot-bom").get()))
    testImplementation(platform(libs.findLibrary("testcontainers-bom").get()))
    implementation(libs.findLibrary("jspecify").get())
    errorprone(libs.findLibrary("error-prone-core").get())
    errorprone(libs.findLibrary("nullaway").get())
}

nullaway {
    annotatedPackages.add("com.alliedpayroll")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release =
        libs
            .findVersion("java")
            .get()
            .requiredVersion
            .toInt()
    options.compilerArgs.addAll(listOf("-parameters", "-Xlint:all,-processing,-serial", "-Werror"))
    options.errorprone {
        disableWarningsInGeneratedCode = true
        excludedPaths = ".*/build/generated/.*"
        error("NullAway")
        nullaway {
            jspecifyMode = true
            checkOptionalEmptiness = true
            // Spring and JPA instantiate through reflection; fields they set are not nullable in use.
            excludedFieldAnnotations.addAll(
                "org.springframework.beans.factory.annotation.Autowired",
                "jakarta.persistence.Id",
                "jakarta.persistence.Column",
                "jakarta.persistence.Version",
                "jakarta.persistence.JoinColumn",
                "org.hibernate.annotations.TenantId",
            )
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
    systemProperty("junit.jupiter.execution.parallel.enabled", "false")
    maxHeapSize = "1g"
    failFast = true
}

spotless {
    lineEndings = com.diffplug.spotless.LineEnding.UNIX
    java {
        target("src/**/*.java")
        googleJavaFormat(libs.findVersion("google-java-format").get().requiredVersion)
        removeUnusedImports()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

checkstyle {
    toolVersion = libs.findVersion("checkstyle").get().requiredVersion
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    maxWarnings = 0
    isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = false
        html.required = true
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}
