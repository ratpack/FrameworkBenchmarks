package ratpack.benchmarks.techempower.groovy

import com.google.inject.AbstractModule
import groovy.sql.Sql
import groovy.transform.CompileStatic
import ratpack.groovy.sql.SqlProvider

@CompileStatic
class DataAccessModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Sql).toProvider(SqlProvider)
  }

}
