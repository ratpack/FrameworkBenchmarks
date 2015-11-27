package ratpack.benchmarks.techempower.groovy

import ratpack.groovy.Groovy
import ratpack.server.RatpackServer


class Application {

    public static void main(String... args) throws Exception {
        RatpackServer.start(Groovy.Script.app(true))
    }
}
