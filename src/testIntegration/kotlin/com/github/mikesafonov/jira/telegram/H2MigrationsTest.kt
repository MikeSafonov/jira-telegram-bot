package com.github.mikesafonov.jira.telegram

import org.junit.Test
import org.junit.runner.RunWith
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
@ContextConfiguration(initializers = arrayOf(H2MigrationsTest.Initializer::class))
class H2MigrationsTest {

    @Test
    fun contextLoad() {
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of("spring.flyway.locations=classpath:db/migration/h2")
                .applyTo(configurableApplicationContext.environment);
        }

    }
}