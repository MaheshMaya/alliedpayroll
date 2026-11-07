plugins {
    id("alliedpayroll.backend-module")
}

description = "Mobile bootstrap and, later, delta sync and the offline punch queue; shapes data only"

dependencies {
    implementation(project(":modules:tenant"))
}
