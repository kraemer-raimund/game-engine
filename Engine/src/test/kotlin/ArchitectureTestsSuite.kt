import dev.rakrae.gameengine.TestTag
import org.junit.platform.suite.api.IncludeTags
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("Architecture Tests")
@SelectPackages("dev.rakrae.gameengine")
@IncludeTags(TestTag.ARCHITECTURE_TEST)
class ArchitectureTestsSuite
