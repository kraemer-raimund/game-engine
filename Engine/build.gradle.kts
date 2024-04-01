plugins {
    kotlin("jvm") version "1.9.23"
}

group = "dev.rakrae.gameengine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testCompileOnly("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.2.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}
