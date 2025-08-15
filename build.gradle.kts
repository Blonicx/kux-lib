import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar

val signingKey: String? = findProperty("signing.key") as String?
val signingPassword: String? = findProperty("signing.password") as String?
val signingKeyId: String? = findProperty("signing.keyId") as String?

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.blonicx"
version = "1.1.0"

java {
    withJavadocJar()
    withSourcesJar()
}

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

mavenPublishing {
    coordinates(group.toString(), "kux-lib", version.toString())

    pom {
        name.set("kux-lib")
        description.set("A Minecraft Paper plugin library that simplifies dependency management ")
        url.set("https://github.com/Blonicx/kux-lib")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit/")
            }
        }
        developers {
            developer {
                id.set("blonicx")
                name.set("Blonicx")
                email.set("")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/Blonicx/kux-lib.git")
            developerConnection.set("scm:git:ssh://github.com/Blonicx/kux-lib.git")
            url.set("https://github.com/Blonicx/kux-lib")
        }
    }
}
