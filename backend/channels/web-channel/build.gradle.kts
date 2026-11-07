plugins {
    id("alliedpayroll.backend-module")
}

description = "Browser token handler: OIDC Authorization Code + PKCE login, session cookie, CSRF, logout"

dependencies {
    implementation(project(":modules:tenant"))
    implementation(libs.spring.boot.starter.oauth2.client)
}
