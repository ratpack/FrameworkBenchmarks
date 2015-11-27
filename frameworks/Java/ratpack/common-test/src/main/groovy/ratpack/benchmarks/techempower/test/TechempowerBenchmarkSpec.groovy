package ratpack.benchmarks.techempower.test

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.sql.Sql
import io.netty.handler.codec.http.HttpHeaders
import ratpack.benchmarks.techempower.common.World
import ratpack.http.client.ReceivedResponse
import ratpack.test.ServerBackedApplicationUnderTest
import ratpack.test.http.TestHttpClient
import ratpack.test.remote.RemoteControl
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.sql.DataSource
import java.text.SimpleDateFormat

import static com.google.common.html.HtmlEscapers.htmlEscaper
import static ratpack.benchmarks.techempower.common.Fortune.ADDITIONAL_FORTUNE
import static ratpack.benchmarks.techempower.common.ResponseData.*
import static ratpack.benchmarks.techempower.common.World.DB_ROWS

abstract class TechempowerBenchmarkSpec extends Specification {

  private final static DATE_FORMAT = 'EEE, dd MMM yyyy HH:mm:ss z'

  @AutoCleanup
  @Shared
  ServerBackedApplicationUnderTest aut = createApplicationUnderTest()

  @Delegate
  TestHttpClient client = aut.httpClient

  @Shared
  RemoteControl remote = new RemoteControl(aut)

  abstract ServerBackedApplicationUnderTest createApplicationUnderTest()

  // See https://github.com/TechEmpower/FrameworkBenchmarks/blob/master/config/create.sql#L48
  final static SAMPLE_FORTUNES = [
    'fortune: No such file or directory',
    "A computer scientist is someone who fixes things that aren''t broken.",
    'After enough decimal places, nobody gives a damn.',
    'A bad random number generator: 1, 1, 1, 1, 1, 4.33e+67, 1, 1, 1',
    'A computer program does what you tell it to do, not what you want it to do.',
    'Emacs is a nice operating system, but I prefer UNIX. — Tom Christaensen',
    'Any program that runs right is obsolete.',
    'A list is only as strong as its weakest link. — Donald Knuth',
    'Feature: A bug with seniority.',
    'Computers make very fast, very accurate mistakes.',
    '<script>alert("This should not be displayed in a browser alert box.");</script>',
    'フレームワークのベンチマーク'
  ]

  def setupSpec() {
    remote.exec {
      Sql sql = new Sql(get(DataSource))

      // Setup World table
      sql.executeInsert("create table if not exists World (id int primary key auto_increment, randomNumber int)")
      sql.withBatch(5000) { stmt ->
        DB_ROWS.times {
          stmt.addBatch("insert into World (randomNumber) values (${World.randomId()})")
        }
      }

      // Setup Fortune table
      sql.executeInsert("create table if not exists Fortune (id int primary key auto_increment, message varchar(2048))")
      SAMPLE_FORTUNES.each {
        sql.executeInsert("insert into Fortune (message) values ($it)")
      }
    }
  }

  def cleanupSpec() {
    remote.exec {
      Sql sql = new Sql(get(DataSource))
      sql.execute("drop table World")
      sql.execute("drop table Fortune")

    }
  }

  JsonNode parseJson(String json) {
    new ObjectMapper().readTree(json)
  }

  def "json test type fulfills requirements"() {
    when:
    get("json")

    then:
    def responseBody = response.body.text
    responseBody == """{"${MESSAGE_KEY}":"${MESSAGE_VALUE}"}"""
    assertResponseHeaders(response, 'application/json', responseBody)
  }

  def "plaintext test type fulfills requirements"() {
    when:
    get("plaintext")

    then:
    def responseBody = response.body.text
    responseBody == MESSAGE_VALUE
    assertResponseHeaders(response, 'text/plain', responseBody)
  }

