plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-test-fixtures`
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("oauth.signpost:signpost-core:2.0.0")
    implementation("oauth.signpost:signpost-commonshttp4:2.0.0")
    implementation("aopalliance:aopalliance:1.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("org.wiremock:wiremock:3.13.1")
    testImplementation("org.hamcrest:hamcrest:2.2")

    testFixturesImplementation("org.wiremock:wiremock:3.13.1")
}

tasks.test {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("thirdpartyintegration.*")
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    useJUnitPlatform()
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    filter {
        includeTestsMatching("thirdpartyintegration.*")
    }
}
