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

  World find(int id) {
    def row = sql.firstRow("select * from World where id = $id")
    row ? new World(id, row.randomNumber) : null
  }

}
