import org.gradle.jvm.toolchain.JavaToolchainService
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
    java
    jacoco
    id("com.github.nbaztec.coveralls-jacoco") version "1.2.14"
    id("io.gitlab.arturbosch.detekt").version("1.23.4")
    id("info.solidsoft.pitest").version("1.7.4")
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"

    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.9.20"
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

group = "com.marabesi"
version = "1.0.0"

configurations {}

val javaToolchains = extensions.getByType<JavaToolchainService>()

val cucumberRuntime by configurations.creating {
    extendsFrom(configurations["testImplementation"])
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("info.picocli:picocli:4.7.6")
    implementation("com.google.inject:guice:5.0.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.commons:commons-csv:1.9.0")
    testCompileOnly("org.junit.jupiter:junit-jupiter-params:5.8.1")
    implementation("org.hamcrest:hamcrest:2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.springframework.social:spring-social-twitter:1.1.0.RELEASE")
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")

    implementation("oauth.signpost:signpost-core:2.0.0")
    implementation("oauth.signpost:signpost-commonshttp4:2.0.0")

    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.wiremock:wiremock:3.13.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    testImplementation("io.cucumber:cucumber-java8:7.0.0")
    testImplementation("io.cucumber:cucumber-junit:7.0.0")
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("acceptance.*")
        excludeTestsMatching("thirdpartyintegration.*")
    }
}

sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTest =
    task<Test>("integrationTest") {
        description = "Runs integration tests."
        group = "verification"

        useJUnitPlatform()
        filter {
            includeTestsMatching("thirdpartyintegration.*")
        }
    }

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    config = files("detek.yml")
    allRules = false // activate all available (even unstable) rules.
    // Do not fail the build for existing issues during the Java 21 upgrade.
    ignoreFailures = true
}

pitest {
    targetClasses.addAll("adapters.*", "application.*")
    targetTests.addAll("unit.*", "integration.*")
    threads.set(1)
    testPlugin.set("junit5")
    junit5PluginVersion.set("0.15")
    useClasspathFile.set(true)
    outputFormats.set(setOf("XML", "HTML"))
    mutators.set(setOf("STRONGER", "DEFAULTS"))
    avoidCallsTo.set(setOf("kotlin.jvm.internal", "kotlinx.coroutines"))
}

task("cucumber") {
    dependsOn("assemble", "compileTestJava")
    doLast {
        javaexec {
            mainClass.set("io.cucumber.core.cli.Main")
            classpath = cucumberRuntime + sourceSets.main.get().output + sourceSets.test.get().output
            // Run with the project's Java toolchain so the launcher matches the compiled classes.
            executable =
                javaToolchains
                    .launcherFor(java.toolchain)
                    .get()
                    .executablePath
                    .asFile
                    .absolutePath
            // Change glue for your project package where the step definitions are.
            // And where the feature files are.
            args = listOf("--plugin", "pretty", "--glue", "acceptance", "src/test/resources")
            // Configure jacoco agent for the test coverage.
            val jacocoAgent =
                zipTree(configurations.jacocoAgent.get().singleFile)
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
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
    manifest {
        attributes()
    }
}

signing {
    val signingKey =
        providers
            .environmentVariable("GPG_SIGNING_KEY")
            .forUseAtConfigurationTime()
    val signingPassphrase =
        providers
            .environmentVariable("GPG_SIGNING_PASSPHRASE")
            .forUseAtConfigurationTime()
    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        val extension =
            extensions
                .getByName("publishing") as PublishingExtension
        sign(extension.publications)
    }
}

object Meta {
    const val DESC = "Social publisher allows you to schedule and publish posts into social media."
    const val LICENSE = "Apache-2.0"
    const val GITHUB_REPO = "marabesi/social-publisher"
    const val RELEASE = "https://s01.oss.sonatype.org/service/local/"
    const val SNAPSHOT = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    const val DEVELOPER_ID = "marabesi"
    const val DEVELOPER_NAME = "Matheus Marabesi"
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set(project.name)
                description.set(Meta.DESC)
                url.set("https://github.com/${Meta.GITHUB_REPO}")
                licenses {
                    license {
                        name.set(Meta.LICENSE)
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                developers {
                    developer {
                        id.set(Meta.DEVELOPER_ID)
                        name.set(Meta.DEVELOPER_NAME)
                    }
                }
                scm {
                    url.set(
                        "https://github.com/${Meta.GITHUB_REPO}.git",
                    )
                    connection.set(
                        "scm:git:git://github.com/${Meta.GITHUB_REPO}.git",
                    )
                    developerConnection.set(
                        "scm:git:git://github.com/${Meta.GITHUB_REPO}.git",
                    )
                }
                issueManagement {
                    url.set("https://github.com/${Meta.GITHUB_REPO}/issues")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri(Meta.RELEASE))
            snapshotRepositoryUrl.set(uri(Meta.SNAPSHOT))
            val ossrhUsername =
                providers
                    .environmentVariable("OSSRH_USERNAME")
                    .forUseAtConfigurationTime()
            val ossrhPassword =
                providers
                    .environmentVariable("OSSRH_PASSWORD")
                    .forUseAtConfigurationTime()
            if (ossrhUsername.isPresent && ossrhPassword.isPresent) {
                username.set(ossrhUsername.get())
                password.set(ossrhPassword.get())
            }
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "21"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "21"
}
