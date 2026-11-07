plugins {
    base
    alias(libs.plugins.spotless)
}

description = "Allied Payroll backend: one Spring Boot application made of Modulith modules"

spotless {
    lineEndings = com.diffplug.spotless.LineEnding.UNIX
    kotlinGradle {
        target("*.gradle.kts", "build-logic/**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint()
    }
}
