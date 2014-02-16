package ratpack.benchmarks.techempower.groovy

import groovy.sql.Sql
import ratpack.benchmarks.techempower.common.World

import javax.inject.Inject

class WorldService {

  final Sql sql

  @Inject
  WorldService(Sql sql) {
    this.sql = sql
  }

  World findByRandomId() {
    find(World.randomId())
  }

  World[] findByRandomIdMulti(int queryCount) {
    World[] worlds = new World[queryCount]
    // seems to be a bit more efficient than
    // other groovier looping methods
    for (i in 0..queryCount-1) {
      worlds[i] = findByRandomId()
    }
    return worlds
  }

  World[] updateByRandomIdMulti(int queryCount) {
    World[] worlds = new World[queryCount]
    for (i in 0..queryCount-1) {
      World world = findByRandomId()
      world.randomNumber = World.randomId()
      worlds[i] = world
    }
    batchUpdate(worlds)
    return worlds
  }

  World find(int id) {
    def row = sql.firstRow("select * from World where id = $id")
    row ? new World(id, row.randomNumber) : null
  }

  void batchUpdate(World[] worlds) {
    sql.withBatch { stmt ->
      worlds.each {
        stmt.addBatch("update World set randomNumber = $it.randomNumber where id = $it.id")
      }
    }
  }

}
