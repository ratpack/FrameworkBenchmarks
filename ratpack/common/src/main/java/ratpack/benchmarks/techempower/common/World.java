package ratpack.benchmarks.techempower.common;

import java.util.concurrent.ThreadLocalRandom;

public class World {

  private int id;
  private int randomNumber;

  public World(int id, int randomNumber) {
    this.id = id;
    this.randomNumber = randomNumber;
  }

  public int getId() {
    return id;
  }

  public int getRandomNumber() {
    return randomNumber;
  }

  static int randomId() {
    // World ids range from 1 to 10000
    return 1 + ThreadLocalRandom.current().nextInt(10000);
  }

}