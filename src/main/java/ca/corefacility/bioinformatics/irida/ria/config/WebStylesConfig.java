package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration for Ant Design theme styles from properties file.
 */
@Configuration
@PropertySource(value = {
		"classpath:/ca/corefacility/bioinformatics/irida/config/styles.properties",
})
public class WebStylesConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
