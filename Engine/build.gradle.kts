plugins {
    kotlin("jvm") version "1.9.23"
}

group = "dev.rakrae.gameengine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.10.2"
    val junitPlatformVersion = "1.10.2"
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-suite:$junitPlatformVersion")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.2.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(20)
}
