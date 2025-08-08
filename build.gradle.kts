import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "com.blonicx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    // Customize the fat jar
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("") // no "-all"
        mergeServiceFiles() // optional but good practice
    }

    // Disable default plain jar
    named<Jar>("jar") {
        enabled = false
    }

    // Ensure build depends on fat jar
    build {
        dependsOn(shadowJar)
    }

    // Configure run-paper plugin
    runServer {
        minecraftVersion("1.21")
    }

    // Replace version placeholders in plugin config
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}