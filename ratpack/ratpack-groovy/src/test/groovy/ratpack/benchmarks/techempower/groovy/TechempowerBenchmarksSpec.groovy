package ratpack.benchmarks.techempower.groovy

import com.jayway.restassured.response.Response
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
    responseStr == """{"${Helper.MESSAGE_KEY}":"${Helper.MESSAGE_VALUE}"}"""
    assertResponseHeaders(response, 'application/json', responseStr, testStartDate)

    where:
    testStartDate = new Date()
  }

  def "plaintext test type fulfills requirements"() {
    when:
    get("plaintext")

    then:
    def responseStr = response.asString()
    responseStr == Helper.MESSAGE_VALUE
    assertResponseHeaders(response, 'text/plain;charset=UTF-8', responseStr, testStartDate)

    where:
    testStartDate = new Date()
  }

  void assertResponseHeaders(Response response, String expectedContentType, String responseText, Date testStartTime) {
    assert response.contentType == expectedContentType
    assert response.header(HttpHeaders.Names.CONTENT_LENGTH) == responseText.getBytes().length.toString()
    assert response.header(HttpHeaders.Names.SERVER) == Helper.SERVER_NAME
    def headerDate = new SimpleDateFormat(Helper.DATE_FORMAT, Locale.UK).parse(response.header(HttpHeaders.Names.DATE))
    assert testStartTime.time.intdiv(1000) <= headerDate.time.intdiv(1000)
    assert headerDate <= new Date()
  }
}