package ca.corefacility.bioinformatics.irida.config.environment;

import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * EnvironmentPostProcessor to translate deprecated properties into valid properties.
 */
public class IridaEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	/**
	 * The default order for the processor.
	 */
	public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 11; // after ConfigDataEnvironmentPostProcessor

	private final Log logger;

	public IridaEnvironmentPostProcessor(Log logger) {
		this.logger = logger;
	}
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		translateDeprecatedProperties(environment);
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private void translateDeprecatedProperties(ConfigurableEnvironment env) {
		ImmutableMap<String, String> deprecatedPropertiesMap = ImmutableMap.<String, String>builder()
			.put("jdbc.url", "spring.datasource.url")
			.put("jdbc.username", "spring.datasource.username")
			.put("jdbc.password", "spring.datasource.password")
			.put("jdbc.driver", "spring.datasource.driver-class-name")
			.put("hibernate.dialect", "spring.jpa.database-platform")
			.put("hibernate.hbm2ddl.auto", "spring.jpa.hibernate.dll-auto")
			.put("hibernate.hbm2ddl.import_files", "spring.jpa.properties.hibernate.hbm2ddl.import_files")
			.put("hibernate.show_sql", "spring.jpa.properties.hibernate.show_sql")
			.put("jdbc.pool.initialSize", "spring.datasource.dcp2.intial-size")
			.put("jdbc.pool.maxActive", "spring.datasource.dcp2.max-active")
			.put("jdbc.pool.testOnBorrow", "spring.datasource.dcp2.test-on-borrow")
			.put("jdbc.pool.testOnReturn", "spring.datasource.dcp2.test-on-return")
			.put("jdbc.pool.testWhileIdle", "spring.datasource.dcp2.test-while-idle")
			.put("jdbc.pool.validationQuery", "spring.datasource.dcp2.validation-query")
			.put("jdbc.pool.maxWait", "spring.datasource.dcp2.max-wait")
			.put("jdbc.pool.removeAbandoned", "spring.datasource.dcp2.remove-abandoned")
			.put("jdbc.pool.logAbandoned", "spring.datasource.dcp2.log-abandoned")
			.put("jdbc.pool.removeAbandonedTimeout", "spring.datasource.dcp2.remove-abandoned-timeout")
			.put("jdbc.pool.maxIdle", "spring.datasource.dcp2.max-idle")
			.build();

		MutablePropertySources propertySources = env.getPropertySources();

		Properties properties = new Properties();
		for (Map.Entry<String, String> entry : deprecatedPropertiesMap.entrySet()) {
			if (env.containsProperty(entry.getKey())) {
				this.logger.warn("Translating deprecated property " + entry.getKey() + " to " + entry.getValue());
				properties.setProperty(entry.getValue(), env.getProperty(entry.getKey()));
			}
		}

		if ( properties.size() > 0 ) {
			this.logger.warn("Adding translated deprecated properties as highest priority property source.");
			propertySources.addFirst(new PropertiesPropertySource("deprecatedProperties", properties));
		}
	}
}
