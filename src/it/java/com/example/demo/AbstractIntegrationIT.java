package com.example.demo;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-it.properties")
@AutoConfigureWebTestClient
public class AbstractIntegrationIT {

  private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationIT.class);

  @Configuration
  public static class InitializerConfiguration {

    @Bean
    public ConnectionFactoryInitializer initializer(final ConnectionFactory connectionFactory) {
      logger.info("init test db");

      final ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();

      initializer.setConnectionFactory(connectionFactory);

      final ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
          new ClassPathResource("schema.sql"),
          new ClassPathResource("contacts.sql"));
      initializer.setDatabasePopulator(populator);

      return initializer;
    }
  }
}
