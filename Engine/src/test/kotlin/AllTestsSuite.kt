import org.junit.platform.suite.api.IncludePackages
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("All Tests")
@SelectPackages("dev.rakrae.gameengine")
@IncludePackages("dev.rakrae.gameengine")
class AllTestsSuite
