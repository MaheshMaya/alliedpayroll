plugins {
    id("alliedpayroll.backend-module")
}

description = "Users, roles and the permission catalogue of a tenant; who may do what, from which channel"

dependencies {
    implementation(project(":modules:tenant"))
}
