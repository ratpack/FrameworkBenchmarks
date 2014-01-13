package ratpack.benchmarks.techempower.java

import ratpack.test.RatpackMainApplicationUnderTest
import ratpack.test.ServerBackedApplicationUnderTest

class TechempowerBenchmarksSpec extends ratpack.benchmarks.techempower.test.TechempowerBenchmarkSpec {

  ServerBackedApplicationUnderTest createApplicationUnderTest() {
    new RatpackMainApplicationUnderTest()
  }
}
