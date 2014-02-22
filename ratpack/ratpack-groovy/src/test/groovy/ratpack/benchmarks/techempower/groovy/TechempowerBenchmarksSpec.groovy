package ratpack.benchmarks.techempower.groovy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.sql.Sql
import ratpack.benchmarks.techempower.common.World
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.test.ServerBackedApplicationUnderTest
import ratpack.test.remote.RemoteControl
import spock.lang.Shared
import spock.lang.Unroll

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

  Map getWorldCountForQueriesMap() {
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

  String getQueriesQueryString(queries) {
    return queries == null ? '' : "?queries=$queries"
  }

  JsonNode parseJson(String json) {
    new ObjectMapper().readTree(json)
  }

  void assertMultiQueryResponseBody(String responseBody, int worldCount) {
    def json = parseJson(responseBody)
    assert json.size() == worldCount
    json.each {
      assert it.size() == 2
      assert it.id.asInt() >= 1 && it.id.asInt() <= DB_ROWS
      assert it.randomNumber.asInt() >= 1 && it.randomNumber.asInt() <= DB_ROWS
    }
  }

  def "single query test type fulfils requirements"() {
    when:
    get("db")

    then:
    def responseBody = response.asString()
    with(parseJson(responseBody)) {
      size() == 2
      id.asInt() >= 1 && id.asInt() <= DB_ROWS
      randomNumber.asInt() >= 1 && randomNumber.asInt() <= DB_ROWS
    }
    assertResponseHeaders(response, 'application/json', responseBody)
  }

  @Unroll
  def "database updates test type fulfils requirements - updating '#queries' queries"() {
    when:
    get("updates$queryString")

    then:
    def responseBody = response.asString()
    assertMultiQueryResponseBody(responseBody, worldCount)
    assertResponseHeaders(response, 'application/json', responseBody)

    where:
    queries << worldCountForQueriesMap.keySet()
    worldCount = worldCountForQueriesMap.get(queries)
    queryString = getQueriesQueryString(queries)
  }

  // TODO: Test also whether the records are actually updated in the DB
  @Unroll
  def "multiple queries test type fulfils requirements - requesting '#queries' queries"() {
    when:
    get("queries$queryString")

    then:
    def responseBody = response.asString()
    assertMultiQueryResponseBody(responseBody, worldCount)
    assertResponseHeaders(response, 'application/json', responseBody)

    where:
    queries << worldCountForQueriesMap.keySet()
    worldCount = worldCountForQueriesMap.get(queries)
    queryString = getQueriesQueryString(queries)
  }
}