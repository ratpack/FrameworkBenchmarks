package ratpack.benchmarks.techempower.java;

import ratpack.func.Action;
import ratpack.guice.ModuleRegistry;
import ratpack.jackson.JacksonModule;

public class ModuleBootstrap implements Action<ModuleRegistry> {

  public void execute(ModuleRegistry registry) throws Exception {
    registry.register(new JacksonModule());
  }
}
