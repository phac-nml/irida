package ca.corefacility.bioinformatics.irida.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.corefacility.bioinformatics.irida.config.data.DataConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevListener;
import ca.corefacility.bioinformatics.irida.util.RecursiveDeleteVisitor;

/**
 * Configuration for repository/data storage classes.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@EnableTransactionManagement(order = 1000)
@EnableJpaRepositories(basePackages = "ca.corefacility.bioinformatics.irida.repositories", repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@Import({ IridaApiPropertyPlaceholderConfig.class, IridaApiJdbcDataSourceConfig.class })
public class IridaApiRepositoriesConfig {
	private static final Logger logger = LoggerFactory.getLogger(IridaApiRepositoriesConfig.class);
	@Autowired
	private DataConfig dataConfig;

	@Autowired
	private Environment environment;

	private @Value("${sequence.file.base.directory}") String sequenceFileBaseDirectory;

	private @Value("${reference.file.base.directory}") String referenceFileBaseDirectory;

	// test profiles are dev, test, it but not prod.
	private static final String[] TEST_PROFILES = { "dev", "test", "it", "!prod" };

	private static final Set<Path> BASE_DIRECTORIES = new HashSet<>();

	// Franklin: I assume that the scope of a configuration bean is the lifetime
	// of the application, so the directory should only get deleted *after* the
	// tests have finished running.
	@PreDestroy
	public void tearDown() throws IOException {
		for (Path b : BASE_DIRECTORIES) {
			Files.walkFileTree(b, new RecursiveDeleteVisitor());
		}
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setJpaProperties(dataConfig.getJpaProperties());
		factory.setPackagesToScan("ca.corefacility.bioinformatics.irida.model",
				"ca.corefacility.bioinformatics.irida.repositories.relational.auditing");

		return factory;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

	@Bean(name = "referenceFileBaseDirectory")
	public Path referenceFileBaseDirectory() throws IOException {
		return configureDirectory(referenceFileBaseDirectory, "reference-file-dev");
	}

	@Bean(name = "sequenceFileBaseDirectory")
	public Path sequenceFileBaseDirectory() throws IOException {
		return configureDirectory(sequenceFileBaseDirectory, "sequence-file-dev");
	}

	private Path configureDirectory(String pathName, String defaultDevPathPrefix) throws IOException {
		Path baseDirectory = Paths.get(pathName);
		if (!Files.exists(baseDirectory)) {
			if (environment.acceptsProfiles(TEST_PROFILES)) {
				baseDirectory = Files.createTempDirectory(defaultDevPathPrefix);
				BASE_DIRECTORIES.add(baseDirectory);
				logger.info(String.format(
						"The directory [%s] does not exist, but it looks like you're running in a dev environment, "
								+ "so I created a temporary location at [%s].", pathName, baseDirectory.toString()));
			} else {
				throw new IllegalStateException("Cannot continue startup; base directory [" + baseDirectory
						+ "] does not exist!");
			}
		}
		return baseDirectory;
	}

	@Bean(initMethod = "initialize")
	public RevisionListener revisionListener() {
		return new UserRevListener();
	}

	@Bean
	public AuditReader auditReader(EntityManagerFactory entityManagerFactory) {
		return AuditReaderFactory.get(entityManagerFactory.createEntityManager());
	}
}
