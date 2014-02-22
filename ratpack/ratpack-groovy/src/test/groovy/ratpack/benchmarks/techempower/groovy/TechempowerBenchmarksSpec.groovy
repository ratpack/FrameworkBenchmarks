package ratpack.benchmarks.techempower.groovy

import com.jayway.restassured.response.Response
import groovy.sql.Sql
import ratpack.benchmarks.techempower.common.World
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.test.ServerBackedApplicationUnderTest
import ratpack.test.remote.RemoteControl
import spock.lang.Shared

import static ratpack.benchmarks.techempower.common.World.DB_ROWS

class TechempowerBenchmarksSpec extends ratpack.benchmarks.techempower.test.TechempowerBenchmarkSpec {

  @Shared RemoteControl remote = new RemoteControl(aut)

  ServerBackedApplicationUnderTest createApplicationUnderTest() {
    new LocalScriptApplicationUnderTest(
      'other.remoteControl.enabled': 'true',
      'other.hikari.dataSourceClassName': 'org.h2.jdbcx.JdbcDataSource',
      'other.hikari.dataSourceProperties.URL': 'jdbc:h2:mem:dev'
    )
  }

  def setupSpec() {
    remote.exec {
      Sql sql = get(Sql)
      sql.executeInsert("create table if not exists World (id int primary key auto_increment, randomNumber int)")
      sql.withBatch(5000) { stmt ->
        DB_ROWS.times {
          stmt.addBatch("insert into World (randomNumber) values (${World.randomId()})")
        }
      }
    }
  }

  def cleanupSpec() {
    remote.exec {
      get(Sql).execute("drop table World")
    }
  }

  def "single query test type fulfils requirements"() {
    when:
    get("db")

    then:
    def responseBody = response.asString()
    def responseClone = cloneResponse(response, responseBody)
    with(responseClone.jsonPath()) {
      getMap("").size() == 2
      getInt("id") >= 1 && getInt("id") <= DB_ROWS
      getInt("randomNumber") >= 1 && getInt("randomNumber") <= DB_ROWS
    }
    assertResponseHeaders(responseClone, 'application/json')
  }

  def "multiple queries test type fulfils requirements"() {
    when:
    get("queries$queryString")

    then:
    def responseBody = response.asString()
    def responseClone = cloneResponse(response, responseBody)
    assertMultiQueryResponseBody(responseClone, worldCount)
    assertResponseHeaders(responseClone, 'application/json')

    where:
    queries << worldCountForQueriesMap().keySet()
    worldCount = worldCountForQueriesMap().get(queries)
    queryString = getQueryString(queries)
  }

  // TODO: Test also whether the records are actually updated in the DB
  def "database updates test type fulfils requirements"() {
    when:
    get("updates$queryString")

    then:
    def responseBody = response.asString()
    def responseClone = cloneResponse(response, responseBody)
    assertMultiQueryResponseBody(responseClone, worldCount)
    assertResponseHeaders(responseClone, 'application/json')

    where:
    queries << worldCountForQueriesMap().keySet()
    worldCount = worldCountForQueriesMap().get(queries)
    queryString = getQueryString(queries)
  }

  void assertMultiQueryResponseBody(Response response, int worldCount) {
    with(response.jsonPath()) {
      assert getList("").size() == worldCount
      getList("").each {
        assert it.size() == 2
        assert it.id >= 1 && it.id <= DB_ROWS
        assert it.randomNumber >= 1 && it.randomNumber <= DB_ROWS
      }
    }
  }

  Map worldCountForQueriesMap() {
    return [
      null  : 1,
      ''    : 1,
      'foo' : 1,
      0     : 1,
      (-1)  : 1,
      500   : 500,
      501   : 500,
      1     : 1,
      5     : 5,
      10    : 10,
      15    : 15,
      20    : 20
    ]
  }

  String getQueryString(queries) {
    return queries == null ? '' : "?queries=$queries"
  }
}