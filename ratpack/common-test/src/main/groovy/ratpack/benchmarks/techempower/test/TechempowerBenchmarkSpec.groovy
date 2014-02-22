package ratpack.benchmarks.techempower.test

import com.jayway.restassured.builder.ResponseBuilder
import com.jayway.restassured.response.Response
import io.netty.handler.codec.http.HttpHeaders
import ratpack.groovy.test.TestHttpClient
import ratpack.groovy.test.TestHttpClients
import ratpack.test.ServerBackedApplicationUnderTest
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.text.SimpleDateFormat

import static ratpack.benchmarks.techempower.common.ResponseData.*

abstract class TechempowerBenchmarkSpec extends Specification {

  private final static DATE_FORMAT = 'EEE, dd MMM yyyy HH:mm:ss z'

  @AutoCleanup
  @Shared
  ServerBackedApplicationUnderTest aut = createApplicationUnderTest()
  @Delegate
  TestHttpClient client = TestHttpClients.testHttpClient(aut)

  abstract ServerBackedApplicationUnderTest createApplicationUnderTest()

  def "json test type fulfills requirements"() {
    when:
    get("json")

    then:
    def responseBody = response.asString()
    responseBody == """{"${MESSAGE_KEY}":"${MESSAGE_VALUE}"}"""
    assertResponseHeaders(response, 'application/json', responseBody)
  }

  def "plaintext test type fulfills requirements"() {
    when:
    get("plaintext")

    then:
    def responseBody = response.asString()
    responseBody == MESSAGE_VALUE
    assertResponseHeaders(response, 'text/plain;charset=UTF-8', responseBody)
  }

  void assertResponseHeaders(Response response, String expectedContentType, String responseText) {
    assert response.contentType == expectedContentType
    assert response.header(HttpHeaders.Names.CONTENT_LENGTH) == responseText.bytes.length.toString()
    assert response.header(HttpHeaders.Names.SERVER) == SERVER_NAME.toString()
    def headerDate = new SimpleDateFormat(DATE_FORMAT).parse(response.header(HttpHeaders.Names.DATE))
    assert headerDate <= new Date()
  }
}
