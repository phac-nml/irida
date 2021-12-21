package ca.corefacility.bioinformatics.irida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;

/**
 * Entry point to Spring Boot application
 */
@SpringBootApplication(
	exclude = {
		ThymeleafAutoConfiguration.class,
		LiquibaseAutoConfiguration.class,
	}
)
@Import(IridaApiServicesConfig.class)
public class IridaApplication {

	@Bean
	public ErrorPageRegistrar errorPageRegistrar() {
		return registry -> registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
	}

	public static void main(String[] args) {
		SpringApplication.run(IridaApplication.class, args);
	}

}
