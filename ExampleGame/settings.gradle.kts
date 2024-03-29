plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "ExampleGame"
include(":MyKotlinGameEngine")
project(":MyKotlinGameEngine").projectDir = File("../MyKotlinGameEngine")
