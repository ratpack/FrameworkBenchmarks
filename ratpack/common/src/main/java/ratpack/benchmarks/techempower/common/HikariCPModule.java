package ratpack.benchmarks.techempower.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HikariCPModule extends AbstractModule {

  private HikariConfig config;

  public HikariCPModule(String propertiesFileName) {
    this.config = new HikariConfig(loadProperties(propertiesFileName));
  }

  @Override
  protected void configure() {}

  @Provides
  DataSource dataSource() {
    return new HikariDataSource(config);
  }

  protected Properties loadProperties(String propertiesFileName) {
    InputStream is = this.getClass().getResourceAsStream("/" + propertiesFileName);
    Properties props = new Properties();
    try {
      props.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return props;
  }

}
