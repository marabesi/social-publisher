plugins {
    kotlin("jvm") version "2.4.20" apply false
    kotlin("plugin.serialization") version "2.4.20" apply false
    kotlin("plugin.spring") version "2.4.20" apply false
    kotlin("plugin.compose") version "2.4.20" apply false
    id("org.springframework.boot") version "4.1.1" apply false
    id("org.jetbrains.compose") version "1.12.1" apply false
    id("com.gradleup.shadow") version "9.6.1" apply false
    id("dev.detekt") version "2.0.0-alpha.6" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0" apply false
}

allprojects {
    group = "com.marabesi"
    version = "1.0.0"

    repositories {
        google()
        mavenCentral()
    }
}