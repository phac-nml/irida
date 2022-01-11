package ca.corefacility.bioinformatics.irida.config.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration class for loading properties files. This configuration source
 * looks in three places for properties:
 *
 * <ol>
 * <li>within the package at jdbc.dev.properties,</li>
 * <li>within the package at filesystem.properties, and</li>
 * <li>on the filesystem at /etc/irida/irida.conf</li>
 * </ol>
 */
@Configuration
@PropertySource(value = {
		"classpath:/ca/corefacility/bioinformatics/irida/config/jdbc.${irida.db.profile:dev}.properties"
	}, ignoreResourceNotFound = true)
public class IridaApiPropertyPlaceholderConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
