package ca.corefacility.bioinformatics.irida.config.data;

import java.util.Properties;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.StringUtils;

@Configuration
@Profile({ "dev", "prod", "it" })
public class IridaApiJdbcDataSourceConfig implements DataConfig {

	@Autowired
	Environment environment;

	private static final Logger logger = LoggerFactory.getLogger(IridaApiJdbcDataSourceConfig.class);

	private static final String HIBERNATE_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
	private static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

	/**
	 * Create an instance of {@link SpringLiquibase} to update the database
	 * schema with liquibase change sets. This bean should only be invoked in a
	 * production/dev environment and should *not* be invoked if Hibernate is
	 * going to be creating the database schema. The scenario should not come
	 * up, however we will test to see if Hibernate is set to generate a schema
	 * before executing.
	 * 
	 * @param dataSource
	 *            the connection to use to migrate the database
	 * @return an instance of {@link SpringLiquibase}.
	 */
	@Bean
	// don't bother for integration tests.
	@Profile({ "dev", "prod" })
	public SpringLiquibase springLiquibase(final DataSource dataSource) {
		final SpringLiquibase springLiquibase = new SpringLiquibase();
		springLiquibase.setDataSource(dataSource);
		springLiquibase.setChangeLog("classpath:ca/corefacility/bioinformatics/irida/database/all-changes.xml");

		// confirm that hibernate isn't also scheduled to execute
		final String importFiles = environment.getProperty(HIBERNATE_IMPORT_FILES);
		final String hbm2ddlAuto = environment.getProperty(HIBERNATE_HBM2DDL_AUTO);
		Boolean liquibaseShouldRun = environment.getProperty("liquibase.update.database.schema", Boolean.class);

		if (!StringUtils.isEmpty(importFiles) || !StringUtils.isEmpty(hbm2ddlAuto)) {
			if (liquibaseShouldRun) {
				// log that we're disabling liquibase regardless of what was
				// requested in irida.conf
				logger.warn("**** DISABLING LIQUIBASE ****: You have configured liquibase to execute a schema update, but Hibernate is also configured to create the schema.");
				logger.warn("**** DISABLING LIQUIBASE ****: " + HIBERNATE_HBM2DDL_AUTO
						+ "should be set to an empty string (or not set), but is currently set to: [" + hbm2ddlAuto
						+ "]");
				logger.warn("**** DISABLING LIQUIBASE ****: " + HIBERNATE_IMPORT_FILES
						+ " should be set to an empty string (or not set), but is currently set to: [" + importFiles
						+ "]");
			}
			liquibaseShouldRun = Boolean.FALSE;
		}

		springLiquibase.setShouldRun(liquibaseShouldRun);
		springLiquibase.setIgnoreClasspathPrefix(true);

		return springLiquibase;
	}

	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(environment.getProperty("jdbc.driver"));
		basicDataSource.setUrl(environment.getProperty("jdbc.url"));
		basicDataSource.setUsername(environment.getProperty("jdbc.username"));
		basicDataSource.setPassword(environment.getProperty("jdbc.password"));
		basicDataSource.setInitialSize(environment.getProperty("jdbc.pool.initialSize", Integer.class));
		basicDataSource.setMaxTotal(environment.getProperty("jdbc.pool.maxActive", Integer.class));
		basicDataSource.setMaxWaitMillis(environment.getProperty("jdbc.pool.maxWait", Long.class));
		basicDataSource.setTestOnBorrow(environment.getProperty("jdbc.pool.testOnBorrow", Boolean.class));
		basicDataSource.setTestOnReturn(environment.getProperty("jdbc.pool.testOnReturn", Boolean.class));
		basicDataSource.setTestWhileIdle(environment.getProperty("jdbc.pool.testWhileIdle", Boolean.class));
		basicDataSource.setValidationQuery(environment.getProperty("jdbc.pool.validationQuery"));

		return basicDataSource;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(true);
		adapter.setGenerateDdl(true);
		adapter.setDatabase(Database.MYSQL);
		return adapter;
	}

	@Bean
	public Properties getJpaProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		properties.setProperty(HIBERNATE_HBM2DDL_AUTO, environment.getProperty(HIBERNATE_HBM2DDL_AUTO));

		// if import_files is empty it tries to load any properties file it can
		// find. Stopping this here.
		String importFiles = environment.getProperty(HIBERNATE_IMPORT_FILES);

		if (!StringUtils.isEmpty(importFiles)) {
			properties.setProperty(HIBERNATE_IMPORT_FILES, importFiles);
		}

		properties.setProperty("org.hibernate.envers.store_data_at_delete",
				environment.getProperty("org.hibernate.envers.store_data_at_delete"));
		properties.setProperty("show_sql", "false");
		return properties;
	}
}
