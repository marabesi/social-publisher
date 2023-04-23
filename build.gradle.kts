import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    application
    java
    jacoco
    id("com.github.nbaztec.coveralls-jacoco") version "1.2.14"
    id("io.gitlab.arturbosch.detekt").version("1.21.0")
    id("info.solidsoft.pitest").version("1.7.4")
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"

    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.4.20"
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

group = "com.marabesi"
version = "1.0.0"

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
    implementation("org.apache.commons:commons-csv:1.9.0")
    testCompileOnly("org.junit.jupiter:junit-jupiter-params:5.8.1")
    implementation("org.hamcrest:hamcrest:2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.springframework.social:spring-social-twitter:1.1.0.RELEASE")
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")

    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    testImplementation("io.cucumber:cucumber-java8:7.0.0")
    testImplementation("io.cucumber:cucumber-junit:7.0.0")
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

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    useJUnitPlatform()
    filter {
        includeTestsMatching("thirdpartyintegration.*")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    config = files("detek.yml")
    allRules = false // activate all available (even unstable) rules.
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
    withSourcesJar()
    withJavadocJar()
    manifest {
        attributes()
    }
}

signing {
    val signingKey = providers
        .environmentVariable("GPG_SIGNING_KEY")
        .forUseAtConfigurationTime()
    val signingPassphrase = providers
        .environmentVariable("GPG_SIGNING_PASSPHRASE")
        .forUseAtConfigurationTime()
    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        val extension = extensions
            .getByName("publishing") as PublishingExtension
        sign(extension.publications)
    }
}
object Meta {
    const val desc = "Social publisher allows you to schedule and publish posts into social media."
    const val license = "Apache-2.0"
    const val githubRepo = "marabesi/social-publisher"
    const val release = "https://s01.oss.sonatype.org/service/local/"
    const val snapshot = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    const val developerId = "marabesi"
    const val developerName = "Matheus Marabesi"
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
                description.set(Meta.desc)
                url.set("https://github.com/${Meta.githubRepo}")
                licenses {
                    license {
                        name.set(Meta.license)
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                developers {
                    developer {
                        id.set(Meta.developerId)
                        name.set(Meta.developerName)
                    }
                }
                scm {
                    url.set(
                        "https://github.com/${Meta.githubRepo}.git"
                    )
                    connection.set(
                        "scm:git:git://github.com/${Meta.githubRepo}.git"
                    )
                    developerConnection.set(
                        "scm:git:git://github.com/${Meta.githubRepo}.git"
                    )
                }
                issueManagement {
                    url.set("https://github.com/${Meta.githubRepo}/issues")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri(Meta.release))
            snapshotRepositoryUrl.set(uri(Meta.snapshot))
            val ossrhUsername = providers
                .environmentVariable("OSSRH_USERNAME")
                .forUseAtConfigurationTime()
            val ossrhPassword = providers
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
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
