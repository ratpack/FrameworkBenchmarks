package ratpack.benchmarks.techempower.groovy

import ratpack.benchmarks.techempower.common.World

interface QueryCountToWorldTransformer {
  World[] transform(WorldService worldService, int queryCount)
}
