package ratpack.benchmarks.techempower.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaders;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.MutableHeaders;
import ratpack.http.Response;
import ratpack.launch.LaunchConfig;
import ratpack.launch.internal.DefaultLaunchConfig;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static ratpack.benchmarks.techempower.common.ResponseData.*;

public class HandlerFactory implements ratpack.launch.HandlerFactory {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
  private CharSequence date = HttpHeaders.newEntity(format.format(new Date()));

  public Handler create(LaunchConfig launchConfig) throws Exception {
    launchConfig.getForeground().getExecutor().scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        date = HttpHeaders.newEntity(format.format(new Date()));
      }
    }, 1000, 1000, TimeUnit.MILLISECONDS);

    return new Handler() {
      @Override
      public void handle(Context context) throws Exception {
        Response response = context.getResponse();
        MutableHeaders headers = response.getHeaders();

        headers.set(DATE, date);
        headers.set(SERVER, SERVER_NAME);

        String path = context.getRequest().getPath();

        switch (path) {
          case "plaintext":
            headers.set(CONTENT_TYPE, TEXT_PLAIN);
            response.send("text/plain", MESSAGE_VALUE_BUFFER.duplicate().retain());
            break;
          case "json":
            headers.set(CONTENT_TYPE, APPLICATION_JSON);
            byte[] bytes = objectMapper.writeValueAsBytes(Collections.singletonMap(MESSAGE_KEY, MESSAGE_VALUE));
            response.send(bytes);
            break;
          default:
            context.clientError(404);
            break;
        }
      }
    };
  }
}