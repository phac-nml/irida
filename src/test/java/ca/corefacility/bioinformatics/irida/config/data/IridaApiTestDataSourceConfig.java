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

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.SequenceFileFilesystemRepository;
import ca.corefacility.bioinformatics.irida.utils.repositories.IdentifiableTestEntityRepo;
import ca.corefacility.bioinformatics.irida.utils.repositories.IdentifiableTestEntityRepoImpl;

@Configuration
@Profile("test")
public class IridaApiTestDataSourceConfig implements DataConfig {

	@Bean
	public CRUDRepository<Long, SequenceFile> sequenceFileFilesystemRepository() {
		Path baseDirectory = Paths.get("/tmp", "sequence-files");
		if (!Files.exists(baseDirectory)) {
			try {
				Files.createDirectories(baseDirectory);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return new SequenceFileFilesystemRepository(baseDirectory);
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
				"ca.corefacility.bioinformatics.irida.utils");
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", false);
		properties.put("hibernate.hbm2ddl.auto", "create");
		properties
				.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");

		builder.addProperties(properties);

		return builder.buildSessionFactory();
	}

	@Bean
	public IdentifiableTestEntityRepo identifiedRepository() {
		return new IdentifiableTestEntityRepoImpl(dataSource(),
				sessionFactory());
	}
}
