import com.fasterxml.jackson.core.util.MinimalPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import ratpack.benchmarks.techempower.groovy.DataAccessModule
import ratpack.benchmarks.techempower.groovy.FortuneService
import ratpack.benchmarks.techempower.groovy.WorldService
import ratpack.exec.Blocking
import ratpack.groovy.template.MarkupTemplateModule
import ratpack.handling.Context
import ratpack.hikari.HikariModule

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import static ratpack.benchmarks.techempower.common.ResponseData.*
import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

  serverConfig {
    props('ratpack.properties')
    sysProps()
    require('/hikari', HikariConfig)
    require('/template', MarkupTemplateModule.Config)
  }

  bindings {
    module HikariModule
    module DataAccessModule
    module MarkupTemplateModule
    bind WorldService
    bind FortuneService
    bindInstance new ObjectMapper().writer(new MinimalPrettyPrinter())
  }

  handlers {
    all {
      response.headers.set(HttpHeaderNames.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()))
      response.headers.set(HttpHeaderNames.SERVER, SERVER_NAME)
      next()
    }

    // Test type 1: JSON serialization
    get("json") {
      render json((MESSAGE_KEY): MESSAGE_VALUE)
    }

    // Test type 2: Single database query
    get("db") { WorldService worldService ->
      Blocking.get {
        worldService.findByRandomId()
      } then {
        render json(it)
      }
    }

    // Test type 3: Multiple database queries
    get("queries") { WorldService worldService ->
      render(json(worldService.findByRandomIdMulti(queriesParam(context))))
    }

    // Test type 4: Fortunes
    get("fortunes") { FortuneService fortuneService ->
      Blocking.get {
        fortuneService.allPlusOne()
      } then {
        render groovyMarkupTemplate(fortunes: it, "fortunes.gtpl")
      }
    }

    // Test type 5: Database updates
    get("updates") { WorldService worldService ->
      render(json(worldService.updateByRandomIdMulti(queriesParam(context))))
    }

    // Test type 6: Plaintext
    get("plaintext") {
      response.contentType(HttpHeaderValues.TEXT_PLAIN).send(MESSAGE_VALUE)
    }
  }

}

static int queriesParam(Context ctx) {
  def param = ctx.request.queryParams.queries
  if (param) {
    try {
      int count = Integer.parseInt(param)
      Math.max(Math.min(count, 500), 1)
    } catch (NumberFormatException ignore) {
      1
    }
  } else {
    1
  }
}