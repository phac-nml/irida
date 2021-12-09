package ca.corefacility.bioinformatics.irida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Import;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;

@SpringBootApplication(
	exclude = {
		ThymeleafAutoConfiguration.class,
		LiquibaseAutoConfiguration.class,
	}
)
@Import(IridaApiPropertyPlaceholderConfig.class)
public class IridaApplication {

	public static void main(String[] args) {
		SpringApplication.run(IridaApplication.class, args);
	}

}
