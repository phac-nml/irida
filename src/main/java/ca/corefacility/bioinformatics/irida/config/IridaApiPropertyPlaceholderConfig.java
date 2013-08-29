package ca.corefacility.bioinformatics.irida.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration class for loading properties files.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@PropertySource({
		"classpath:/ca/corefacility/bioinformatics/irida/config/jdbc.${spring.profiles.active:dev}.properties",
		"classpath:/ca/corefacility/bioinformatics/irida/config/filesystem.properties" })
public class IridaApiPropertyPlaceholderConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
