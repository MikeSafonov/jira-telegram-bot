package com.github.mikesafonov.jira.telegram

import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner


/**
 * @author Mike Safonov
 */
@RunWith(SpringRunner::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = arrayOf(PostgresqlMigrationsTest.Initializer::class))
class PostgresqlMigrationsTest {

    companion object{
        @ClassRule @JvmField
        var container = MyPostgreSQLContainer("postgres:latest")
    }



    @Test
    fun contextLoad(){
    }


    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of("spring.flyway.locations=classpath:db/migration/postgresql",
                "spring.datasource.driver-class-name=org.postgresql.Driver",
                "spring.datasource.url=" + PostgresqlMigrationsTest.container.jdbcUrl,
                "spring.datasource.username=" + PostgresqlMigrationsTest.container.username,
                "spring.datasource.password=" + PostgresqlMigrationsTest.container.password
            )
                .applyTo(configurableApplicationContext.environment);
        }

    }
}