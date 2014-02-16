package ratpack.benchmarks.techempower.common;

public class RequestData {

  public static int queryCount(String queriesParam) {
    int count = 1;
    try {
      count = Integer.parseInt(queriesParam);
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
}
