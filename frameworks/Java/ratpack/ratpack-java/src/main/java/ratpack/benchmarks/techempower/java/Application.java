package ratpack.benchmarks.techempower.java;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import org.skife.jdbi.v2.DBI;
import ratpack.groovy.Groovy;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.handling.Context;
import ratpack.hikari.HikariModule;
import ratpack.http.MutableHeaders;
import ratpack.http.Response;
import ratpack.jackson.Jackson;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

import javax.sql.DataSource;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ratpack.benchmarks.techempower.common.ResponseData.*;

public class Application {

  public static void main(String[] args) throws Exception {
    RatpackServer.start(s -> {
      s.serverConfig(c -> {
        c.baseDir(BaseDir.find());
        c.props("ratpack.properties");
        c.sysProps();
        c.require("/hikari", HikariConfig.class);
        c.require("/template", TextTemplateModule.Config.class);
      });
      s.registry(Guice.registry(b -> {
        b
          .module(new HikariModule())
          .module(new ApplicationModule())
          .module(new TextTemplateModule())
          .add(new ObjectMapper().writer(new MinimalPrettyPrinter()));
      }));
      s.handlers(chain ->
        chain
          .all(ctx -> {
            MutableHeaders headers = ctx.getResponse().getHeaders();
            headers.set(DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
            headers.set(SERVER, SERVER_NAME);
            ctx.next();
          })
          // Test type 1: JSON serialization
          .get("json", ctx -> ctx.render(Jackson.json(Collections.singletonMap(MESSAGE_KEY, MESSAGE_VALUE))))
          // Test type 2: Single database query
          .get("db", ctx -> ctx.get(WorldService.class).findByRandomId().then(world -> ctx.render(Jackson.json(world))))
          // Test type 3: Multiple database queries
          .get("queries", ctx -> ctx.get(WorldService.class).findByRandomIdMulti(getQueryCount(ctx)).then(worlds -> ctx.render(Jackson.json(worlds))))
          // Test type 4: Fortunes
          .get("fortunes", ctx -> ctx.get(FortuneService.class).allPlusOne().then(fortunes -> ctx.render(Groovy.groovyTemplate(ImmutableMap.of("fortunes", fortunes), "fortunes.html", "text/html;charset=UTF-8"))))
          // Test type 5: Database updates
          .get("updates", ctx -> ctx.get(WorldService.class).updateByRandomIdMulti(getQueryCount(ctx)).then(worlds -> ctx.render(Jackson.json(worlds))))
          // Test type 6: Plaintext
          .get("plaintext", ctx -> {
            Response response = ctx.getResponse();
            response.getHeaders().set(CONTENT_TYPE, TEXT_PLAIN);
            response.send(MESSAGE_VALUE_BUFFER.duplicate());
          })
      );
    });
  }

  private static int getQueryCount(Context context) {
    int count = 1;
    try {
      count = Integer.parseInt(context.getRequest().getQueryParams().get("queries"));
      if (count < 1) {
        count = 1;
      }
      if (count > 500) {
        count = 500;
      }
    } catch (NumberFormatException e) {
      // ignore
    }
    return count;
  }

  private static class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    DBI dbi(DataSource dataSource) {
      return new DBI(dataSource);
    }

    @Provides
    @Singleton
    WorldService worldService(DBI dbi) {
      return new WorldService(dbi);
    }

    @Provides
    @Singleton
    FortuneService fortuneService(DBI dbi) {
      return new FortuneService(dbi);
    }

  }
}
