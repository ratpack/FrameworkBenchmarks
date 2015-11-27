package ratpack.benchmarks.techempower.groovy

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import ratpack.benchmarks.techempower.common.Fortune

import javax.inject.Inject

import static ratpack.benchmarks.techempower.common.Fortune.ADDITIONAL_FORTUNE

@CompileStatic
class FortuneService {

  final Sql sql

  @Inject
  FortuneService(Sql sql) {
    this.sql = sql
  }

  List<Fortune> allPlusOne() {
    List<Fortune> fortunes = sql.rows("select * from Fortune").collect { GroovyRowResult result ->
      new Fortune((int)result.id, (String)result.message)
    }
    fortunes.add(new Fortune(0, ADDITIONAL_FORTUNE))
    Collections.sort(fortunes)
    return fortunes
  }

}
