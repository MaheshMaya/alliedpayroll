plugins {
    id("alliedpayroll.backend-module")
}

description = "The immutable trail: every change and security event, written in the same transaction, read within scope"

dependencies {
    implementation(project(":modules:identity"))
    implementation(libs.spring.boot.starter.jdbc)
}
