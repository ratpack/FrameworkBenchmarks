import io.netty.handler.codec.http.HttpHeaders
import ratpack.benchmarks.techempower.groovy.DataAccessModule
import ratpack.benchmarks.techempower.groovy.QueryCountAcceptingBackgroundHandler
import ratpack.benchmarks.techempower.groovy.WorldService
import ratpack.groovy.sql.SqlModule
import ratpack.hikari.HikariModule
import ratpack.jackson.JacksonModule
import ratpack.remote.RemoteControlModule

import static ratpack.benchmarks.techempower.common.ResponseData.*
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

  modules {
    register new HikariModule()
    register new SqlModule()
    register new JacksonModule()
    register new DataAccessModule()
    register new RemoteControlModule()
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
    get("db") { WorldService ws ->
      background {
        ws.findByRandomId()
      } then {
        render json(it)
      }
    }

    // Test type 3: Multiple database queries
    get("queries", new QueryCountAcceptingBackgroundHandler({ WorldService worldService, int queryCount ->
      worldService.findByRandomIdMulti(queryCount)
    }))

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