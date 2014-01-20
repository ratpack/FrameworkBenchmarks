package ratpack.benchmarks.techempower.test

import com.jayway.restassured.response.Response
import io.netty.handler.codec.http.HttpHeaders
import ratpack.benchmarks.techempower.common.ResponseData
import ratpack.groovy.test.TestHttpClient
import ratpack.groovy.test.TestHttpClients
import ratpack.test.ServerBackedApplicationUnderTest
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.text.SimpleDateFormat

abstract class TechempowerBenchmarkSpec extends Specification {

  private final static DATE_FORMAT = 'EEE, dd MMM yyyy HH:mm:ss z'

  @AutoCleanup ServerBackedApplicationUnderTest aut = createApplicationUnderTest()
  @Delegate TestHttpClient client = TestHttpClients.testHttpClient(aut)

  abstract ServerBackedApplicationUnderTest createApplicationUnderTest()

  def "json test type fulfills requirements"() {
    when:
    get("json")

    then:
    def responseStr = response.asString()
    responseStr == """{"${ResponseData.MESSAGE_KEY}":"${ResponseData.MESSAGE_VALUE}"}"""
    assertResponseHeaders(response, 'application/json', responseStr, testStartDate)

    where:
    testStartDate = new Date()
  }

  def "plaintext test type fulfills requirements"() {
    when:
    get("plaintext")

    then:
    def responseStr = response.asString()
    responseStr == ResponseData.MESSAGE_VALUE
    assertResponseHeaders(response, 'text/plain;charset=UTF-8', responseStr, testStartDate)

    where:
    testStartDate = new Date()
  }

  void assertResponseHeaders(Response response, String expectedContentType, String responseText, Date testStartTime) {
    assert response.contentType == expectedContentType
    assert response.header(HttpHeaders.Names.CONTENT_LENGTH) == responseText.getBytes().length.toString()
    assert response.header(HttpHeaders.Names.SERVER) == ResponseData.SERVER_NAME.toString()
    def headerDate = new SimpleDateFormat(DATE_FORMAT).parse(response.header(HttpHeaders.Names.DATE))
    assert testStartTime.time.intdiv(1000) <= headerDate.time.intdiv(1000)
    assert headerDate <= new Date()
  }
}
