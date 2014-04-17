import io.netty.handler.codec.http.HttpHeaders
import ratpack.benchmarks.techempower.groovy.DataAccessModule
import ratpack.benchmarks.techempower.groovy.FortuneService
import ratpack.benchmarks.techempower.groovy.QueryCountAcceptingBackgroundHandler
import ratpack.benchmarks.techempower.groovy.WorldService
import ratpack.groovy.templating.TemplatingModule
import ratpack.hikari.HikariModule
import ratpack.jackson.JacksonModule
import ratpack.remote.RemoteControlModule

import static ratpack.benchmarks.techempower.common.ResponseData.*
import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

  modules {
    register new HikariModule()
    register new JacksonModule()
    register new DataAccessModule()
    register new RemoteControlModule()
    bind(WorldService)
    bind(FortuneService)
    get(TemplatingModule).staticallyCompile = true
    get(JacksonModule).noPrettyPrint()
  }

  handlers {
    handler {
      response.headers.set(HttpHeaders.Names.DATE, new Date())
      response.headers.set(HttpHeaders.Names.SERVER, SERVER_NAME)
      next()
    }

    // Test type 1: JSON serialization
    get("json") {
      render json((MESSAGE_KEY): MESSAGE_VALUE)
    }

    // Test type 2: Single database query
    get("db") { WorldService worldService ->
      blocking {
        worldService.findByRandomId()
      } then {
        render json(it)
      }
    }

    // Test type 3: Multiple database queries
    get("queries", new QueryCountAcceptingBackgroundHandler({ WorldService worldService, int queryCount ->
      worldService.findByRandomIdMulti(queryCount)
    }))

    // Test type 4: Fortunes
    get("fortunes") { FortuneService fortuneService ->
      blocking {
        fortuneService.allPlusOne()
      } then {
        render groovyTemplate("fortunes.html", fortunes: it)
      }
    }

    // Test type 5: Database updates
    get("updates",new QueryCountAcceptingBackgroundHandler({ WorldService worldService, int queryCount ->
      worldService.updateByRandomIdMulti(queryCount)
    }))

    // Test type 6: Plaintext
    get("plaintext") {
      // using response.send() directly, by-passing any render() overhead
      response.send MESSAGE_VALUE
    }
  }

}