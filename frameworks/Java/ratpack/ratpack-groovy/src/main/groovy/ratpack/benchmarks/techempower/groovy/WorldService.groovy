package ratpack.benchmarks.techempower.groovy

import groovy.sql.BatchingStatementWrapper
import groovy.sql.Sql
import groovy.transform.CompileStatic
import ratpack.benchmarks.techempower.common.World

import javax.inject.Inject

@CompileStatic
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
    sql.cacheStatements {
      // seems to be a bit more efficient than
      // other groovier looping methods
      for (i in 0..<queryCount) {
        worlds[i] = findByRandomId()
      }
    }
    return worlds
  }

  World[] updateByRandomIdMulti(int queryCount) {
    World[] worlds = new World[queryCount]
    sql.cacheStatements {
      for (i in 0..<queryCount) {
        World world = findByRandomId()
        world.randomNumber = World.randomId()
        worlds[i] = world
      }
      batchUpdate(worlds)
    }
    return worlds
  }

  World find(int id) {
    def row = sql.firstRow("select * from World where id = $id")
    row ? new World((int) id, (int) row.randomNumber) : null
  }

  void batchUpdate(World[] worlds) {
    sql.withBatch { BatchingStatementWrapper statement ->
      worlds.each { World world ->
        statement.addBatch("update World set randomNumber = $world.randomNumber where id = $world.id")
      }
    }
  }

}
