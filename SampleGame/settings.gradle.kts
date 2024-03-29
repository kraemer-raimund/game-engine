plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "SampleGame"
include(":GameEngine")
project(":GameEngine").projectDir = File("../Engine")
