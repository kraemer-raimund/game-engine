package dev.rakrae.gameengine.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTag
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.GeneralCodingRules
import dev.rakrae.gameengine.TestTag

@AnalyzeClasses(packages = ["dev.rakrae.gameengine"])
@ArchTag(TestTag.ARCHITECTURE_TEST)
class ArchTest {

    @ArchTest
    private val noGenericExceptions: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS

    /**
     * We want to intentionally reinvent some wheels, including creating our
     * own math "library" for this game engine, implementing the math we need
     * when we need it.
     */
    @ArchTest
    val noMathFromStandardLibrary: ArchRule =
        noClasses().that().resideInAPackage("dev.rakrae.gameengine.*")
            .should().dependOnClassesThat().haveNameMatching("java.lang.Math")
}
