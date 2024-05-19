package org.example.configuration;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
public class TestConfiguration {
    @Bean(initMethod = "start")
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                "postgres:14-alpine"
        )
                .withDatabaseName("app")
                .withUsername("app")
                .withPassword("secret")
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)))
                ));
        return postgres;
    }

    @Bean
    //hack to initialize postgres container before any datasource consumer beans
    @DependsOn("postgreSQLContainer")
    public DataSource dataSource() {
        return new SimpleDriverDataSource(new org.postgresql.Driver(), "jdbc:postgresql://localhost:5432/app", "app", "secret");
    }
}
