plugins {
    kotlin("jvm") version "1.5.31"
}

group = "io.github.dolphin2410"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.github.monun:kommand-api:2.6.6")
    compileOnly("io.github.teamcheeze:plum:0.0.13")
}

tasks {
    jar {
        archiveFileName.set(File(project.rootDir, "server/plugins/musical.jar").toString())
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}