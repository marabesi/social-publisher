plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
    id("org.springframework.boot")
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
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation(testFixtures(project(":core")))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    testImplementation("io.mockk:mockk:1.14.11")
}

tasks.test {
    useJUnitPlatform()
}
