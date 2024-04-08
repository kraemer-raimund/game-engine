import dev.rakrae.gameengine.TestTag
import org.junit.platform.suite.api.ExcludeTags
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("Unit Tests")
@SelectPackages("dev.rakrae.gameengine")
@ExcludeTags(TestTag.INTEGRATION_TEST, TestTag.ARCHITECTURE_TEST)
class UnitTestsSuite
