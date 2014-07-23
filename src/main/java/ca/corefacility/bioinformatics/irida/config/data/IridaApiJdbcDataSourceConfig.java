package ca.corefacility.bioinformatics.irida.config.data;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
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

	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(environment.getProperty("jdbc.driver"));
		basicDataSource.setUrl(environment.getProperty("jdbc.url"));
		basicDataSource.setUsername(environment.getProperty("jdbc.username"));
		basicDataSource.setPassword(environment.getProperty("jdbc.password"));
		basicDataSource.setInitialSize(Integer.valueOf(environment.getProperty("jdbc.pool.initialSize")));
		basicDataSource.setMaxTotal(Integer.valueOf(environment.getProperty("jdbc.pool.maxActive")));
		basicDataSource.setMaxWaitMillis(Integer.valueOf(environment.getProperty("jdbc.pool.maxWait")));
		basicDataSource.setTestOnBorrow(Boolean.valueOf(environment.getProperty("jdbc.pool.testOnBorrow")));
		basicDataSource.setTestOnReturn(Boolean.valueOf(environment.getProperty("jdbc.pool.testOnReturn")));
		basicDataSource.setTestWhileIdle(Boolean.valueOf(environment.getProperty("jdbc.pool.testWhileIdle")));
		basicDataSource.setValidationQuery(environment.getProperty("jdbc.pool.validationQuery"));

		return basicDataSource;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(false);
		adapter.setGenerateDdl(true);
		adapter.setDatabase(Database.MYSQL);
		return adapter;
	}

	@Bean
	public Properties getJpaProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		properties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));

		// if import_files is empty it tries to load any properties file it can
		// find. Stopping this here.
		String importFiles = environment.getProperty("hibernate.hbm2ddl.import_files");

		if (!StringUtils.isEmpty(importFiles)) {
			properties.setProperty("hibernate.hbm2ddl.import_files", importFiles);
		}

		properties.setProperty("org.hibernate.envers.store_data_at_delete",
				environment.getProperty("org.hibernate.envers.store_data_at_delete"));
		properties.setProperty("show_sql", "false");
		return properties;
	}
}
