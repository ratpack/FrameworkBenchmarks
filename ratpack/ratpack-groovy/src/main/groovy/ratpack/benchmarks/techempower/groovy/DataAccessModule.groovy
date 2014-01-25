package ratpack.benchmarks.techempower.groovy

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class DataAccessModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(WorldService.class).in(Scopes.SINGLETON)
  }

}
