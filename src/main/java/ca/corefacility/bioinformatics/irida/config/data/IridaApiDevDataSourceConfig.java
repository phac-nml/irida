package ca.corefacility.bioinformatics.irida.config.data;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

@Configuration
@Profile("dev")
public class IridaApiDevDataSourceConfig implements DataConfig {
	private @Value("${jdbc.driver}")
	String driverClassName;
	private @Value("${jdbc.url}")
	String url;
	private @Value("${jdbc.username}")
	String username;
	private @Value("${jdbc.password}")
	String password;
	private @Value("${jdbc.pool.initialSize}")
	int initialSize;
	private @Value("${jdbc.pool.maxActive}")
	int maxActive;
	private @Value("${jdbc.pool.maxWait}")
	int maxWait;
	

	private @Value("${hibernate.hbm2ddl.auto}")
	String hbm2ddlAuto;
	private @Value("${hibernate.hbm2ddl.import_files}")
	String hbm2ddlImport;
	private @Value("${hibernate.dialect}")
	String hibernateDialect;

	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(driverClassName);
		basicDataSource.setUrl(url);
		basicDataSource.setUsername(username);
		basicDataSource.setPassword(password);
		basicDataSource.setInitialSize(initialSize);
		basicDataSource.setMaxActive(maxActive);
		basicDataSource.setMaxWait(maxWait);
		basicDataSource.setTestOnBorrow(true);
		basicDataSource.setTestOnReturn(true);
		basicDataSource.setTestWhileIdle(true);
		basicDataSource.setValidationQuery("select 1");

		return basicDataSource;
	}

	@Bean
	public SessionFactory sessionFactory() {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(
				dataSource());

		builder.scanPackages("ca.corefacility.bioinformatics.irida.model",
				"ca.corefacility.bioinformatics.irida.repositories.relational.auditing");
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", false);
		properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
		properties.put("hibernate.hbm2ddl.import_files", hbm2ddlImport);
		properties.put("hibernate.dialect", hibernateDialect);
		builder.addProperties(properties);

		return builder.buildSessionFactory();
	}
}
