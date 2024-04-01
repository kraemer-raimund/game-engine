package dev.rakrae.gameengine.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.GeneralCodingRules

@AnalyzeClasses(packages = ["dev.rakrae.gameengine"])
class GeneralArchTest {

    @ArchTest
    private val noGenericExceptions: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
}
