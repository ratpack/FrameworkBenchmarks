import io.netty.handler.codec.http.HttpHeaders
import ratpack.benchmarks.techempower.common.HikariCPModule
import ratpack.benchmarks.techempower.common.ResponseData
import ratpack.benchmarks.techempower.groovy.DataAccessModule
import ratpack.benchmarks.techempower.groovy.WorldService
import ratpack.groovy.sql.SqlModule
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

  modules {
    register new HikariCPModule("hikaricp.properties")
    register new SqlModule()
    register new JacksonModule()
    register new DataAccessModule()
  }

  handlers {
    handler {
      response.headers.set(HttpHeaders.Names.DATE, new Date())
      response.headers.set(HttpHeaders.Names.SERVER, ResponseData.SERVER_NAME)
      next()
    }

    // Test type 1: JSON serialization
    get("json") {
      render json((ResponseData.MESSAGE_KEY): ResponseData.MESSAGE_VALUE)
    }

    // Test type 2: Single database query
    get("db") { WorldService ws ->
      background {
        ws.findByRandomId()
      } then {
        render json(it)
      }
    }

    // Test type 6: Plaintext
    get("plaintext") {
      // using response.send() directly, by-passing any render() overhead
      response.send ResponseData.MESSAGE_VALUE
    }
  }

}