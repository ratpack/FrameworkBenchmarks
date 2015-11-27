package ratpack.benchmarks.techempower.java

import ratpack.benchmarks.techempower.test.ApplicationUnderTest
import ratpack.test.ServerBackedApplicationUnderTest

class TechempowerBenchmarkSpec extends ratpack.benchmarks.techempower.test.TechempowerBenchmarkSpec {

  ServerBackedApplicationUnderTest createApplicationUnderTest() {
    new ApplicationUnderTest(Application)
  }
}
