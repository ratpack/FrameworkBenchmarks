package ratpack.benchmarks.techempower.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

public class ResponseData {
  public final static CharSequence TEXT_PLAIN = AsciiString.of("text/plain");
  public final static CharSequence APPLICATION_JSON = AsciiString.of("application/json");
  public final static CharSequence DATE = AsciiString.of(HttpHeaderNames.DATE);
  public final static CharSequence SERVER = AsciiString.of(HttpHeaderNames.SERVER);
  public final static CharSequence SERVER_NAME = AsciiString.of("Ratpack");
  public final static CharSequence CONTENT_TYPE = AsciiString.of(HttpHeaderNames.CONTENT_TYPE);
  public final static String MESSAGE_KEY = "message";
  public final static String MESSAGE_VALUE = "Hello, World!";
  public final static ByteBuf MESSAGE_VALUE_BUFFER = Unpooled.unreleasableBuffer(Unpooled.directBuffer().writeBytes(MESSAGE_VALUE.getBytes(CharsetUtil.UTF_8)));
}
