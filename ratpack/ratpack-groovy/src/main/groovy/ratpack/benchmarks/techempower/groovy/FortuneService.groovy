package ratpack.benchmarks.techempower.groovy

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import ratpack.benchmarks.techempower.common.Fortune

import javax.inject.Inject

import static ratpack.benchmarks.techempower.common.Fortune.ADDITIONAL_FORTUNE

class FortuneService {

  final Sql sql

  @Inject
  FortuneService(Sql sql) {
    this.sql = sql
  }

  List<Fortune> allPlusOne() {
    List<Fortune> fortunes
    sql.cacheStatements {
      fortunes = sql.rows("select * from Fortune").collect { GroovyRowResult result ->
        new Fortune(result.id, result.message)
      }
    }
    fortunes.add(new Fortune(0, ADDITIONAL_FORTUNE))
    Collections.sort(fortunes)
    return fortunes
  }

}
