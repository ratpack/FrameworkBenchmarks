package ratpack.benchmarks.techempower.java;

import ratpack.guice.ModuleRegistry;
import ratpack.jackson.JacksonModule;
import ratpack.util.Action;

public class ModuleBootstrap implements Action<ModuleRegistry> {

  public void execute(ModuleRegistry registry) throws Exception {
    registry.register(new JacksonModule());
  }
}
