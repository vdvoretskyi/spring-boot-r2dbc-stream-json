package com.example.demo;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;

@SpringBootApplication
public class HelloServiceApplication {

	public static final Logger logger = LoggerFactory.getLogger(HelloServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HelloServiceApplication.class, args);
	}

	@Configuration
	public static class InitializerConfiguration {

		@Bean
		public ConnectionFactoryInitializer initializer(final ConnectionFactory connectionFactory) {

			logger.info("init database");

			final ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();

			initializer.setConnectionFactory(connectionFactory);

			final ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
					new ClassPathResource("schema.sql"),
					new ClassPathResource("data.sql"));
			initializer.setDatabasePopulator(populator);

			return initializer;
		}
	}

}
