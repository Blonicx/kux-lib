import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.blonicx"
version = "1.2.0"

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

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("") // no "-all"
        mergeServiceFiles()
    }

    named<Jar>("jar") {
        enabled = false
    }

    build {
        dependsOn(shadowJar)
    }

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
        description.set("A Minecraft Paper plugin library that simplifies dependency management")
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

// Optional: attach Javadoc & sources to Maven publication
afterEvaluate {
    tasks.named("generateMetadataFileForMavenPublication") {
        dependsOn(tasks.named("plainJavadocJar"))
    }
}