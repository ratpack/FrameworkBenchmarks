package ratpack.benchmarks.techempower.groovy

import com.google.inject.AbstractModule
import groovy.sql.Sql
import ratpack.groovy.sql.SqlProvider

class DataAccessModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Sql).toProvider(SqlProvider)
  }

}
