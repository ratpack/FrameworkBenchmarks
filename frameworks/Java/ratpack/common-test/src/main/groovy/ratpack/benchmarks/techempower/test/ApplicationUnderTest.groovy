package ratpack.benchmarks.techempower.test

import com.zaxxer.hikari.HikariConfig
import ratpack.registry.Registry
import ratpack.remote.RemoteControl
import ratpack.test.MainClassApplicationUnderTest


public class ApplicationUnderTest extends MainClassApplicationUnderTest {

    ApplicationUnderTest(Class<?> mainClass) {
        super(mainClass)
    }

    @Override
    protected Registry createOverrides(Registry serverRegistry) throws Exception {
        HikariConfig hikari = serverRegistry.get(HikariConfig)
        hikari.setDataSourceClassName('org.h2.jdbcx.JdbcDataSource')
        hikari.addDataSourceProperty('URL', 'jdbc:h2:mem:dev')
        return Registry.of { r ->
            r.add(hikari)
            r.add(RemoteControl.handlerDecorator())
        }
    }
}
