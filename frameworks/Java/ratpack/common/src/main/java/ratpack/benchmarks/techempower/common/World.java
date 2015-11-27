package ratpack.benchmarks.techempower.common;

import java.util.concurrent.ThreadLocalRandom;

public class World {

  private int id;
  private int randomNumber;
  public static final int DB_ROWS = 10000;

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

  public void setRandomNumber(int randomNumber) {
    this.randomNumber = randomNumber;
  }

  public static int randomId() {
    // World ids range from 1 to 10000
    return 1 + ThreadLocalRandom.current().nextInt(DB_ROWS);
  }

}