package ratpack.benchmarks.techempower.java;

import io.netty.handler.codec.http.HttpHeaders;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.MutableHeaders;
import ratpack.util.Action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ratpack.jackson.Jackson.json;

public class HandlerBootstrap implements Action<Chain> {

  public void execute(Chain handlers) throws Exception {
    handlers
      .handler(new Handler() {
        public void handle(Context context) throws Exception {
          MutableHeaders headers = context.getResponse().getHeaders();
          headers.set(HttpHeaders.Names.DATE, new Date());
          headers.set(HttpHeaders.Names.SERVER, ResponseData.SERVER_NAME);
          context.next();
        }
      })
      .get("plaintext", new Handler() {
        public void handle(Context context) throws Exception {
          context.getResponse().send(ResponseData.MESSAGE_VALUE);
        }
      })
      .get("json", new Handler() {
        public void handle(Context context) throws Exception {
          Map<String, String> message = new HashMap<>();
          message.put(ResponseData.MESSAGE_KEY, ResponseData.MESSAGE_VALUE);
          context.render(json(message));
        }
      });
  }
}
