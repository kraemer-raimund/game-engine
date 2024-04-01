package dev.rakrae.gameengine.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

@AnalyzeClasses(packages = ["dev.rakrae.gameengine.math"])
class MathArchTest {

    /**
     * We want to intentionally reinvent some wheels, including creating our
     * own math "library" for this game engine, implementing the math we need
     * when we need it.
     */
    @ArchTest
    val mathShouldNotDependOnStandardLibraryMath: ArchRule =
        noClasses().that().resideInAPackage("dev.rakrae.gameengine.math")
            .should().dependOnClassesThat().haveNameMatching("java.lang.Math")
}
