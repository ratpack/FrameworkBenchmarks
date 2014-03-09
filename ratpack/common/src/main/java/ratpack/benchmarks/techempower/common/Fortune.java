package ratpack.benchmarks.techempower.common;

public class Fortune implements Comparable<Fortune> {

  private int id;
  private String message;
  public static final String ADDITIONAL_FORTUNE = "Additional fortune added at request time.";

  public Fortune(int id, String message) {
    this.id = id;
    this.message = message;
  }

  public int getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public int compareTo(Fortune other) {
    return getMessage().compareTo(other.message);
  }

}
