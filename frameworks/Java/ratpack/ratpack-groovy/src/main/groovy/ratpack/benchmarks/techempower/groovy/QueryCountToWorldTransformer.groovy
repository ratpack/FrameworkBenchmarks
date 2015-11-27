package ratpack.benchmarks.techempower.groovy

import groovy.transform.CompileStatic
import ratpack.benchmarks.techempower.common.World

@CompileStatic
interface QueryCountToWorldTransformer {
  World[] transform(WorldService worldService, int queryCount)
}