  def "single query test type fulfils requirements"() {
    when:
    get("db")

    then:
    def responseBody = response.body.text
    with(parseJson(responseBody)) {
      size() == 2
      id.asInt() >= 1 && id.asInt() <= DB_ROWS
      randomNumber.asInt() >= 1 && randomNumber.asInt() <= DB_ROWS
    }
    assertResponseHeaders(response, 'application/json', responseBody)
  }

  @Unroll
  def "multiple queries test type fulfils requirements - requesting '#queries' queries"() {
    when:
    get("queries$queryString")

    then:
    def responseBody = response.body.text
    assertMultiQueryResponseBody(responseBody, worldCount)
    assertResponseHeaders(response, 'application/json', responseBody)

    where:
    queries << worldCountForQueriesMap.keySet()
    worldCount = worldCountForQueriesMap.get(queries)
    queryString = getQueriesQueryString(queries)
  }

  @Unroll
  def "database updates test type fulfils requirements - updating '#queries' queries"() {
    when:
    get("updates$queryString")

    then:
    def responseBody = response.body.text
    assertMultiQueryResponseBody(responseBody, worldCount)
    assertResponseHeaders(response, 'application/json', responseBody)
    assertUpdatesPerformed(responseBody)

    where:
    queries << worldCountForQueriesMap.keySet()
    worldCount = worldCountForQueriesMap.get(queries)
    queryString = getQueriesQueryString(queries)
  }

  def "fortunes test type fulfils requirements"() {
    when:
    get("fortunes")

    then:
    def responseBody = response.body.text
    assertFortunesResponseBody(responseBody)
    assertResponseHeaders(response, 'text/html', responseBody)
  }

  void assertFortunesResponseBody(String responseBody) {
    def html = new XmlSlurper(false, false, true).parseText(responseBody)
    def tableDataRows = html.body.table.tr.findAll{ !it.td.isEmpty() }
    SAMPLE_FORTUNES << ADDITIONAL_FORTUNE
    assert tableDataRows.size() == SAMPLE_FORTUNES.size()
    SAMPLE_FORTUNES.sort().eachWithIndex { fortune, i ->
      assert tableDataRows[i].td[1].text() == fortune
      // Escaping needs to be tested separately on the original
      // responseBody since XmlSlurper unescapes the parsed text
      assert responseBody.contains(htmlEscaper().escape(fortune))
    }
  }
  String getQueriesQueryString(queries) {
    return queries == null ? '' : "?queries=$queries"
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

  void assertMultiQueryResponseBody(String responseBody, int worldCount) {
    def json = parseJson(responseBody)
    assert json.size() == worldCount
    json.each {
      assert it.size() == 2
      assert it.id.asInt() >= 1 && it.id.asInt() <= DB_ROWS
      assert it.randomNumber.asInt() >= 1 && it.randomNumber.asInt() <= DB_ROWS
    }
  }

  void assertResponseHeaders(ReceivedResponse response, String expectedContentType, String responseText) {
    assert response.getBody().contentType.type == expectedContentType
    assert response.headers.get(HttpHeaders.Names.CONTENT_LENGTH) == responseText.bytes.length.toString()
    assert response.headers.get(HttpHeaders.Names.SERVER) == SERVER_NAME.toString()
    def headerDate = new SimpleDateFormat(DATE_FORMAT).parse(response.headers.get(HttpHeaders.Names.DATE))
    assert headerDate <= new Date()
  }

  void assertUpdatesPerformed(String responseBody) {
    //map updated worlds to check for the last randomNumber if there have been any id clashes
    def worlds = parseJson(responseBody).collectEntries { [it.id.asInt(), it.randomNumber.asInt()] }
    def randomNumbers = remote.exec {
      def sql = new Sql(get(DataSource))
      //I don't seem to be able to find a way to select multiple items using a list of ids...
      worlds.keySet().collect {
        sql.firstRow("select randomNumber from World where id = $it").randomNumber
      }
    }
    assert randomNumbers == worlds.values().toList()
  }
}
