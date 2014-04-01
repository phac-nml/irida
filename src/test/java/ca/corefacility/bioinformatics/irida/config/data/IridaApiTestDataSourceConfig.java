package ca.corefacility.bioinformatics.irida.config.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.SequenceFileFilesystemImpl;
import ca.corefacility.bioinformatics.irida.utils.RecursiveDeleteVisitor;

@Configuration
@Profile("test")
public class IridaApiTestDataSourceConfig implements DataConfig {

	private Path baseDirectory;

	// Franklin: I assume that the scope of a configuration bean is the lifetime
	// of the application, so the directory should only get deleted *after* the
	// tests have finished running.
	@PreDestroy
	public void tearDown() throws IOException {
		Files.walkFileTree(baseDirectory(), new RecursiveDeleteVisitor());
	}

	@Bean
	public SequenceFileFilesystem sequenceFileFilesystem() throws IOException {
		return new SequenceFileFilesystemImpl(baseDirectory());
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
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
	}

	@Override
	@Bean
	public Properties getJpaProperties() {
		return new Properties();
	}

	@Bean(name = "baseDirectory")
	public Path baseDirectory() throws IOException {
		baseDirectory = Files.createTempDirectory("irida-test-dir");
		return baseDirectory;
	}
}
