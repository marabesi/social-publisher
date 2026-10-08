import com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer

plugins {
    kotlin("jvm")
    application
    id("com.gradleup.shadow")
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

application {
    applicationName = "social"
    mainClass.set("MainKt")
}

tasks.named<Zip>("distZip") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.shadowJar {
    archiveBaseName.set("social")
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    mergeServiceFiles()
    mergeServiceFiles {
        path = "META-INF/spring"
    }
    transform(AppendingTransformer::class.java) {
        resource = "META-INF/spring.factories"
    }
}

val cucumberRuntime: Configuration =
    configurations.create("cucumberRuntime") {
        extendsFrom(configurations["testImplementation"])
    }

dependencies {
    implementation(project(":core"))
    implementation(project(":desktop"))
    implementation(project(":rest-api"))
    implementation("info.picocli:picocli:4.7.6")
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation(testFixtures(project(":core")))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("org.wiremock:wiremock:3.13.1")
    testImplementation("io.cucumber:cucumber-java8:7.0.0")
    testImplementation("io.cucumber:cucumber-junit:7.0.0")
}

tasks.test {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("acceptance.*")
    }
}

configurations.configureEach {
    resolutionStrategy.apply {
        force(
            "org.junit.jupiter:junit-jupiter:5.11.4",
            "org.junit.jupiter:junit-jupiter-api:5.11.4",
            "org.junit.jupiter:junit-jupiter-engine:5.11.4",
            "org.junit.jupiter:junit-jupiter-params:5.11.4",
            "org.junit.platform:junit-platform-commons:1.11.4",
            "org.junit.platform:junit-platform-engine:1.11.4",
            "org.junit.platform:junit-platform-launcher:1.11.4",
        )
        eachDependency {
            if (requested.group == "org.eclipse.jetty" && requested.version != "11.0.24") {
                useVersion("11.0.24")
            }
        }
    }
}

tasks.register<JavaExec>("cucumber") {
    description = "Runs the Cucumber acceptance suite."
    group = "verification"
    dependsOn("assemble", "compileTestJava")
    mainClass.set("io.cucumber.core.cli.Main")
    classpath = cucumberRuntime + sourceSets.main.get().output + sourceSets.test.get().output
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    args = listOf("--plugin", "pretty", "--glue", "acceptance", "src/test/resources")

    val jacocoAgent =
        zipTree(configurations.jacocoAgent.get().singleFile)
            .filter { it.name == "jacocoagent.jar" }
            .singleFile
    jvmArgs =
        listOf(
            "-javaagent:$jacocoAgent=destfile=${layout.buildDirectory.get()}/results/jacoco/cucumber.exec,append=false",
        )
}
