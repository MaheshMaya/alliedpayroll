plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(plugin(libs.plugins.spring.boot))
    implementation(plugin(libs.plugins.spotless))
    implementation(plugin(libs.plugins.errorprone))
    implementation(plugin(libs.plugins.nullaway))
}

// Precompiled script plugins need the plugin artifact, not the marker, on the classpath.
fun plugin(provider: Provider<PluginDependency>): Provider<String> =
    provider.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
