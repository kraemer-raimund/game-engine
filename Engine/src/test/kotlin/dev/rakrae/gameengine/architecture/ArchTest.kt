package dev.rakrae.gameengine.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTag
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.GeneralCodingRules
import dev.rakrae.gameengine.TestTag

@AnalyzeClasses(packages = ["dev.rakrae.gameengine"])
@ArchTag(TestTag.ARCHITECTURE_TEST)
class ArchTest {

    @ArchTest
    private val noGenericExceptions: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
}
