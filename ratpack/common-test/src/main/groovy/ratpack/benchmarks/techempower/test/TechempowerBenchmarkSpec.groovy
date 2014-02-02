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
    def responseClone = cloneResponse(response, responseBody)
    assertResponseHeaders(responseClone, 'application/json')
  }

  def "plaintext test type fulfills requirements"() {
    when:
    get("plaintext")

    then:
    def responseBody = response.asString()
    responseBody == MESSAGE_VALUE
    def responseClone = cloneResponse(response, responseBody)
    assertResponseHeaders(responseClone, 'text/plain;charset=UTF-8')
  }

  void assertResponseHeaders(Response response, String expectedContentType) {
    assert response.contentType == expectedContentType
    assert response.header(HttpHeaders.Names.CONTENT_LENGTH) == response.asByteArray().length.toString()
    assert response.header(HttpHeaders.Names.SERVER) == SERVER_NAME.toString()
    def headerDate = new SimpleDateFormat(DATE_FORMAT).parse(response.header(HttpHeaders.Names.DATE))
    assert headerDate <= new Date()
  }

  /**
   * Workaround to avoid {@code java.io.IOException: Attempted read on closed stream}.
   * <p>
   * Calling {@link com.jayway.restassured.internal.RestAssuredResponseImpl#asString()}
   * after having called {@link com.jayway.restassured.internal.RestAssuredResponseImpl#jsonPath()}
   * (or calling {@link com.jayway.restassured.internal.RestAssuredResponseImpl#asString()}
   * or {@link com.jayway.restassured.internal.RestAssuredResponseImpl#asByteArray()} twice)
   * will attempt a read on an {@code InputStream} which is already closed from the first call.
   * <p>
   * The workaround is to clone the response and issue the second call to the clone rather than
   * to the original response object.
   */
  Response cloneResponse(Response response, String responseBody) {
    final Response clone = new ResponseBuilder().clone(response).setBody(responseBody).build()
    clone.hasExpectations = true
    return clone
  }
}
