plugins {
    id("java")
}

group = "me.wylan.minestom.spigot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:65f75bb059")
    implementation("ch.qos.logback:logback-classic:1.5.7")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}