package ca.corefacility.bioinformatics.irida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;

@SpringBootApplication(
	exclude = {
		ThymeleafAutoConfiguration.class,
		LiquibaseAutoConfiguration.class,
	}
)
@EntityScan(basePackages = {
	"ca.corefacility.bioinformatics.irida.model",
	"ca.corefacility.bioinformatics.irida.repositories.relational.auditing"
})
public class IridaApplication {

	public static void main(String[] args) {
		SpringApplication.run(IridaApplication.class, args);
	}

}
