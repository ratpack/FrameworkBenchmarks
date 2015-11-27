package ratpack.benchmarks.techempower.java;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import ratpack.benchmarks.techempower.common.Fortune;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static ratpack.benchmarks.techempower.common.Fortune.ADDITIONAL_FORTUNE;

public class FortuneService {

  private final DBI dbi;

  @Inject
  public FortuneService(DBI dbi) {
    this.dbi = dbi;
  }

  Promise<List<Fortune>> allPlusOne() {
    return Blocking.get(() -> {
      try (Handle handle = dbi.open()) {
        return handle.createQuery("select id, message from Fortune")
          .map((index, r, ctx) -> {
            return new Fortune(r.getInt(1), r.getString(2));
          })
          .list();
      }
    }).map(fortunes -> {
      fortunes.add(new Fortune(0, ADDITIONAL_FORTUNE));
      Collections.sort(fortunes);
      return fortunes;
    });
  }

}
