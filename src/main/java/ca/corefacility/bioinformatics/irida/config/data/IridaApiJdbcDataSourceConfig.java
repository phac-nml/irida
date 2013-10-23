package ca.corefacility.bioinformatics.irida.config.data;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

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
	@SuppressWarnings("deprecation")
	public SessionFactory sessionFactory() {
		return hibernateConfiguration().buildSessionFactory();
	}

	@Bean
	public org.hibernate.cfg.Configuration hibernateConfiguration() {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource());

		builder.scanPackages("ca.corefacility.bioinformatics.irida.model",
				"ca.corefacility.bioinformatics.irida.repositories.relational.auditing");
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", Boolean.valueOf(environment.getProperty("hibernate.show_sql")));
		properties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		properties.put("hibernate.hbm2ddl.import_files", environment.getProperty("hibernate.hbm2ddl.import_files"));
		properties.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		properties.put("org.hibernate.envers.store_data_at_delete", environment.getProperty("org.hibernate.envers.store_data_at_delete"));
		builder.addProperties(properties);
		return builder;
	}
}
