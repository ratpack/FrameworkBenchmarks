import io.netty.handler.codec.http.HttpHeaders
import ratpack.benchmarks.techempower.groovy.Helper
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

  modules {
    register new JacksonModule()
  }

  handlers {
    handler {
      response.headers.set( HttpHeaders.Names.DATE, new Date() )
      response.headers.set( HttpHeaders.Names.SERVER, Helper.SERVER_NAME )
      next()
    }

    get("json") {
      render json( (Helper.MESSAGE_KEY): Helper.MESSAGE_VALUE )
    }

    get("plaintext") {
      // using response.send() directly, by-passing any render() overhead
      response.send Helper.MESSAGE_VALUE
    }
  }

}