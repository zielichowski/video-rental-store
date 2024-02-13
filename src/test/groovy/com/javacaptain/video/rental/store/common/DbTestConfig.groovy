package com.javacaptain.video.rental.store.common

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared

class DbTestConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Shared
    PostgreSQLContainer instance = new PostgreSQLContainer(DockerImageName.parse("postgres:14"))
            .withDatabaseName("foo")
            .withUsername("foo")
            .withPassword("secret")

    @Override
    void initialize(ConfigurableApplicationContext applicationContext) {
        instance.start()
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                "spring.datasource.url=jdbc:postgresql://${instance.host}:${instance.firstMappedPort}/",
                "spring.datasource.username=${instance.username}",
                "spring.datasource.password=${instance.password}"
        )
    }
}