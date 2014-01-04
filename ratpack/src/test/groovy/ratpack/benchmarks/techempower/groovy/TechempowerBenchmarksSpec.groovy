package ratpack.benchmarks.techempower.groovy

import io.netty.handler.codec.http.HttpHeaders
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import ratpack.groovy.test.TestHttpClients
import spock.lang.Specification

import java.text.SimpleDateFormat

/**
 * See http://www.techempower.com/benchmarks/#section=code for the requirements
 */
class TechempowerBenchmarksSpec extends Specification {

  def aut = new LocalScriptApplicationUnderTest()
  @Delegate TestHttpClient client = TestHttpClients.testHttpClient(aut)

  def "json test type fulfills requirements"() {
    when:
    get("json")

    then:
    def responseStr = response.asString()
    responseStr == "{\"${Helper.MESSAGE_KEY}\":\"${Helper.MESSAGE_VALUE}\"}"
    assertResponseHeaders(response, 'application/json', responseStr.getBytes().length)
  }

  def "plaintext test type fulfills requirements"() {
    when:
    get("plaintext")

    then:
    def responseStr = response.asString()
    responseStr == Helper.MESSAGE_VALUE
    assertResponseHeaders(response, 'text/plain;charset=UTF-8', responseStr.getBytes().length)
  }

  void assertResponseHeaders(response, expectedContentType, expectedContentLength) {
    assert response.contentType == expectedContentType
    assert response.header(HttpHeaders.Names.CONTENT_LENGTH) == expectedContentLength.toString()
    assert response.header(HttpHeaders.Names.SERVER) == Helper.SERVER_NAME
    def headerDate = new SimpleDateFormat(Helper.DATE_FORMAT, Locale.UK).parse( response.header(HttpHeaders.Names.DATE) )
    def now = new Date()
    assert now.time - headerDate.time < 2000
  }
}