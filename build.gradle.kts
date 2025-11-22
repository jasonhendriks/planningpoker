plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
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

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)

    testImplementation(libs.ktor.server.test.host)
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.7.2")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.7.2")

    intTestImplementation("io.cucumber:cucumber-java8:7.15.0")
    intTestRuntimeOnly("org.junit.platform:junit-platform-launcher")
    intTestImplementation("io.cucumber:cucumber-junit-platform-engine:7.15.0")
    intTestImplementation("org.junit.platform:junit-platform-suite")
    intTestImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
}
