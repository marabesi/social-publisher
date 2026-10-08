plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("dev.detekt")
    id("org.jlleitschuh.gradle.ktlint")
    jacoco
}

kotlin {
    jvmToolchain(25)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("../detek.yml"))
    allRules = false
    ignoreFailures = true
}

dependencies {
    implementation(project(":core"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    testImplementation(kotlin("test"))
    testImplementation(testFixtures(project(":core")))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

compose.desktop {
    application {
        mainClass = "desktop.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
