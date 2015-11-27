package ratpack.benchmarks.techempower.java;

import com.google.common.collect.Lists;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import ratpack.benchmarks.techempower.common.World;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.func.Function;

import javax.inject.Inject;
import java.util.List;

public class WorldService {

  private final DBI dbi;

  private static final ResultSetMapper<World> WORLD_MAPPER = (index, r, ctx) -> new World(r.getInt(1), r.getInt(2));

  @Inject
  public WorldService(DBI dbi) {
    this.dbi = dbi;
  }

  public Promise<World> findByRandomId() {
    return operation(handle -> find(handle, World.randomId()));
  }

  public Promise<List<World>> findByRandomIdMulti(final int queryCount) {
    final List<World> worlds = Lists.newArrayListWithCapacity(queryCount);
    return operation(handle -> {
      for (int i = 0; i < queryCount; i++) {
        worlds.add(i, find(handle, World.randomId()));
      }
      return worlds;
    });
  }

  public Promise<List<World>> updateByRandomIdMulti(final int queryCount) {
    final List<World> worlds = Lists.newArrayListWithCapacity(queryCount);
    return operation(handle -> {
      for (int i = 0; i < queryCount; i++) {
        World world = find(handle, World.randomId());
        world.setRandomNumber(World.randomId());
        worlds.add(i, world);
      }
      batchUpdate(handle, worlds);
      return worlds;
    });
  }

  private <T> Promise<T> operation(final Function<? super Handle, T> function) {
    return Blocking.get(() -> {
        try (Handle handle = dbi.open()) {
          return function.apply(handle);
        }
      });
  }

  private World find(Handle handle, int id) {
    return handle.createQuery("select id, randomNumber from World where id = :id")
      .bind(0, id)
      .map(WORLD_MAPPER)
      .first();
  }

  private void batchUpdate(Handle handle, Iterable<? extends World> worlds) {
    PreparedBatch preparedBatch = handle.prepareBatch("update World set randomNumber = :n where id = :i");
    for (World world : worlds) {
      preparedBatch.add(world.getRandomNumber(), world.getId());
    }
    preparedBatch.execute();
  }

}
