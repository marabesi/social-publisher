import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
    java
    jacoco
}

group = "me.marabesi"
version = "1.0-SNAPSHOT"

configurations {}

val cucumberRuntime by configurations.creating {
    extendsFrom(configurations["testImplementation"])
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("info.picocli:picocli:4.6.2")
    implementation("com.google.inject:guice:4.0")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("io.cucumber:cucumber-java8:7.0.0")
    testImplementation("io.cucumber:cucumber-junit:7.0.0")
    implementation("org.apache.commons:commons-csv:1.9.0")
}

tasks.test {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("acceptance.*")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

task("cucumber") {
    dependsOn("assemble", "compileTestJava")
    doLast {
        javaexec {
            mainClass.set("io.cucumber.core.cli.Main")
            classpath = cucumberRuntime + sourceSets.main.get().output + sourceSets.test.get().output
            // Change glue for your project package where the step definitions are.
            // And where the feature files are.
            args = listOf("--plugin", "pretty", "--glue", "acceptance", "src/test/resources")
            // Configure jacoco agent for the test coverage.
            val jacocoAgent = zipTree(configurations.jacocoAgent.get().singleFile)
                .filter { it.name == "jacocoagent.jar" }
                .singleFile
            jvmArgs = listOf("-javaagent:$jacocoAgent=destfile=$buildDir/results/jacoco/cucumber.exec,append=false")
        }
    }
}

tasks.jacocoTestReport {
    // Give jacoco the file generated with the cucumber tests for the coverage.
    executionData(files("$buildDir/jacoco/test.exec", "$buildDir/results/jacoco/cucumber.exec"))
    reports {
        xml.required.set(true)
    }
}

application {
    mainClass.set("MainKt")
}

java {
    manifest {
        attributes()
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}