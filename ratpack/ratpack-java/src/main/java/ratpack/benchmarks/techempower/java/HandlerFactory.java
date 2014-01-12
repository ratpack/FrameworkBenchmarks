package ratpack.benchmarks.techempower.java;

import ratpack.handling.Handler;
import ratpack.launch.LaunchConfig;

import static ratpack.guice.Guice.handler;

public class HandlerFactory implements ratpack.launch.HandlerFactory {

  public Handler create(LaunchConfig launchConfig) throws Exception {
    return handler(launchConfig, new ModuleBootstrap(), new HandlerBootstrap());
  }
}
