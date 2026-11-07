plugins {
    id("alliedpayroll.platform-library")
}

description = "Typed ids, id generation, domain errors and value objects with no framework meaning"

dependencies {
    api(libs.uuid.creator)
    api(libs.spring.modulith.api)
}
