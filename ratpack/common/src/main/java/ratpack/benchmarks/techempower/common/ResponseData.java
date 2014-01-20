package ratpack.benchmarks.techempower.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;

public class ResponseData {
  public final static CharSequence TEXT_PLAIN = HttpHeaders.newEntity("text/plain");
  public final static CharSequence APPLICATION_JSON = HttpHeaders.newEntity("application/json");
  public final static CharSequence DATE = HttpHeaders.newEntity(HttpHeaders.Names.DATE);
  public final static CharSequence SERVER = HttpHeaders.newEntity(HttpHeaders.Names.SERVER);
  public final static CharSequence SERVER_NAME = HttpHeaders.newEntity("Ratpack");
  public final static CharSequence CONTENT_TYPE = HttpHeaders.newEntity(HttpHeaders.Names.CONTENT_TYPE);
  public final static String MESSAGE_KEY = "message";
  public final static String MESSAGE_VALUE = "Hello, World!";
  public final static ByteBuf MESSAGE_VALUE_BUFFER = Unpooled.directBuffer().writeBytes(MESSAGE_VALUE.getBytes(CharsetUtil.UTF_8));
}
