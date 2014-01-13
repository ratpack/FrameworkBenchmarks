import io.netty.handler.codec.http.HttpHeaders
import ratpack.benchmarks.techempower.common.ResponseData
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

  modules {
    register new JacksonModule()
  }

  handlers {
    handler {
      response.headers.set(HttpHeaders.Names.DATE, new Date())
      response.headers.set(HttpHeaders.Names.SERVER, ResponseData.SERVER_NAME)
      next()
    }

    get("json") {
      render json((ResponseData.MESSAGE_KEY): ResponseData.MESSAGE_VALUE)
    }

    get("plaintext") {
      // using response.send() directly, by-passing any render() overhead
      response.send ResponseData.MESSAGE_VALUE
    }
  }

}