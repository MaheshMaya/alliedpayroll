plugins {
    id("alliedpayroll.platform-library")
}

description = "TenantContext, request-level tenant resolution, the RLS-setting DataSource and the Hibernate tenant resolver"

dependencies {
    api(project(":platform:common-core"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
}
