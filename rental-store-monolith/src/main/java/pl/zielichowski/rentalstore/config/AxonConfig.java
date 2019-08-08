package pl.zielichowski.rentalstore.config;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.GenericTokenTableFactory;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.jdbc.JdbcSagaStore;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@Slf4j
class AxonConfig {

    @Bean
    public TokenStore tokenStore(ConnectionProvider connectionProvider, Serializer serializer) {
        JdbcTokenStore build = JdbcTokenStore
                .builder()
                .connectionProvider(connectionProvider)
                .serializer(serializer)
                .build();

        build.createSchema(new GenericTokenTableFactory());
        return build;
    }

    @Bean
    public SagaStore sagaStore(ConnectionProvider connectionProvider, DataSource dataSource, Serializer serializer) {

        JdbcSagaStore build = JdbcSagaStore.builder()
                .connectionProvider(connectionProvider)
                .dataSource(dataSource)
                .serializer(serializer)
                .build();

        try {
            build.createSchema();
        } catch (SQLException e) {
            log.warn(e.getMessage());
        }

        return build;

    }
}
