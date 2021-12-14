package ca.corefacility.bioinformatics.irida.config.data;

import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * Configuration for IRIDA's JDBC Datasource
 */
@Configuration
@EntityScan(basePackages = {
	"ca.corefacility.bioinformatics.irida.model",
	"ca.corefacility.bioinformatics.irida.repositories.relational.auditing"
})
public class IridaApiJdbcDataSourceConfig {

	@Autowired
	Environment environment;

	private static final Logger logger = LoggerFactory.getLogger(IridaApiJdbcDataSourceConfig.class);

	/**
	 * Custom implementation of the SpringLiquibase bean (for doing liquibase on spring startup) that
	 * exposes the application context so that we can have access to the application context in custom
	 * java changesets.
	 *
	 */
	public static class ApplicationContextAwareSpringLiquibase extends SpringLiquibase {
		private final ApplicationContext applicationContext;
		
		public ApplicationContextAwareSpringLiquibase(final ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		@Override
		protected SpringResourceOpener createResourceOpener() {
			return new ApplicationContextSpringResourceOpener(getChangeLog());
		}
		
		/**
		 * Custom SpringResourceOpener that gives access to the application context.
		 *
		 */
		public class ApplicationContextSpringResourceOpener extends SpringResourceOpener {
			public ApplicationContextSpringResourceOpener(final String parentFile) {
				super(parentFile);
			}
			
			public ApplicationContext getApplicationContext() {
				return ApplicationContextAwareSpringLiquibase.this.applicationContext;
			}
		}
	}

	/**
	 * Create an instance of {@link SpringLiquibase} to update the database
	 * schema with liquibase change sets. This bean should only be invoked in a
	 * production/dev environment and should *not* be invoked if Hibernate is
	 * going to be creating the database schema. The scenario should not come
	 * up, however we will test to see if Hibernate is set to generate a schema
	 * before executing.
	 *
	 * @param dataSource         the connection to use to migrate the database
	 * @param applicationContext the Spring Application Context
	 * @return an instance of {@link SpringLiquibase}.
	 */
	@Bean
	public SpringLiquibase springLiquibase(final DataSource dataSource, final ApplicationContext applicationContext) {

		final ApplicationContextAwareSpringLiquibase springLiquibase = new ApplicationContextAwareSpringLiquibase(applicationContext);
		springLiquibase.setDataSource(dataSource);
		springLiquibase.setChangeLog("classpath:ca/corefacility/bioinformatics/irida/database/all-changes.xml");

		final String importFiles = environment.getProperty(AvailableSettings.HBM2DDL_IMPORT_FILES);
		final String hbm2ddlAuto = environment.getProperty(AvailableSettings.HBM2DDL_AUTO);
		Boolean liquibaseShouldRun = environment.getProperty("liquibase.update.database.schema", Boolean.class);

		if (!StringUtils.isEmpty(importFiles) || !StringUtils.isEmpty(hbm2ddlAuto)) {
			logger.debug("Running hibernate -> not importing SQL file or running Liquibase.");
			if (liquibaseShouldRun) {
				// log that we're disabling liquibase regardless of what was
				// requested in irida.conf
				logger.warn("**** DISABLING LIQUIBASE ****: You have configured liquibase to execute a schema update, but Hibernate is also configured to create the schema.");
				logger.warn("**** DISABLING LIQUIBASE ****: " + AvailableSettings.HBM2DDL_AUTO
						+ "should be set to an empty string (or not set), but is currently set to: [" + hbm2ddlAuto
						+ "]");
				logger.warn("**** DISABLING LIQUIBASE ****: " + AvailableSettings.HBM2DDL_IMPORT_FILES
						+ " should be set to an empty string (or not set), but is currently set to: [" + importFiles
						+ "]");
			}
			liquibaseShouldRun = Boolean.FALSE;
		}

		springLiquibase.setShouldRun(liquibaseShouldRun);
		springLiquibase.setIgnoreClasspathPrefix(true);

		return springLiquibase;
	}
}
