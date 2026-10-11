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
    // Bundle the native skiko runtime for every platform so the distribution
    // jar (social.jar) can launch the desktop app on any OS/architecture.
    implementation(compose.desktop.linux_x64)
    implementation(compose.desktop.linux_arm64)
    implementation(compose.desktop.macos_arm64)
    implementation(compose.desktop.windows_x64)
    implementation(compose.material3)
    implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")

    testImplementation(kotlin("test"))
    testImplementation(testFixtures(project(":core")))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    testImplementation(compose.desktop.uiTestJUnit4)
}

tasks.matching { it.name == "run" }.configureEach {
    if (this is JavaExec) {
        workingDir = rootProject.projectDir
    }
}

compose.desktop {
    application {
        mainClass = "desktop.MainKt"
    }
}

// Where the docs read their images from: VitePress serves everything under
// docs/public/ from the site root, so the files end up at /screenshots/*.png.
val docsScreenshotsDir = rootProject.layout.projectDirectory.dir("docs/public/screenshots")

// The app icon is bundled with the desktop app, so it lives in its resources.
val desktopResourcesDir = layout.projectDirectory.dir("src/main/resources")

// Renders the desktop app screens and writes them to docs/public/screenshots.
// Reproducible: static fixture, fixed window size and a single command.
val generateScreenshots =
    tasks.register<Test>("generateScreenshots") {
        val testSourceSet = sourceSets.test.get()
        group = "documentation"
        description = "Renders the desktop app screens to docs/public/screenshots."
        testClassesDirs = testSourceSet.output.classesDirs
        classpath = testSourceSet.runtimeClasspath
        useJUnitPlatform()
        filter {
            includeTestsMatching("desktop.ScreenshotGalleryTest")
        }
        systemProperty("screenshots.dir", docsScreenshotsDir.asFile.absolutePath)
        outputs.upToDateWhen { false }
    }

// Renders the desktop app icon and writes it to desktop/src/main/resources.
// Reproducible: the icon is drawn from Compose primitives, not hand-authored.
val generateIcon =
    tasks.register<Test>("generateIcon") {
        val testSourceSet = sourceSets.test.get()
        group = "documentation"
        description = "Renders the desktop app icon to desktop/src/main/resources."
        testClassesDirs = testSourceSet.output.classesDirs
        classpath = testSourceSet.runtimeClasspath
        useJUnitPlatform()
        filter {
            includeTestsMatching("desktop.AppIconGeneratorTest")
        }
        systemProperty("icon.dir", desktopResourcesDir.asFile.absolutePath)
        outputs.upToDateWhen { false }
    }

tasks.test {
    useJUnitPlatform()
    // The gallery and the icon generator are documentation/build tooling: run
    // them through :desktop:generateScreenshots and :desktop:generateIcon.
    filter {
        excludeTestsMatching("desktop.ScreenshotGalleryTest")
        excludeTestsMatching("desktop.AppIconGeneratorTest")
    }
}
