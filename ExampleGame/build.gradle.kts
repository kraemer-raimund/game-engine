plugins {
    kotlin("jvm") version "1.9.23"
}

group = "me.kraem"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    project(":MyKotlinGameEngine")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
