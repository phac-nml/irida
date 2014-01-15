package ca.corefacility.bioinformatics.irida.config.data;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@Profile({ "dev", "prod" })
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
		basicDataSource.setMaxActive(Integer.valueOf(environment.getProperty("jdbc.pool.maxActive")));
		basicDataSource.setMaxWait(Integer.valueOf(environment.getProperty("jdbc.pool.maxWait")));
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
		properties.setProperty("hibernate.hbm2ddl.import_files",
				environment.getProperty("hibernate.hbm2ddl.import_files"));
		properties.setProperty("org.hibernate.envers.store_data_at_delete",
				environment.getProperty("org.hibernate.envers.store_data_at_delete"));
		properties.setProperty("show_sql", "false");
		return properties;
	}
}
