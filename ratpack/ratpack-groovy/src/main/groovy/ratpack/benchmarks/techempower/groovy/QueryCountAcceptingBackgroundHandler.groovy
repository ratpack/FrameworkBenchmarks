package ratpack.benchmarks.techempower.groovy

import groovy.transform.CompileStatic
import ratpack.benchmarks.techempower.common.World
import ratpack.handling.Context
import ratpack.handling.Handler

import static ratpack.jackson.Jackson.json

@CompileStatic
class QueryCountAcceptingBackgroundHandler implements Handler {

  QueryCountToWorldTransformer transformer

  QueryCountAcceptingBackgroundHandler(QueryCountToWorldTransformer transformer) {
    this.transformer = transformer
  }

  private int queryCount(String queriesParam) {
    int count = 1;
    try {
      count = Integer.parseInt(queriesParam);
      if (count < 1) {
        count = 1;
      }
      if (count > 500) {
        count = 500;
      }
    } catch (NumberFormatException e) {
      // ignore
    }
    return count;
  }

  @Override
  void handle(Context context) throws Exception {
    def worldService = context.get(WorldService)
    context.blocking {
      transformer.transform(worldService, queryCount(context.request.queryParams.queries))
    } then { World[] worlds->
      context.render(json(worlds))
    }
  }
}
