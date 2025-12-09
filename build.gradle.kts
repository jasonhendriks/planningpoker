plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    kotlin("plugin.serialization") version "2.2.21" // Or your current Kotlin version
}

group = "ca.hendriks"
version = "0.0.1"

application {
    mainClass = "ca.hendriks.planningpoker.Main"
}

sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val intTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val intTestRuntimeOnly by configurations.getting

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}

tasks.check { dependsOn(integrationTest) }

ktor {
    @OptIn(io.ktor.plugin.OpenApiPreview::class)
    openApi {
        title = "OpenAPI example"
        version = "2.1"
        summary = "This is a sample API"
    }
}

// Builds OpenAPI specification automatically
tasks.processResources {
//    dependsOn("buildOpenApi")
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.openapi)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.apache)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.swagger)
    implementation(ktorLibs.server.sse)
    implementation(ktorLibs.server.webjars)
    implementation(ktorLibs.server.htmlBuilder)
    implementation(libs.bundles.htmx)
    implementation(libs.logback.classic)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotest.assertions.core.jvm)
    testImplementation(libs.kotest.framework.datatest.jvm)
    testImplementation(libs.kotest.runner.junit5.jvm)

    intTestImplementation("org.junit.platform:junit-platform-suite")
    intTestImplementation("org.seleniumhq.selenium:selenium-java:4.39.0")
    intTestImplementation(ktorLibs.client.cio)
    intTestImplementation(ktorLibs.client.core)
    intTestImplementation(libs.bundles.cucumber.libs)
    intTestImplementation(libs.kotest.assertions.core.jvm)
    intTestRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
