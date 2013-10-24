package ca.corefacility.bioinformatics.irida.config.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.SequenceFileFilesystemImpl;

@Configuration
@Profile("test")
public class IridaApiTestDataSourceConfig implements DataConfig {

	@Bean
	public SequenceFileFilesystem sequenceFileFilesystemRepository() {
		Path baseDirectory = Paths.get("/tmp", "sequence-files");
		if (!Files.exists(baseDirectory)) {
			try {
				Files.createDirectories(baseDirectory);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return new SequenceFileFilesystemImpl(baseDirectory);
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(false);
		adapter.setGenerateDdl(true);
		adapter.setDatabase(Database.HSQL);
		return adapter;
	}
	
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL)
				.build();
	}

	@Bean
	public SessionFactory sessionFactory() {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(
				dataSource());

		builder.scanPackages(
				"ca.corefacility.bioinformatics.irida.model",
				"ca.corefacility.bioinformatics.irida.repositories.relational.auditing",
				"ca.corefacility.bioinformatics.irida.utils.model");
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", false);
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		properties
				.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");

		builder.addProperties(properties);

		return builder.buildSessionFactory();
	}

}
