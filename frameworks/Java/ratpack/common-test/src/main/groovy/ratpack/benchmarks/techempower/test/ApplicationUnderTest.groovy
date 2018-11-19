package ratpack.benchmarks.techempower.test

import com.zaxxer.hikari.HikariConfig
import ratpack.impose.Impositions
import ratpack.impose.ImpositionsSpec
import ratpack.impose.UserRegistryImposition
import ratpack.remote.RemoteControl
import ratpack.test.MainClassApplicationUnderTest


public class ApplicationUnderTest extends MainClassApplicationUnderTest {

    ApplicationUnderTest(Class<?> mainClass) {
        super(mainClass)
    }

    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
        UserRegistryImposition.of { serverRegistry ->
            HikariConfig hikari = serverRegistry.get(HikariConfig)
            hikari.setDataSourceClassName('org.h2.jdbcx.JdbcDataSource')
            hikari.addDataSourceProperty('URL', 'jdbc:h2:mem:dev')
            return Registry.of { r ->
                r.add(hikari)
                r.add(RemoteControl.handlerDecorator())
            }
        }
    }
}
